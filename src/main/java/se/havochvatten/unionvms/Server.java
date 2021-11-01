package se.havochvatten.unionvms;

import se.havochvatten.unionvms.rest.AisServerState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private static final int PORT_NUMBER_IN = 8040;
    private boolean interruptRunningProcess;
    private boolean suspendRunningProcess;
    private ServerSocket serverSocket;
    private int nthPos;
    private String filename;
    List<Worker> workers;

    public Server(int nthPos, String filename) {
        this();
        setNthPos(nthPos);
        setFilename(filename);
    }

    public Server() {
        interruptRunningProcess = false;
        workers = new ArrayList<>();
        nthPos = 1;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(PORT_NUMBER_IN);
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
            if (interruptRunningProcess) {
                // Socket was closed manually because of server shutdown
                System.out.println("Server is stopping. Will now stop workers too...");
                for (Worker worker : workers) {
                    System.out.println("Attempting to stop a worker...");
                    worker.stop();
                }
            } else {
                // Socket was closed because of some error.
                System.out.println("Error: unable to open port " + PORT_NUMBER_IN + ".");
                System.out.println("Server is stopping.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        interruptRunningProcess = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server.shutdown() called.");
    }

    public void setFilename(String filename) {
        this.filename = filename;
        for (Worker worker : workers) {
            worker.setFilename(this.filename);
        }
    }

    public void setNthPos(int nthPos) {
        this.nthPos = nthPos;
        for (Worker worker : workers) {
            worker.setNthPos(this.nthPos);
        }
    }

    public boolean isPaused() {
        synchronized (this) {
            return suspendRunningProcess;
        }
    }

    public void pause() {
        synchronized (this) {
            suspendRunningProcess = true;
            for (Worker worker : workers) {
                worker.pause();
            }
        }
    }

    public void unpause() {
        synchronized (this) {
            suspendRunningProcess = false;
            for (Worker worker : workers) {
                worker.unpause();
            }
        }
    }
}
