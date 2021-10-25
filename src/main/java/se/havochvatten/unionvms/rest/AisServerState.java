package se.havochvatten.unionvms.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
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

@OpenAPIDefinition(
        info = @Info(
                title = "AIS-server controller",
                version = "0.1",
                description = "This is a REST API for controlling and configuring the AIS-server.",

                license = @License(
                        name = "License",
                        url = "https://focusfish.atlassian.net/wiki/spaces/UVMS/overview")
        )
)
@Startup
@Singleton
@Path("/state")
public class AisServerState {

    private Server aisServer;
    private Thread aisServerThread;
    private String filename;
    private int nth;

    public AisServerState() {
        // First time server is started, if nothing is changed before that, it will use these values.
        filename = "aisdk_20190513.csv";
        nth = 3;
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
            this.aisServer = new Server(nth, this.filename);
            this.aisServerThread = new Thread(aisServer);

            feedback = "Starting AIS-server..";
            System.out.println(feedback);
            aisServerThread.start();
            return Response.ok().entity(feedback).type(MediaType.TEXT_PLAIN).build();

        } else {
            feedback = "Unable to start server server since it is already running.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
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
    @APIResponse(responseCode = "404", description = "Unable to stop server server since it is already stopped.")
    public Response stop() {
        String feedback;
        if (serverIsRunning()) {
            feedback = "AIS-server has been stopped";
            aisServer.stop();
            return Response.ok().entity(feedback).type(MediaType.TEXT_PLAIN).build();
        } else {
            feedback = "Unable to stop server server since it is already stopped.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
        }
    }


    private boolean serverIsRunning() {
        if (aisServerThread != null && aisServerThread.getState() != Thread.State.TERMINATED) {
            return true;
        } else {
            return false;
        }
    }

    @Operation(summary = "Check if server is running or not.",
            description = "Server can either be running or completely stopped.")
    @GET
    @Path("/status")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    @APIResponse(responseCode = "200", description = "Server is running")
    @APIResponse(responseCode = "404", description = "Server is not running")
    public Response getStatus() {

        if (serverIsRunning()) {
            return Response.ok().entity("AIS-server is now running.").type(MediaType.TEXT_PLAIN).build();
        } else {
            return Response.status(404).entity("AIS-server is not running.").type(MediaType.TEXT_PLAIN).build();
        }
    }

    @Operation(summary = "Select datafile that AIS-server reads from.",
            description = "The selected file must already be located on the server.")
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
            this.aisServer.setFilename(filename);
            return Response.ok().entity("New filename is '" + this.filename + "'.").type(MediaType.TEXT_PLAIN).build();
        } else {
            this.filename = "";
            return Response.status(404).entity("Error: no such file found.").type(MediaType.TEXT_PLAIN).build();
        }
    }

    @Operation(summary = "Select new Nth value for AIS server.",
            description = "This makes the server skip some lines in the CSV datafile.")
    @GET
    @Consumes((MediaType.TEXT_PLAIN))
    @APIResponse(responseCode = "200", description = "The Nth value has been set.")
    @APIResponse(responseCode = "404", description = "User has provided an illegal value (not >=1).")
    @Path("/setnth/{nth}")
    public Response setNthValue(@Parameter(description = "Value of Nth. Must be >= 1. ", required = true)
                                @PathParam("nth") int nth) {

        if (nth < 1) {
            return Response.status(404).entity("Error: you must use a value >=1.").type(MediaType.TEXT_PLAIN).build();
        }

        // Change nth value of Server and it's Workers
        this.nth = nth;
        aisServer.setNthPos(this.nth);

        return Response.ok().entity("Nth value is now set to " + nth).type(MediaType.TEXT_PLAIN).build();
    }
}
