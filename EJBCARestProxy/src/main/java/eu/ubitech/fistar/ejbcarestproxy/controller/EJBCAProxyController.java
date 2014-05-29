/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbcarestproxy.controller;

import eu.ubitech.fistar.ejbcarestproxy.conf.Configuration;
import eu.ubitech.fistar.ejbcarestproxy.server.ServerFactory;
import eu.ubitech.fistar.ejbcarestproxy.service.EJBCARestService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Endpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 *
 * @author promitheas
 */
public class EJBCAProxyController {

    private final static Logger LOGGER = Logger.getLogger(EJBCAProxyController.class.getName());
    private final Properties prop = new Properties();
    private InputStream input = null;
    //Jetty Server
    private Server server;

    public static void main(String[] args) {
        EJBCAProxyController controller = new EJBCAProxyController();
        LOGGER.info("EJBCAProxyController initiated");
        controller.readProperties();
        controller.startJettyServer();

    }//main

    private void readProperties() {
        try {
            String path = new java.io.File(".").getCanonicalPath();
            LOGGER.log(Level.INFO, "PATH:{0}", path);
            input = new FileInputStream("gitproxy.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            Configuration.port = Integer.parseInt(prop.getProperty("port").trim());
            Configuration.ScannedFolder = prop.getProperty("ScannedFolder").trim();
            Configuration.timeout = Integer.parseInt(prop.getProperty("timeout").trim());
            Configuration.token = prop.getProperty("token").trim();
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }
    }//EoM readproperties

    private void startJettyServer() {
        try {
            //get Singleton
            server = ServerFactory.INSTANCE.getJettyServer();
            ServletHandler handler = new ServletHandler();
            handler.addServletWithMapping(HelloServlet.class, "/hello");//Set the servlet to run.
            server.setHandler(handler);
            //manage thread
            server.start();
            server.join();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }//EoM

    private void stopJettyServer() {
        try {
            server.stop();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }//EoM    

    @SuppressWarnings("serial")
    public static class HelloServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello SimpleServlet</h1>");
        }
    }

}
