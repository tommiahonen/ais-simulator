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
    private int nthPos = 1;

    public Server(int nthPos) {
        this();
        this.nthPos = nthPos;
    }

    public Server() {
        interruptRunningProcess = false;
    }

    @Override
    public void run() {
        List<Worker> workers = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(8040);
            while (!interruptRunningProcess) {
                Socket clientSocket = serverSocket.accept();
                String ais_nth_pos_str = System.getProperty("ais_nth_pos");
                if (ais_nth_pos_str != null) {
                    nthPos = Integer.parseInt(ais_nth_pos_str);
                }
                System.out.println("New client connected.. using nth_pos = " + nthPos);
                Worker worker = new Worker(clientSocket, nthPos);
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

    public void stop() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        interruptRunningProcess = true;
        System.out.println("Server.stop() called.");
    }
}
