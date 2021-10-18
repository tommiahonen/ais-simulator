package se.havochvatten.unionvms.rest;

import se.havochvatten.unionvms.Server;
import se.havochvatten.unionvms.Starter;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class ServerState {

    @PostConstruct
    public void init() {
        System.out.println("### At this point server should be started up. ###");
        Starter.launch();
    }
}
