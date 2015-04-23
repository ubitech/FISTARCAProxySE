package eu.ubitech.fistar.ejbcarestclient.services;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.ubitech.fistar.ejbcarestclient.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Chris Paraskeva
 */
public class RESTClientService {

    final RESTClientProvider restClientProvider;

    public RESTClientService(RESTClientProvider _restClientProvider) {
        restClientProvider = _restClientProvider;
    }

    //Create the end-enity(User) in order to generate the certificate
    public boolean createEndEntity(String username, String password, String ca, String dn, String entityprofile, String certificateprofile, String email, String token) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("password", password);
        params.add("ca", ca);
        params.add("dn", dn);
        params.add("entityprofile", entityprofile);
        params.add("certificateprofile", certificateprofile);
        params.add("email", email);
        params.add("token", token);
//        if (!hardtoken.isEmpty()) {
//            params.add("hardtoken", hardtoken);
//        }
//        params.add("subjectaltname", subjectaltname);

        ClientResponse response = restClientProvider.getRestService().path("endentity").path(username).queryParams(params).accept(MediaType.TEXT_HTML).post(ClientResponse.class);
        if (response.getStatus() == RESTfulServiceStatus.OK) {
            return true;
        }
        return false;
    }

    public InputStream createUserCertificate(String username, String password) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("password", password);
        ClientResponse response = restClientProvider.getRestService().path("user_certificate").path(username).queryParams(params).accept(MediaType.TEXT_HTML).get(ClientResponse.class);

        InputStream certificate = null;

        //Util.storeKeystore(response.getEntity(String.class).getBytes(), "JKS", "binary", "/home/ermis/Downloads/", "iCert");
        //certificateChain = IOUtils.toByteArray(response.getEntityInputStream());
        //Uncomment to write to FileSystem
        certificate = response.getEntityInputStream();

//        try {
//            Util.writeTOdisk("/home/ermis/" + username + ".P12", IOUtils.toByteArray(response.getEntityInputStream()));
//        } catch (IOException ex) {
//            Logger.getLogger(RESTClientService.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return certificate;

    }

    public static void main(String[] args) {
        //FI-STAR server
        //RESTClientProvider restClientProvider = new RESTClientProvider("http://193.175.132.251:8089/rest/ejbca");

        RESTClientProvider restClientProvider = new RESTClientProvider("http://192.168.1.203:9090/rest/ejbca");

        RESTClientService restClientService = new RESTClientService(restClientProvider);
        //Fail

        restClientService.createEndEntity("fistarREST", "password", "FISTARManagementCA", "CN=fistarREST", "FISTARUser", "ENDUSER", "test@test", "P12");
//
        restClientService.createUserCertificate("fistarREST", "password");

    }

}
