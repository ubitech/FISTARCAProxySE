/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbca.proxy.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cesecore.certificates.crl.RevokedCertInfo;
import org.cesecore.certificates.endentity.EndEntityConstants;
import org.cesecore.certificates.endentity.EndEntityType;
import org.cesecore.certificates.endentity.EndEntityTypes;
import org.cesecore.util.CertTools;
import org.ejbca.core.protocol.ws.client.gen.AuthorizationDeniedException_Exception;
import org.ejbca.core.protocol.ws.client.gen.EjbcaWS;
import org.ejbca.core.protocol.ws.client.gen.ExtendedInformationWS;
import org.ejbca.core.protocol.ws.client.gen.KeyStore;
import org.ejbca.core.protocol.ws.client.gen.RevokeStatus;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;
import org.ejbca.core.protocol.ws.client.gen.UserDoesntFullfillEndEntityProfile_Exception;
import org.ejbca.core.protocol.ws.common.CertificateHelper;
import org.ejbca.ui.cli.ErrorAdminCommandException;
import org.ejbca.ui.cli.IllegalAdminCommandException;

/**
 *
 * @author promitheas
 */
public class ServiceClientImpl {

    private final Logger logger = Logger.getLogger(ServiceClientImpl.class.getName());

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
        final String outputPath = ejbcaUser.getEntityArgument(EjbcaUser.Arguments.OUTPUTPATH).length() > 0 ? getOutputPath(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.OUTPUTPATH)) : null;

        try {
            userdata.setUsername(username);
            userdata.setPassword(password);
            userdata.setClearPwd(false);

            //userdata.setClearPwd(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.CLEARPWD).equalsIgnoreCase("true"));
            userdata.setSubjectDN(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.SUBJECTDN));

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

            /* EndEntityType type = new EndEntityType(EndEntityTypes.getTypesFromHexCode(Integer.parseInt(ejbcaUser.getEntityArgument(EjbcaUser.Arguments.TYPE))));
             if (type.contains(EndEntityTypes.SENDNOTIFICATION)) {
             userdata.setSendNotification(true);
             }
             if (type.contains(EndEntityTypes.KEYRECOVERABLE)) {
             userdata.setKeyRecoverable(true);
             }
             */
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
            wslogger.append("Status: " + userdata.getStatus());
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
                getEjbcaRAWS().editUser(userdata);
                wslogger.append("User '" + userdata.getUsername() + "' has been added/edited.");
//softTokenRequest

                KeyStore result = getEjbcaRAWS().softTokenRequest(userdata, "NULL", "2048", "RSA");

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

    private String getCertSN(String certsn) {
        try {
            new BigInteger(certsn, 16);
        } catch (NumberFormatException e) {
            logger.severe("Error in Certificate SN\nCommand used check the status of certificate \n Usage : checkrevocationstatus <issuerdn> <certificatesn (HEX)>  \n\n");
            System.exit(-1); // NOPMD, this is not a JEE app
        }
        return certsn;
    }

    private String getHardTokenSN(String hardtokensn) {
        if (hardtokensn.equalsIgnoreCase("NONE")) {
            return null;
        }

        return hardtokensn;
    }

    private String getEncoding(String encoding) {
        if (!encoding.equalsIgnoreCase("PEM") && !encoding.equalsIgnoreCase("DER")) {
            throw new UnsupportedOperationException("ERROR: You must specify a valid Encoding format (PEM/DER)...");
        }

        return encoding.toUpperCase();
    }

    private int getStatus(String status) {
        if (status.equalsIgnoreCase("NEW")) {
            return EndEntityConstants.STATUS_NEW;
        }
        if (status.equalsIgnoreCase("INPROCESS")) {
            return EndEntityConstants.STATUS_INPROCESS;
        }
        if (status.equalsIgnoreCase("FAILED")) {
            return EndEntityConstants.STATUS_FAILED;
        }
        if (status.equalsIgnoreCase("HISTORICAL")) {
            return EndEntityConstants.STATUS_HISTORICAL;
        }
        logger.log(Level.SEVERE, "Error in status string : {0}", status);
        System.exit(-1);
        return 0;
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

    private String getOutputPath(String outputpath) {
        File dir = new File(outputpath);
        if (!dir.exists()) {
            logger.severe("Error : Output directory doesn't seem to exist.");
            System.exit(-1); // NOPMD, it's not a JEE app
        }
        if (!dir.isDirectory()) {
            logger.severe("Error : Output directory doesn't seem to be a directory.");
            System.exit(-1); // NOPMD, it's not a JEE app			
        }
        if (!dir.canWrite()) {
            logger.severe("Error : Output directory isn't writeable.");
            System.exit(-1); // NOPMD, it's not a JEE app

        }
        return outputpath;
    }

    private class EjbcaWSLogger {

        private final String LINE_SEPERATOR = System.getProperty("line.separator");
        private final StringBuilder logs = new StringBuilder();

        public void append(String log) {
            logs.append(log);
            logs.append(LINE_SEPERATOR);
        }

        public String getLogMessages() {
            return this.logs.toString();
        }

        public void showLogs(Logger logger) {
            logger.info(logs.toString());
        }
    }

    public EjbcaWS getEjbcaRAWS() {

        try {
            return EjbcaWSClient.INSTANCE.getEjbcaRAWS();
        } catch (Exception ex) {
            Logger.getLogger(ServiceClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
