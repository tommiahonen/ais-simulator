package se.havochvatten.unionvms;

public class Starter {

    public static void launch() {
    	Server server = new Server();
    	System.out.println("Starting server..");
        new Thread(server).start();
    }
}