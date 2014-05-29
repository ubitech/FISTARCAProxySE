/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */   // RevokeStatus.
package eu.ubitech.fistar.ejbca.proxy.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cesecore.certificates.crl.RevokedCertInfo;
import org.cesecore.util.CertTools;
import org.ejbca.core.protocol.ws.client.gen.AlreadyRevokedException_Exception;
import org.ejbca.core.protocol.ws.client.gen.ApprovalException_Exception;
import org.ejbca.core.protocol.ws.client.gen.AuthorizationDeniedException_Exception;
import org.ejbca.core.protocol.ws.client.gen.CADoesntExistsException_Exception;
import org.ejbca.core.protocol.ws.client.gen.Certificate;
import org.ejbca.core.protocol.ws.client.gen.DateNotValidException_Exception;
import org.ejbca.core.protocol.ws.client.gen.EjbcaException_Exception;
import org.ejbca.core.protocol.ws.client.gen.EjbcaWS;
import org.ejbca.core.protocol.ws.client.gen.ExtendedInformationWS;
import org.ejbca.core.protocol.ws.client.gen.KeyStore;
import org.ejbca.core.protocol.ws.client.gen.NotFoundException_Exception;
import org.ejbca.core.protocol.ws.client.gen.RevokeBackDateNotAllowedForProfileException_Exception;
import org.ejbca.core.protocol.ws.client.gen.RevokeStatus;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;
import org.ejbca.core.protocol.ws.client.gen.UserDoesntFullfillEndEntityProfile_Exception;
import org.ejbca.core.protocol.ws.client.gen.WaitingForApprovalException_Exception;
import org.ejbca.ui.cli.ErrorAdminCommandException;
import org.ejbca.ui.cli.IllegalAdminCommandException;

/**
 *
 * @author promitheas
 */
public class ServiceClientImpl {

    private final Logger logger = Logger.getLogger(ServiceClientImpl.class.getName());
    private final String PEM_ENCODING = "PEM";
    private final String DER_ENCODING = "DER";

    /**
     * Edits/adds a user to the EJBCA database.
     *
     * @param ejbcaUser
     * @throws org.ejbca.ui.cli.IllegalAdminCommandException
     * @throws org.ejbca.ui.cli.ErrorAdminCommandException
     */
    public void editUser(EjbcaUser ejbcaUser) throws IllegalAdminCommandException, ErrorAdminCommandException {
        EjbcaUser user = new EjbcaUser();
        user.setEntityArgument(EjbcaUser.Arguments.USERNAME, null);
        EjbcaWSLogger wslogger = new EjbcaWSLogger();
        final UserDataVOWS userdata = new UserDataVOWS();
        final String username = ejbcaUser.getEntityArgument(EjbcaUser.Arguments.USERNAME);
        final String password = ejbcaUser.getEntityArgument(EjbcaUser.Arguments.PASSWORD);
        final String encoding = getEncoding(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.ENCODING));
        final String hardtokensn = ejbcaUser.getEntityArgument(EjbcaUser.Arguments.HARDTOKENSN);
        final String outputPath = ejbcaUser.getEntityArgument(EjbcaUser.Arguments.OUTPUTPATH).length() > 0 ? ejbcaUser.getEntityArgument(EjbcaUser.Arguments.OUTPUTPATH) : null;

        try {
            userdata.setUsername(username);
            userdata.setPassword(password);
            userdata.setClearPwd(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.CLEARPWD).equalsIgnoreCase("true"));
            userdata.setSubjectDN(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.SUBJECTDN));

            if (!ejbcaUser.getEntityArgument(EjbcaUser.Arguments.HARDTOKENSN).equalsIgnoreCase("NULL")) {
                userdata.setHardTokenIssuerName(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.HARDTOKENSN));
            }

            if (!ejbcaUser.getEntityArgument(EjbcaUser.Arguments.SUBJECTALTNAME).equalsIgnoreCase("NULL")) {
                userdata.setSubjectAltName(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.SUBJECTALTNAME));
            }

            if (!ejbcaUser.getEntityArgument(EjbcaUser.Arguments.EMAIL).equalsIgnoreCase("NULL")) {
                userdata.setEmail(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.EMAIL));
            }

            userdata.setCaName(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.CA));
            userdata.setTokenType(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.TOKEN));
            userdata.setStatus(Integer.parseInt(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.STATUS)));
            userdata.setEndEntityProfileName(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.ENDENTITYPROFILE));
            userdata.setCertificateProfileName(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.CERTIFICATEPROFILE));

            if (!ejbcaUser.getEntityArgument(EjbcaUser.Arguments.ISSUERALIAS).equalsIgnoreCase("NONE")) {
                userdata.setEmail(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.ISSUERALIAS));
            }

            wslogger.append("Trying to add user:");
            wslogger.append("Username: " + userdata.getUsername());
            wslogger.append("Subject DN: " + userdata.getSubjectDN());
            wslogger.append("Subject Altname: " + userdata.getSubjectAltName());
            wslogger.append("Email: " + userdata.getEmail());
            wslogger.append("CA Name: " + userdata.getCaName());
            wslogger.append("Token: " + userdata.getTokenType());
            wslogger.append("Status: " + getStatus(userdata.getStatus()));
            wslogger.append("End entity profile: " + userdata.getEndEntityProfileName());
            wslogger.append("Certificate profile: " + userdata.getCertificateProfileName());
            wslogger.append("Hard Token Issuer Alias: " + (userdata.getHardTokenIssuerName() != null ? userdata.getHardTokenIssuerName() : "NONE"));
            {
                final List<ExtendedInformationWS> eil = userdata.getExtendedInformation();
                if (eil != null) {
                    wslogger.append("Extended information:");
                    for (ExtendedInformationWS ei : eil) {
                        wslogger.append("	'" + ei.getName() + "' = '" + ei.getValue() + "'");
                    }
                }
            }
            {
                final BigInteger bi = userdata.getCertificateSerialNumber();
                if (bi != null) {
                    wslogger.append("CERTIFICATESERIALNUMBER" + "=0x" + bi.toString(16));
                }
            }

            try {
                //Add/Edit End-Entity to EJBCA
                getEjbcaRAWS().editUser(userdata);
                wslogger.append("User '" + userdata.getUsername() + "' has been added/edited.");
                KeyStore result = getEjbcaRAWS().softTokenRequest(userdata, userdata.getHardTokenIssuerName(), "2048", "RSA");

                //TODO: Proper handling of Certificate Format
                if (result == null) {
                    wslogger.append("No certificate could be generated for user, check server logs for error.");
                } else {
                    String filepath = username;
                    if (encoding.equals("DER")) {
                        filepath += ".cer";
                    } else {
                        filepath += ".jks";
                    }
                    if (outputPath != null) {
                        filepath = outputPath + "/" + filepath;
                    }

                    FileOutputStream fos = new FileOutputStream(filepath);
                    fos.write(result.getRawKeystoreData());
                    fos.close();

                    wslogger.append("Certificate generated, written to " + filepath);
                }
            } catch (AuthorizationDeniedException_Exception e) {
                wslogger.append("Error : " + e.getMessage());
            } catch (UserDoesntFullfillEndEntityProfile_Exception e) {
                wslogger.append("Error : Given userdata doesn't fullfill end entity profile. : " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ErrorAdminCommandException(e);
        }
        wslogger.showLogs(this.logger);
    }

    /**
     * Fetches issued certificate.
     *
     * Authorization requirements:
     * <pre>
     * - A valid certificate
     * - /ca_functionality/view_certificate
     * - /ca/&lt;of the issing CA&gt;
     * </pre>
     *
     * @param certSNinHex the certificate serial number in hexadecimal
     * representation
     * @param issuerDN the issuer of the certificate
     * @return the certificate (in WS representation) or null if certificate
     * couldn't be found.
     */
    public Certificate getCertificate(String certSNinHex, String issuerDN) {
        EjbcaWSLogger wslogger = new EjbcaWSLogger();
        Certificate certificate = null;
        try {
            certificate = getEjbcaRAWS().getCertificate(certSNinHex, issuerDN);
            if (certificate == null) {
                wslogger.append("Certificate with SerialNumber: " + certSNinHex + " and IssuerDN: " + issuerDN + " could not be found!");
            } else {
                wslogger.append("Certificate with SerialNumber: " + certSNinHex + " and IssuerDN: " + issuerDN + " was fetched!");
            }
        } catch (AuthorizationDeniedException_Exception ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CADoesntExistsException_Exception ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EjbcaException_Exception ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        wslogger.showLogs(this.logger);
        return certificate;
    }

    /**
     * Returns revocation status for given user.
     *
     * @param issuerDN The distinguish name of the issuer CA
     * @param certSN The serial number of the certificate to check the
     * revocation status
     * @return RevokeStatus Object
     */
    public RevokeStatus checkCertificateRevokeStatus(String issuerDN, String certSN) {
        RevokeStatus status = null;
        EjbcaWSLogger wslogger = new EjbcaWSLogger();

        try {
            issuerDN = CertTools.stringToBCDNString(issuerDN);
            certSN = getCertSN(certSN);

            status = getEjbcaRAWS().checkRevokationStatus(issuerDN, certSN);

            if (status == null) {
                wslogger.append("Error, No certificate found in database.");
            } else {
                wslogger.append("Revocation status :");
                wslogger.append("  IssuerDN      : " + status.getIssuerDN());
                wslogger.append("  CertificateSN : " + status.getCertificateSN());
                if (status.getReason() == RevokedCertInfo.NOT_REVOKED) {
                    wslogger.append("  Status        : NOT REVOKED");
                } else {
                    wslogger.append("  Status        : REVOKED");
                    wslogger.append("  Reason        : " + EjbcaWSClient.INSTANCE.getRevokeReason(status.getReason()));
                    wslogger.append("  Date          : " + status.getRevocationDate().toString());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        wslogger.showLogs(this.logger);
        return status;

    }

    /**
     * Revokes a user certificate.
     *
     * @param issuerDN of the certificate to revoke
     * @param certificateSN of the certificate to revoke
     * @param reason for revocation, one of
     * {@link org.ejbca.core.protocol.ws.client.gen.RevokeStatus}.REVOKATION_REASON_
     * constants, or use
     * {@link org.ejbca.core.protocol.ws.client.gen.RevokeStatus}.NOT_REVOKED to
     * un-revoke a certificate on hold.
     * @param sDate The revocation date. If null then the current date is used.
     * If specified then the profile of the certificate must allow "back dating"
     * and the date must be i the past. The parameter is specified as an
     * <a href="http://en.wikipedia.org/wiki/ISO8601">ISO 8601 string</a>. An
     * example: 2012-06-07T23:55:59+02:00
     */
    public void revokeCertBackdated(String issuerDN, String certificateSN, int reason, String sDate) {
        EjbcaWSLogger wslogger = new EjbcaWSLogger();
        try {
            final String issuerdn = CertTools.stringToBCDNString(issuerDN);
            final String certsn = getCertSN(certificateSN);
            final String justRevoke = "To revoke the certificate with the current time remove the last argument (revocation time).";
            try {
                final RevokeStatus status = getEjbcaRAWS().checkRevokationStatus(issuerdn, certsn);
                if (status != null) {
                    getEjbcaRAWS().revokeCertBackdated(issuerdn, certsn, reason, sDate);
                    wslogger.append("Certificate revoked (or unrevoked) successfully.");
                } else {
                    wslogger.append("Certificate does not exist.");
                }
            } catch (AuthorizationDeniedException_Exception e) {
                wslogger.append("Error : " + e.getMessage());
            } catch (AlreadyRevokedException_Exception e) {
                wslogger.append("The certificate was already revoked, or you tried to unrevoke a permanently revoked certificate.");
            } catch (WaitingForApprovalException_Exception e) {
                wslogger.append("The revocation request has been sent for approval.");
            } catch (ApprovalException_Exception e) {
                wslogger.append("This revocation has already been requested.");
            } catch (DateNotValidException_Exception e) {
                wslogger.append(e.getMessage());
                wslogger.append(justRevoke);
            } catch (RevokeBackDateNotAllowedForProfileException_Exception e) {
                wslogger.append(e.getMessage());
                wslogger.append(justRevoke);
            }
        } catch (CADoesntExistsException_Exception e) {
            try {
                throw new ErrorAdminCommandException(e);
            } catch (ErrorAdminCommandException ex) {
                Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (EjbcaException_Exception e) {
            try {
                throw new ErrorAdminCommandException(e);
            } catch (ErrorAdminCommandException ex) {
                Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NotFoundException_Exception e) {
            try {
                throw new ErrorAdminCommandException(e);
            } catch (ErrorAdminCommandException ex) {
                Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        wslogger.showLogs(this.logger);
    }

    /**
     * Same as {@link #revokeCertBackdated(String, String, int, String)} but
     * revocation date is current time.
     *
     * @param issuerDN
     * @param certificateSN
     * @param reason
     */
    public void revokeUserCert(String issuerDN, String certificateSN, int reason) {
        revokeCertBackdated(issuerDN, certificateSN, reason, null);

    }

    // -- Help Functions --
    
    
    private String getCertSN(String certsn) {
        try {
            new BigInteger(certsn, 16);
        } catch (NumberFormatException e) {
            logger.severe("Error in Certificate SN\nCommand used check the status of certificate \n Usage : checkrevocationstatus <issuerdn> <certificatesn (HEX)>  \n\n");
        }
        return certsn;
    }

    private String getHardTokenSN(String hardtokensn) {
        if (hardtokensn.equalsIgnoreCase("NONE")) {
            return null;
        }

        return hardtokensn;
    }

    public boolean storeCertificate(Certificate certificate, String encoding, String pathname, String filename) {
        boolean isStoreSuccess = false;
        if (certificate != null) {
            pathname = (pathname == null || pathname.isEmpty() ? System.getProperty("user.dir") : pathname);
            encoding = getEncoding(encoding);
            //Check if a valid path and a certificate encoding is given
            if (this.isValidPath(pathname) && !this.getEncoding(encoding).isEmpty()) {

                if (encoding.equalsIgnoreCase(PEM_ENCODING)) {
                    isStoreSuccess = this.writeTOdisk(getfilepath(pathname, filename + "." + PEM_ENCODING), certificate.getCertificateData());
                } else {
                    isStoreSuccess = this.writeTOdisk(getfilepath(pathname, filename + "." + DER_ENCODING), certificate.getRawCertificateData());
                }
            }
        }
        if (isStoreSuccess) {
            logger.log(Level.SEVERE, "Certificate successfully stored to disk at filepath: {0}", getfilepath(pathname, filename + "." + encoding.toUpperCase()));
        } else {
            logger.severe("Error storing certificate to disk...");
        }
        return isStoreSuccess;
    }

    private String getEncoding(String encoding) {
        if (!encoding.equalsIgnoreCase(PEM_ENCODING) && !encoding.equalsIgnoreCase(DER_ENCODING)) {
            logger.severe("ERROR: You must specify a valid Encoding format (PEM/DER)...");
            return "";
        }
        return encoding.toUpperCase();
    }

    private String getStatus(int status) {

        switch (status) {

            case (UserDataVOWS.STATUS_NEW): {
                return "NEW";
            }
            case (UserDataVOWS.STATUS_FAILED): {
                return "FAILED";
            }
            case (UserDataVOWS.STATUS_INITIALIZED): {
                return "INITIALIZED";
            }
            case (UserDataVOWS.STATUS_INPROCESS): {
                return "INPROCESS";
            }
            case (UserDataVOWS.STATUS_GENERATED): {
                return "GENERATED";
            }
            case (UserDataVOWS.STATUS_REVOKED): {
                return "REVOKED";
            }
            case (UserDataVOWS.STATUS_HISTORICAL): {
                return "HISTORICAL";
            }
            case (UserDataVOWS.STATUS_KEYRECOVERY): {
                return "KEYRECOVERY";
            }
        }

        logger.log(Level.SEVERE, "Error in status string : {0}", status);
        System.exit(-1);
        return "";
    }

    private String getPKCS10(String pkcs10Path) {
        String retval = null;
        try {
            FileInputStream fis = new FileInputStream(pkcs10Path);
            byte[] contents = new byte[fis.available()];
            fis.read(contents);
            fis.close();
            retval = new String(contents);
        } catch (FileNotFoundException e) {
            logger.severe("Error : PKCS10 file couln't be found.");
            System.exit(-1); // NOPMD, it's not a JEE app		
        } catch (IOException e) {
            logger.severe("Error reading content of PKCS10 file.");
            System.exit(-1); // NOPMD, it's not a JEE app	
        }

        return retval;
    }

    private boolean isValidPath(String outputpath) {
        File dir = new File(outputpath);
        if (!dir.exists()) {
            logger.severe("Error : Output directory doesn't seem to exist.");
            return false;
        }
        if (!dir.isDirectory()) {
            logger.severe("Error : Output directory doesn't seem to be a directory.");
            return false;
        }
        if (!dir.canWrite()) {
            logger.severe("Error : Output directory isn't writeable.");
            return false;

        }
        return true;
    }

    public EjbcaWS getEjbcaRAWS() {

        try {
            return EjbcaWSClient.INSTANCE.getEjbcaRAWS();
        } catch (Exception ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getfilepath(String pathname, String filename) {
        return pathname + "/" + filename;
    }

    public boolean writeTOdisk(String filepath, byte[] bytestream) {
        boolean isWriteSuccess = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filepath);
            fos.write(bytestream);
            fos.close();
            isWriteSuccess = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return isWriteSuccess;
    }

    private class EjbcaWSLogger {

        private final String LINE_SEPERATOR = System.getProperty("line.separator");
        private final StringBuilder logs = new StringBuilder();

        public void append(String log) {
            logs.append(log);
            logs.append(LINE_SEPERATOR);
        }

        public void showLogs(Logger logger) {
            logger.info(logs.toString());
        }
    }
}
