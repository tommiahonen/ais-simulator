package se.havochvatten.unionvms;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class Worker implements Runnable {

    private Socket clientSocket;
    private AISEncoder encoder;
    private int nthPosition;

    private boolean terminateProcess;
    private boolean reloadFile;
    private String filename;
    private Reader in;

    public Worker(Socket clientSocket, int nthPosition, String filename) {
        this.filename = filename;
        this.clientSocket = clientSocket;
        this.encoder = new AISEncoder();
        this.nthPosition = nthPosition;
        terminateProcess = false;
    }

    @Override
    public void run() {
        try {
            PrintWriter out;
            while (!terminateProcess) {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                if (this.filename == null) {
                    throw new FileNotFoundException();
                }
                Reader in = loadDatafile();
                Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
                long pos = 0;
                for (CSVRecord record : records) {
                    if (terminateProcess || reloadFile) {
                        break;
                    }
                    String timestamp = record.get("# Timestamp");
                    LocalTime now = LocalTime.now(ZoneId.of("UTC"));
                    LocalTime aisTimestamp = LocalTime.parse(timestamp.split("\\s+")[1], DateTimeFormatter.ofPattern("HH:mm:ss"));
                    if (aisTimestamp.isBefore(now.minusSeconds(5))) {
                        // Do nothing
                    } else if (aisTimestamp.isBefore(now)) {
                        if (pos % nthPosition == 0) {
                            out.println(encoder.encode(record));
                            if (aisTimestamp.getMinute() % 5 == 0) {
                                out.println(encoder.encodeType5(record));
                            }
                        }
                    } else {
                        Thread.sleep(1000);
                    }
                    pos++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Worker is unable to read from file '" + filename + "'");
            //e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Worker has ben shut down.");
    }

    public void stop() {
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        terminateProcess = true;
    }

    /**
     * Changes the name of the CSV datafile that the AIS-server is reading from. This will cause the AIS-server to stop
     * from the current datafile it is using and start reading from this file instead.
     *
     * @param filename Name of file located on AIS-server.
     */
    public void setFilename(String filename) {
        System.out.println("Changing filename of worker to " + filename + ".");
        this.filename = filename;
        this.reloadFile = true;
    }

    /**
     * Load a new CSV datafile.
     *
     * @return A FileReader
     * @throws FileNotFoundException If file can not be loaded.
     */
    private FileReader loadDatafile() throws FileNotFoundException {
        reloadFile = false;
        return new FileReader(this.filename);
    }


}
