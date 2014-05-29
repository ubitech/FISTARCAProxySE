/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbcarestproxy.server;

import org.eclipse.jetty.server.Server;

/**
 *
 * @author promitheas
 */
public enum ServerFactory {

    INSTANCE;

    private Server jettyServer = null;

    public Server getJettyServer() {
        if (jettyServer == null) {
            this.jettyServer = new Server(8888);
        }
        return this.jettyServer;

    }

}
