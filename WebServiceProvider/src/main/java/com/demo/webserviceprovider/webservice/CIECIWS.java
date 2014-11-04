/*
 * SolrBridgeIWS interface provides all the exposed webservice operations
 */
package com.demo.webserviceprovider.webservice;


import javax.jws.WebService;

/**
 *
 * @author Chris Paraskeva
 */
@WebService
public interface CIECIWS {

    /**
     * Web service operation
     *
     * This method is called in order to achieve two-authenticate
     *
     * @param 
     * @return 
     */
    public String dummyWebService(String message);



}
