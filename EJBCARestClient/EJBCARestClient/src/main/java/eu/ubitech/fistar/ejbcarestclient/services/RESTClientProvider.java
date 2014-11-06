/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbcarestclient.services;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author ermis
 */
public class RESTClientProvider {

    private final ClientConfig config = new DefaultClientConfig();

    private final String RestEndPointURL;
    private WebResource service;
    private Client client;

    public RESTClientProvider(String RestWebservicesBaseURL) {
        RestEndPointURL = RestWebservicesBaseURL;
        init();
    }

    //Initialize the RESTClientProvider
    private void init() {
        config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        client = Client.create(config);
        service = client.resource("http://193.175.132.251:8089/rest/ejbca");
         //service = client.resource(getBaseURI());
    }

    private URI getBaseURI() {
        return UriBuilder.fromUri(RestEndPointURL).build();
    }

    public WebResource getRestService() {
        return this.service;
    }

}
