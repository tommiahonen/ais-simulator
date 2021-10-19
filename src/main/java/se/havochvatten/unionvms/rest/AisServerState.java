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

    private Server aisServer;
    private Thread aisServerThread;


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
        String feedback;
        if (!serverIsRunning()) {
            this.aisServer = new Server();
            this.aisServerThread = new Thread(aisServer);

            feedback = "Starting AIS-server..";
            System.out.println(feedback);
            aisServerThread.start();
        }
        else {
            feedback = "Unable to start server server since it is already running.";
        }
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
        String feedback;
        if (serverIsRunning()) {
            feedback = "Stopping AIS-server..";
            System.out.println(feedback);
            aisServer.stop();
        } else {
            feedback = "Unable to stop server server since it is already stopped.";
        }
        return feedback;
    }


    private boolean serverIsRunning() {
        //TODO: This is not working correctly: it does not detect if server is already running.
        if (aisServerThread != null && aisServerThread.getState() != Thread.State.TERMINATED) {
            return true;
        } else {
            return false;
        }
    }

    @GET
    @Path("/status")
    public String getStatus() {

        if (serverIsRunning()) {
            return "AIS-server is running.";
        } else {
            return "AIS-server is not running.";
        }
    }
}
