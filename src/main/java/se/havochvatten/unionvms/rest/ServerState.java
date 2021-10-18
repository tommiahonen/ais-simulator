package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Server;
import se.havochvatten.unionvms.Starter;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Startup
@Singleton
@Path("/state")
public class ServerState {

    @GET
    @Path("/start")
    public void init() {
        System.out.println("### At this point server should be started up. ###");
        Starter.launch();
    }

    @GET
    @Path("/stop")
    public void stop() {
        System.out.println("### At this point server should be closed down. ###");
        Starter.stop();
    }
}
