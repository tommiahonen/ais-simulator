package se.havochvatten.unionvms.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
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
     * Automatically start the AIS-server once EE server has started up.
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
    @Operation(summary = "Start the AIS-server.",
            description = "Does nothing if server is already running.")
    @GET
    @Path("/start")
    @APIResponse(responseCode = "200", description = "Server was started succesfully.")
    @APIResponse(responseCode = "404", description = "Unable to start server server since it is already running.")
    public Response start() {
        String feedback;
        if (!serverIsRunning()) {
            this.aisServer = new Server();
            this.aisServerThread = new Thread(aisServer);

            feedback = "Starting AIS-server..";
            System.out.println(feedback);
            aisServerThread.start();
            return Response.ok(feedback, MediaType.TEXT_PLAIN_TYPE).build();

        } else {
            feedback = "Unable to start server server since it is already running.";
            return Response.status(404, feedback).build();
        }
    }

    /**
     * Stop the AIS-server.
     *
     * @return String indicating that AIS-server was stopped.
     */
    @Operation(summary = "Stops the AIS-server.",
            description = "Does nothing if server is already stopped.")
    @GET
    @Path("/stop")
    @APIResponse(responseCode = "200", description = "Server was stopped succesfully.")
    @APIResponse(responseCode = "200", description = "Unable to stop server server since it is already stopped.")
    public String stop() {
        String feedback;
        if (serverIsRunning()) {
            feedback = "AIS-server has been stopped";
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

    @Operation(summary = "Check is server is running or not.",
            description = "Server can either be running or completely stopped.")
    @GET
    @Path("/status")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    @APIResponse(responseCode = "200", description = "Server is running")
    @APIResponse(responseCode = "200", description = "Server is not running")
    public String getStatus() {

        if (serverIsRunning()) {
            return "AIS-server is running.";
        } else {
            return "AIS-server is not running.";
        }
    }

    @Operation(summary = "Select datafile that AIS-server reads from.",
            description = "Select a CSV datafile that AIS-server will read its data from when it is running. The file must already be located on the server.")
    @GET
    @Path("/setFilename/{filename}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    @APIResponse(responseCode = "200", description = "New filename has been set.")
    @APIResponse(responseCode = "404", description = "File not found")
    public Response setFilename(@Parameter(description = "Name of the CSV datafile.", required = true)
                                @PathParam("filename") String filename) {


        File f = new File(filename);
        if (f.exists() && f.isFile()) {
            this.filename = filename;
            return Response.ok("New filename is '" + this.filename + "'.", MediaType.TEXT_PLAIN_TYPE).build();
        } else {
            return Response.status(404, "Error: no such file found.").build();
        }
    }

    @Operation(summary = "Select new Nth value for AIS server.",
            description = "This makes the server skip some lines in the CSV datafile.")
    @GET
    @Consumes((MediaType.TEXT_PLAIN))
    @Path("{nth}")
    public String startServerwithNthPosition(@Parameter(description = "Value of Nth. Must be >= 1. ", required = true)
                                             @PathParam("nth") int nth) {
        this.aisServer = new Server(nth);
        this.aisServerThread = new Thread(aisServer);
        aisServerThread.start();
        return "Started a Server with nth position " + nth;
    }
}
