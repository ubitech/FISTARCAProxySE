package eu.ubitech.fistar.ejbca;

import eu.ubitech.fistar.ejbca.proxy.client.EjbcaUser;
import eu.ubitech.fistar.ejbca.proxy.client.EjbcaWSClientImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ejbca.core.protocol.ws.client.gen.Certificate;
import org.ejbca.core.protocol.ws.client.gen.KeyStore;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;
import org.ejbca.core.protocol.ws.client.gen.UserMatch;
import org.ejbca.ui.cli.ErrorAdminCommandException;
import org.ejbca.ui.cli.IllegalAdminCommandException;

/**
 * Unit test for simple App.
 */
public class EndEntityTest
        extends TestCase {

    EjbcaWSClientImpl EJBCAWSClient = new EjbcaWSClientImpl();

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EndEntityTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(EndEntityTest.class);
    }

    /**
     * Check the status of a certificate (returns a revoked reason of the
     * certificate)
     */
    public void testAddEndEntity() {
        EjbcaUser ejbcaUser = new EjbcaUser();
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.USERNAME, "testEjbcaUser");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.PASSWORD, "testPassword");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.CA, "CIEC Sign Gold CA");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.SUBJECTDN, "CN=testa user3");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.ENDENTITYPROFILE, "CIEC officer");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.CERTIFICATEPROFILE, "CIEC user");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.EMAIL, "test@testemail.com");
        ejbcaUser.setEntityArgument(EjbcaUser.Arguments.TOKEN, UserDataVOWS.TOKEN_TYPE_P12);

        try {
            try {
                KeyStore keystore = EJBCAWSClient.editUser(ejbcaUser);
                assert (keystore != null);
            } catch (IllegalAdminCommandException ex) {
                Logger.getLogger(EndEntityTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ErrorAdminCommandException ex) {
            Logger.getLogger(EndEntityTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testFindEndEntity() {
        UserMatch usermatch = new UserMatch(UserMatch.MATCH_WITH_CA, UserMatch.MATCH_TYPE_EQUALS, "CIEC Sign Gold CA");
        assert (EJBCAWSClient.findUser(usermatch).size() > 0);
    }
    
    public void testGetEndEntityCertificate(){
        Certificate cert = EJBCAWSClient.getCertificate("4CA55328EFB349B7", "CN=CIEC Sign Gold CA");
        assertNotNull(cert);
    }

}
