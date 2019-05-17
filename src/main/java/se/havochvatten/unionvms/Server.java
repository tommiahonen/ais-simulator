package se.havochvatten.unionvms;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(1337);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("New client connected..");
				new Thread(new Worker(clientSocket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
