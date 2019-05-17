package se.havochvatten.unionvms;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class Starter extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
    	Server server = new Server();
    	System.out.println("Starting server..");
        new Thread(server).start();
    }
}