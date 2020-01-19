package se.havochvatten.unionvms;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(8040);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				int nthPos = 1;
				String ais_nth_pos_str = System.getProperty("ais_nth_pos");
				if(ais_nth_pos_str != null) {
					nthPos = Integer.parseInt(ais_nth_pos_str);
				}
				System.out.println("New client connected.. using nth_pos = " + nthPos);
				new Thread(new Worker(clientSocket, nthPos)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
