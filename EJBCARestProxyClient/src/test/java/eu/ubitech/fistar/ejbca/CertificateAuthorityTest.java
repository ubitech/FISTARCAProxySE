package eu.ubitech.fistar.ejbca;

import eu.ubitech.fistar.ejbca.proxy.client.EjbcaWSClientImpl;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CertificateAuthorityTest
        extends TestCase {

    EjbcaWSClientImpl EJBCAWSClient = new EjbcaWSClientImpl();

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CertificateAuthorityTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CertificateAuthorityTest.class);
    }

    /**
     * Retrieve all available CAs in EJBCA Instance
     */
    public void testGetAvailableCAs() {
        assertFalse(EJBCAWSClient.getAvailableCAs().isEmpty());
    }

    /*Get the Certificate of the CA who currently issues all certificates (FISTAR EJBCA)*/
    public void testGetCACertificate() {
        assertNotNull(EJBCAWSClient.getCertificate("4CA55328EFB349B7", "CN=CIEC Sign Gold CA"));
    }

    /*Get all available certificate profiles for a specific end-entities profiles*/
    public void testGetAvailableCertificateProfiles() {
        assertFalse(EJBCAWSClient.getAvailableCertificateProfiles(1806334636).isEmpty());
    }

    /*Get all authorized end-entities profiles for the current issuing CA */
    public void testGetAuthorizedEndEntityProfiles() {
        assertFalse(EJBCAWSClient.getAuthorizedEndEntityProfiles().isEmpty());

    }
    
    
    

}
