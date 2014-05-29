
import eu.ubitech.fistar.ejbca.proxy.client.EjbcaUser;
import eu.ubitech.fistar.ejbca.proxy.client.ServiceClientImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejbca.core.protocol.ws.client.gen.Certificate;
import org.ejbca.core.protocol.ws.client.gen.RevokeStatus;
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
           /* System.out.println("EJBCA is running and invocation was success....");
            System.out.println("Test1: CheckRevokationStatus");
            testCheckRevokationStatus();
            System.out.println("Test2: AddNewUser");
            testAddNewUser();
            System.out.println("Test3: RevokeUserCert");
            testRevokeUserCert();*/
            System.out.println("Test4: GetIssuedCert");
            testGetIssuedCert();

        }
    }
    
        private static void testGetIssuedCert() {
        Certificate cert = EJBCAWSClient.getCertificate("7135AB39DEDEF01F","CN=CIEC Sign Gold CA");
        EJBCAWSClient.storeCertificate(cert, "der", "/home/promitheas/Downloads", "mycert");

    }
    

    private static void testRevokeUserCert() {
        EJBCAWSClient.revokeUserCert("CN=CIEC Sign Gold CA", "5FD08B3E1E17EE23", RevokeStatus.REVOKATION_REASON_SUPERSEDED);

    }

    private static void testCheckRevokationStatus() {
        EJBCAWSClient.checkCertificateRevokeStatus("CN=CIEC Sign Gold CA", "5FD08B3E1E17EE23"); // 60EB2A11D8229814
    }

    private static void testAddNewUser() {
        EjbcaUser ejbcaUser = new EjbcaUser();
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.USERNAME, "testEjbcaUser");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.PASSWORD, "testPassword");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.CA, "CIEC Sign Gold CA");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.SUBJECTDN, "CN=testa user2");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.ENDENTITYPROFILE, "CIEC officer");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.CERTIFICATEPROFILE, "CIEC user");
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
