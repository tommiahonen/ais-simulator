package se.havochvatten.unionvms;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class Worker implements Runnable {

	private Socket clientSocket;
	private AISEncoder encoder;

	public Worker(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.encoder = new AISEncoder();
	}

	@Override
	public void run() {
		try {
			PrintWriter out;
			while (true) {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				Reader in = new FileReader("aisdk_20190513.csv");
				Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
				for (CSVRecord record : records) {
					String timestamp = record.get("# Timestamp");
					LocalTime now = LocalTime.now(ZoneId.of("UTC"));
					LocalTime aisTimestamp = LocalTime.parse(timestamp.split("\\s+")[1], DateTimeFormatter.ofPattern("HH:mm:ss"));
					if (aisTimestamp.isBefore(now.minusSeconds(5))) { 
						// Do nothing
					} else if (aisTimestamp.isBefore(now)) {
						out.println(encoder.encode(record));
						if (aisTimestamp.getMinute() % 5 == 0) {
						    out.println(encoder.encodeType5(record));
						}
					} else {
						Thread.sleep(1000);
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
