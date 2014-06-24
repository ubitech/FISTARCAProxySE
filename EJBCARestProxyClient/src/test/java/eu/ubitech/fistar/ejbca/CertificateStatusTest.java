package eu.ubitech.fistar.ejbca;

import eu.ubitech.fistar.ejbca.proxy.client.EjbcaWSClientImpl;
import java.security.cert.X509CRL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.ejbca.core.protocol.ws.client.gen.RevokeStatus;

/**
 * Unit test for simple App.
 */
public class CertificateStatusTest
        extends TestCase {

    EjbcaWSClientImpl EJBCAWSClient = new EjbcaWSClientImpl();

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CertificateStatusTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CertificateStatusTest.class);
    }

    /**
     * Check the status of a certificate (returns a revoked reason of the
     * certificate)
     */
    public void testCheckCertificateStatus() {
        assert (EJBCAWSClient.checkCertificateRevokeStatus("CN=CIEC Sign Gold CA", "5FD08B3E1E17EE23").getReason() == RevokeStatus.REVOKATION_REASON_CACOMPROMISE);
    }

    public void testRevokeUserCert() {
        EJBCAWSClient.revokeUserCert("CN=CIEC Sign Gold CA", "5FD08B3E1E17EE23", RevokeStatus.REVOKATION_REASON_SUPERSEDED);
    }

}
