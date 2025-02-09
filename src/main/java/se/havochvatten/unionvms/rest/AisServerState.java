package se.havochvatten.unionvms.rest;

import com.sun.org.apache.bcel.internal.generic.RETURN;
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

    public final static String UPLOAD_DIRECTORY = "/tmp/uvms/"; // This directory also has to be set in web.xml file
    private Server aisServer;
    private Thread aisServerThread;
    private String filename;
    private int nth;

    public AisServerState() {
        // First time server is started, if nothing is changed before that, it will use these values.
        // filename = UPLOAD_DIRECTORY + "aisdk_20190513.csv";
        nth = 3;
    }

    /**
     * Automatically start the AIS-server once EE server has started up.
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
    @Operation(summary = "Start the AIS-server if it is not running or unpause it if it is paused.",
            description = "Does nothing if server is already running and not paused.")
    @GET
    @Path("/start")
    @APIResponse(responseCode = "200", description = "Server was started (or unpaused) succesfully.")
    @APIResponse(responseCode = "404", description = "Unable to start server server since it is already running.")
    @APIResponse(responseCode = "404", description = "Unable to start server server since no .csv datafile has been set.")
    public Response start() {
        String feedback;
        if (filename==null || filename.equals("")) {
            feedback = "Unable to launch server since no .csv datafile has been set.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
        }


        if (!serverIsRunning()) {
            this.aisServer = new Server(nth, this.filename);
            this.aisServerThread = new Thread(aisServer);

            feedback = "Launching AIS-server..";
            System.out.println(feedback);
            aisServerThread.start();
            return Response.ok().entity(feedback).type(MediaType.TEXT_PLAIN).build();

        } else {
            // Server is already running

            if (aisServer.isPaused()) {
                aisServer.unpause();
                feedback = "AIS-server has been unpaused..";
                return Response.ok().entity(feedback).type(MediaType.TEXT_PLAIN).build();
            }

            feedback = "Unable to launch server server since it is already running.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
        }
    }

    /**
     * Shut down the AIS-server.
     */
    @Operation(summary = "Stops the AIS-server.",
            description = "Does nothing if server is already shut-down.")
    @GET
    @Path("/shutdown")
    @APIResponse(responseCode = "200", description = "Server was shut down succesfully.")
    @APIResponse(responseCode = "404", description = "Unable to shut down server since it is already shut down.")
    public Response shutdown() {
        String feedback;
        if (serverIsRunning()) {
            feedback = "AIS-server has been shutdown";
            aisServer.shutdown();
            return Response.ok().entity(feedback).type(MediaType.TEXT_PLAIN).build();
        } else {
            feedback = "Unable to shut down server since it is already shut down.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
        }
    }

    @Operation(summary = "Pause the AIS-server.",
            description = "Does nothing if server is already paused or is shut down.")
    @GET
    @Path("/pause")
    @APIResponse(responseCode = "200", description = "Server was shut down successfully.")
    @APIResponse(responseCode = "404", description = "Unable to pause server since it is already paused.")
    @APIResponse(responseCode = "404", description = "Unable to pause server since it is already shut down.")
    public Response suspend() {
        String feedback;

        if (aisServer == null || !serverIsRunning()) {
            feedback = "Unable to pause server since it is shut down.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
        }

        if (aisServer.isPaused()) {
            feedback = "Unable to pause server since it is already paused.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
        }

        aisServer.pause();
        feedback = "AIS-server has been suspended";
        return Response.ok().entity(feedback).type(MediaType.TEXT_PLAIN).build();
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
    @APIResponse(responseCode = "404", description = "Server is running but its workers are paused")
    @APIResponse(responseCode = "404", description = "Server is not running")
    public Response getStatus() {

        String feedback;
        if (!serverIsRunning()) {
            return Response.status(404).entity("AIS-server is not running.").type(MediaType.TEXT_PLAIN).build();
        }

        if (aisServer.isPaused()) {
            feedback = "AIS-server is running but its workers are paused.";
            return Response.status(404).entity(feedback).type(MediaType.TEXT_PLAIN).build();
        }

        feedback = "Both AIS-server and its workers (if any) are running.";
        return Response.ok().entity(feedback).type(MediaType.TEXT_PLAIN).build();
    }

    @Operation(summary = "Select datafile that AIS-server reads from. Filename should not contain name of directories: only the filename.",
            description = "The selected file must already be located on the server in the folder /tmp/uvms/.")
    @GET
    @Path("/setFilename/{filename}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    @APIResponse(responseCode = "200", description = "New filename has been set.")
    @APIResponse(responseCode = "404", description = "File not found")
    public Response setFilename(@Parameter(description = "Name of the CSV datafile.", required = true)
                                @PathParam("filename") String filename) {
        String fileFullPath = UPLOAD_DIRECTORY + filename;

        File f = new File(fileFullPath);

        if (f.exists() && f.isFile()) {
            this.filename = fileFullPath;
            // If AIS-server has already been started then change filename for already existing instance of AIS-server
            if (this.aisServer != null) {
                this.aisServer.setFilename(fileFullPath);
            }
            return Response.ok().entity("You have selected '" + fileFullPath + "'.").type(MediaType.TEXT_PLAIN).build();
        } else {
            this.filename = "";
            return Response.status(404).entity("Error: no such file found: " + fileFullPath).type(MediaType.TEXT_PLAIN).build();
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
        if (aisServer != null) {
            aisServer.setNthPos(this.nth);
        }

        return Response.ok().entity("Nth value is now set to " + nth).type(MediaType.TEXT_PLAIN).build();

    }

}


