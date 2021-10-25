package se.havochvatten.unionvms;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private boolean interruptRunningProcess;
    private ServerSocket serverSocket;
    private int nthPos;
    private String filename;
    List<Worker> workers;

    public Server(int nthPos, String filename) {
        this();
        setNthPos(nthPos);
        this.filename = filename;
    }

    public Server() {
        interruptRunningProcess = false;
        workers = new ArrayList<>();
        nthPos = 1;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(8040);
            while (!interruptRunningProcess) {
                Socket clientSocket = serverSocket.accept();
                String ais_nth_pos_str = System.getProperty("ais_nth_pos");
                if (ais_nth_pos_str != null) {
                    nthPos = Integer.parseInt(ais_nth_pos_str);
                }
                System.out.println("New client connected.. using nth_pos = " + nthPos);
                Worker worker = new Worker(clientSocket, nthPos, filename);
                workers.add(worker);
                new Thread(worker).start();
            }
        } catch (SocketException e) {
            System.out.println("Server is stopping. Will now stop workers too...");
            for (Worker worker : workers) {
                System.out.println("Attempting to stop a worker...");
                worker.stop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        interruptRunningProcess = true;
        System.out.println("Server.shutdown() called.");
    }

    public void setFilename(String filename) {
        this.filename = filename;
        for (Worker worker : workers) {
            worker.setFilename(filename);
        }
    }

    public void setNthPos(int nthPos) {
        this.nthPos = nthPos;
        for (Worker worker : workers) {
            worker.setNthPos(this.nthPos);
        }
    }
}
