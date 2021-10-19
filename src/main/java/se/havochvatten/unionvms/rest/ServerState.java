package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Server;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Startup
@Singleton
@Path("/state")
public class ServerState {

    private final Server server;
    private Thread thread;

    public ServerState() {
        server = new Server();
        thread = new Thread(server);
    }

    @GET
    @Path("/start")
    public String start() {
        final String feedback = "Starting server..";
        System.out.println(feedback);
        thread.start();
        return feedback;
    }

    @GET
    @Path("/stop")
    public String stop() {
        final String feedback = "Stopping server..";
        System.out.println(feedback);
        thread.stop();
        return feedback;
    }
}
