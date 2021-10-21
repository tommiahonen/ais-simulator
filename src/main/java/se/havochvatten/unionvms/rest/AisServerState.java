package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Server;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.*;
import java.io.File;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Startup
@Singleton
@Path("/state")
public class AisServerState {

    private Server aisServer;
    private Thread aisServerThread;
    private String filename;

    public AisServerState() {
    }

    /**
     *  Automatically start the AIS-server once EE server has started up.
     */
    @PostConstruct
    public void automaticStartUp() {
        start();
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
        if (aisServerThread != null && aisServerThread.getState() != Thread.State.TERMINATED) {
            return true;
        } else {
            return false;
        }
    }

    @GET
    @Path("/status")
    @Consumes({ MediaType.TEXT_PLAIN })
    @Produces({ MediaType.TEXT_PLAIN })
    public String getStatus() {

        if (serverIsRunning()) {
            return "AIS-server is running.";
        } else {
            return "AIS-server is not running.";
        }
    }

    @GET
    @Path("/setFilename/{filename}")
    @Consumes({ MediaType.TEXT_PLAIN })
    @Produces({ MediaType.TEXT_PLAIN })
    public Response setFilename(@PathParam("filename") String filename) {

        File f = new File(filename);
        if(f.exists() && f.isFile()) {
            this.filename=filename;
            return Response.ok("New filename is '" + this.filename + "'.", MediaType.TEXT_PLAIN_TYPE).build();
        } else {
            return Response.status(404, "Error: no such file found.").build();
        }
    }

    @GET
    @Consumes((MediaType.TEXT_PLAIN))
    @Path("{nth}")
    public String startServerwithNthPosition(@PathParam("nth") int nth) {
        this.aisServer = new Server(nth);
        this.aisServerThread = new Thread(aisServer);
        aisServerThread.start();
        return "Started a Server with nth position " + nth;
    }
}
