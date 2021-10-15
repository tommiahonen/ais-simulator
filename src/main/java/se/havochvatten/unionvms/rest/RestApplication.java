package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Starter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
@Singleton
public class RestApplication extends Application {

    public RestApplication() {
        System.out.println("AIS-server will start now.");
        Starter.launch();
    }
}
