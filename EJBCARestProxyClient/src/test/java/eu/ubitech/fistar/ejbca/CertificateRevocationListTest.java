package eu.ubitech.fistar.ejbca;

import eu.ubitech.fistar.ejbca.proxy.client.EjbcaUser;
import eu.ubitech.fistar.ejbca.proxy.client.EjbcaWSClientImpl;
import java.security.cert.X509CRL;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertNotNull;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ejbca.core.protocol.ws.client.gen.KeyStore;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;
import org.ejbca.ui.cli.ErrorAdminCommandException;
import org.ejbca.ui.cli.IllegalAdminCommandException;

/**
 * Unit test for simple App.
 */
public class CertificateRevocationListTest
        extends TestCase {

    EjbcaWSClientImpl EJBCAWSClient = new EjbcaWSClientImpl();

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CertificateRevocationListTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CertificateRevocationListTest.class);
    }

    /**
     * Check the status of a certificate (returns a revoked reason of the
     * certificate)
     */
    public void testCreateCRL() {
        EJBCAWSClient.createCRL("CIEC Sign Gold CA");
    }

        public void testGetLatestCRL() {
        X509CRL crl = EJBCAWSClient.getLatestCRL("CIEC Sign Gold CA", false);
        assertNotNull(crl);
    }
}
