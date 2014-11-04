/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbcarestclient.services;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.impl.provider.entity.ByteArrayProvider;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.ubitech.fistar.ejbcarestclient.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author ermis
 */
public class RESTClientService {

    final RESTClientProvider restClientProvider;

    public RESTClientService(RESTClientProvider _restClientProvider) {
        restClientProvider = _restClientProvider;
    }

    //Create the end-enity(User) in order to generate the certificate
    public boolean createEndEntity(String username, String password, String ca, String dn, String entityprofile, String certificateprofile, String email, String token, String hardtoken, String subjectaltname) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("password", password);
        params.add("ca", ca);
        params.add("dn", dn);
        params.add("entityprofile", entityprofile);
        params.add("certificateprofile", certificateprofile);
        params.add("email", email);
        params.add("token", token);
        if (!hardtoken.isEmpty()) {
            params.add("hardtoken", hardtoken);
        }
        params.add("subjectaltname", subjectaltname);

        ClientResponse response = restClientProvider.getRestService().path("endentity").path(username).queryParams(params).accept(MediaType.TEXT_HTML).post(ClientResponse.class);
        System.out.println("INFO: " + response.getEntity(String.class));

        if (response.getStatus() == RESTfulServiceStatus.OK) {
            return true;
        }
        return false;
    }

    public byte[] createUserCertificate(String username, String password) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("password", password);
        ClientResponse response = restClientProvider.getRestService().path("user_certificate").path(username).queryParams(params).accept(MediaType.TEXT_HTML).post(ClientResponse.class);
    
        try {
            //Util.storeKeystore(response.getEntity(String.class).getBytes(), "JKS", "binary", "/home/ermis/Downloads/", "iCert");
            //System.out.println(response.getEntity(ByteArrayProvider.class));
            Util.writeTOdisk("/home/ermis/Downloads/certs/"+username+".P12", IOUtils.toByteArray(response.getEntityInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(RESTClientService.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println("INFO: |" + response.getEntity(String.class)+"|");
        return null;

    }

    public static void main(String[] args) {
        RESTClientProvider restClientProvider = new RESTClientProvider("http://localhost:9090/rest/ejbca");
        RESTClientService restClientService = new RESTClientService(restClientProvider);
        //Failr
        if (restClientService.createEndEntity("fistarREST", "password", "FISTARManagementCA", "CN=fistarREST", "FISTARUser", "ENDUSER", "test@test", "P12", "", "") == false) {

        } else //Success
        {
            restClientService.createUserCertificate("fistarREST", "password");
        }

        // System.out.println(service.path("info").accept(MediaType.TEXT_HTML).get(String.class));
        //System.out.println(service.path("endentity").path(username).queryParams(params).accept(MediaType.TEXT_HTML).post(String.class, "contentaaa"));
    }

}
