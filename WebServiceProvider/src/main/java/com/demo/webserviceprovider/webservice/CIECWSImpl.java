/*
 * Implement all necessary webservice methods in order to communitcate with SolrServer
 */
package com.demo.webserviceprovider.webservice;

import java.util.logging.Logger;
import javax.jws.WebService;

/**
 *
 * @author Chris Paraskeva
 */
@WebService(serviceName = "CIECWebService", endpointInterface = "eu.ubitech.ubises.engine.webservice.CIECIWS")
public class CIECWSImpl implements CIECIWS {

    public String dummyWebServiceTest(String message) {

        Logger.getLogger(CIECWSImpl.class.getName()).info("\nWebService - CIEC WebService Request\n-----------------------------------------------"
                .concat("\nMessage Received: " + message));
        

        //TODO: Authoratization check
        return "";

    }

}
