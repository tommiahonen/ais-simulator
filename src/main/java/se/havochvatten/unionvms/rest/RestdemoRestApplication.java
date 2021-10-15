package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Starter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
@Singleton
public class RestdemoRestApplication extends Application {

    public RestdemoRestApplication() {
        System.out.println("AIS-server will start now.");
        Starter.launch();
    }

    @GET
    @Path("/glass")
    public String getGlass() {
        return "god glass med lakrits mmmm";
    }
}
