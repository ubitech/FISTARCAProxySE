
import eu.ubitech.fistar.ejbca.proxy.client.EjbcaUser;
import eu.ubitech.fistar.ejbca.proxy.client.ServiceClientImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejbca.ui.cli.ErrorAdminCommandException;
import org.ejbca.ui.cli.IllegalAdminCommandException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Chris Paraskeva
 */
public class EJBCAWebServiceTester {

    private static ServiceClientImpl EJBCAWSClient = new ServiceClientImpl();

    public static void main(String[] args) {
        if (invoke()) {
            System.out.println("EJBCA is running and invocation was success....");
            testAddNewUser();
        }
    }

    private static void testCheckRevokationStatus() {
        EJBCAWSClient.checkCertificateRevokeStatus("CN=CIEC Sign Gold CA", "60EB2A11D8229814");
    }

    private static void testAddNewUser() {
        EjbcaUser ejbcaUser = new EjbcaUser();
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.USERNAME, "testEjbcaUser4");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.PASSWORD, "testPassword");
        //ejbcaUser.setEntityArgument(EjbcaUser.Arguments.PKCS10, "");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.CA, "CIEC Sign Gold CA");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.SUBJECTDN, "CN=test user2");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.ENDENTITYPROFILE, "EMPTY");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.CERTIFICATEPROFILE, "ENDUSER");
                ejbcaUser.setEntityArgument(EjbcaUser.Arguments.EMAIL, "test@testemail.com");

        try {
            try {
                EJBCAWSClient.editUser(ejbcaUser);
            } catch (IllegalAdminCommandException ex) {
                Logger.getLogger(EJBCAWebServiceTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ErrorAdminCommandException ex) {
            Logger.getLogger(EJBCAWebServiceTester.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static boolean invoke() {
        //1 - Retrieve the current version of EJBCA Instance
        System.out.println("Retrieving EJBCA current version: " + EJBCAWSClient.getEjbcaRAWS().getEjbcaVersion());
        return (EJBCAWSClient.getEjbcaRAWS().getEjbcaVersion() != null);
    }

}
