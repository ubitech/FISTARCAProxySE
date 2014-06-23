/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbcarestproxy.services;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author promitheas
 */
public class EJBCARestService {

    private final static Logger LOGGER = Logger.getLogger(EJBCARestService.class.getName());

    public ArrayList<String> getAllRevisions(String token) {
        LOGGER.info("invoked");
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    public String getLatestRevision(String token) {
        LOGGER.info("invoked");
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    public ArrayList<String> getFilesOfCommit(String token, String revision) {
        LOGGER.info("invoked");
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
