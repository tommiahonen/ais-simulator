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
    public void start() {
        Starter.launch();
    }

    @GET
    @Path("/stop")
    public void stop() {
        Starter.stop();
    }
}
