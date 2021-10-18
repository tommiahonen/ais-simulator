package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Starter;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
@ApplicationScoped
public class RestApplication extends Application {

}
