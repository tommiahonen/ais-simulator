package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Server;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Startup
@Singleton
@Path("/state")
public class AisServerState {

    private Server server;
    private Thread thread;

    public AisServerState() {
    }

    /**
     *  Automatically start the AIS-server once EE server has started up.
     */
    @PostConstruct
    public void automaticStartUp() {
        //start();
    }

    /**
     * Start the AIS-server.
     *
     * @return String indicating that AIS-server was started.
     */
    @GET
    @Path("/start")
    public String start() {
        // TODO: Is server running? And should it be stopped first before starting it?
        this.server = new Server();
        this.thread = new Thread(server);

        final String feedback = "Starting AIS-server..";
        System.out.println(feedback);
        thread.start();
        return feedback;
    }

    /**
     * Stop the AIS-server.
     *
     * @return String indicating that AIS-server was stopped.
     */
    @GET
    @Path("/stop")
    public String stop() {
        final String feedback = "Stopping AIS-server..";
        System.out.println(feedback);
        server.stop();
        return feedback;
    }
}
