package se.havochvatten.unionvms;

public class Starter {

    private static final Server server = new Server();
    private static final Thread thread = new Thread(server);

    public static void launch() {
    	System.out.println("Starting server..");
        thread.start();
    }

    public static void stop() {
        System.out.println("Stopping server..");
        thread.stop();
    }
}