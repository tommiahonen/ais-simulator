package se.havochvatten.unionvms;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

	private boolean interruptRunningProcess;
	private ServerSocket serverSocket;

	public Server() {
		interruptRunningProcess=false;
	}

	@Override
	public void run() {
		List<Worker> workers = new ArrayList<>();
		try {
			serverSocket = new ServerSocket(8040);
			while (!interruptRunningProcess) {
				Socket clientSocket = serverSocket.accept();
				int nthPos = 1;
				String ais_nth_pos_str = System.getProperty("ais_nth_pos");
				if(ais_nth_pos_str != null) {
					nthPos = Integer.parseInt(ais_nth_pos_str);
				}
				System.out.println("New client connected.. using nth_pos = " + nthPos);
				Worker worker = new Worker(clientSocket, nthPos);
				workers.add(worker);
				new Thread(worker).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Server shutting down. Will now stop workers too...");
			for (Worker worker : workers) {
				System.out.println("Attempting to stop a worker...");
				worker.stop();
			}
		}
	}

	public void stop() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		interruptRunningProcess=true;
		System.out.println("Server.stop() called.");
	}
}
