package eu.ubitech.fistar.ejbcarestclient.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejbca.core.protocol.ws.client.gen.KeyStore;

/**
 *
 * @author ermis
 */
public class Util {

    private static final Logger logger = Logger.getLogger(Util.class.getName());

    private static enum Encode {

        BASE64, BINARY
    };

    private static enum Extension {

        P12, PEM, DER, JKS
    };

    //Stroes a KeyStore to the filesystem
    public static boolean storeKeystore(KeyStore keystore, String type, String encoding, String pathname, String filename) {
        boolean isStoreSuccess = false;
        if (keystore != null) {
            pathname = (pathname == null || pathname.isEmpty() ? System.getProperty("user.dir") : pathname);
            //Check if a valid path and a certificate encoding is given
            if (isValidPath(pathname) && isValidEncoding(encoding) && isValidKeystoreType(type)) {

                if (encoding.equalsIgnoreCase(Encode.BASE64.toString())) {
                    isStoreSuccess = writeTOdisk(getfilepath(pathname, filename + "." + type.toUpperCase()), keystore.getKeystoreData());
                } else {
                    isStoreSuccess = writeTOdisk(getfilepath(pathname, filename + "." + type.toUpperCase()), keystore.getRawKeystoreData());
                }
            }
        }

        if (isStoreSuccess) {
            logger.log(Level.INFO, "KeyStore successfully stored to disk at filepath: {0}", getfilepath(pathname, filename + "." + type.toUpperCase()) + " in " + encoding.toLowerCase() + " format");
        } else {
            logger.log(Level.SEVERE, "Error storing KeyStore to disk...{0}", (keystore == null ? " Input KeyStore is null!" : ""));
        }
        return isStoreSuccess;
    }

    //Checks if the give path exists and is valid
    private static boolean isValidPath(String outputpath) {
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

    private static boolean isValidKeystoreType(String type) {
        for (Enum extension : Extension.values()) {
            if (extension.toString().equalsIgnoreCase(type)) {
                return true;
            }
        }
        logger.severe("ERROR: You must specify a valid KeyStroe type (PEM/P12/JKS)...");
        return false;
    }

    private static boolean isValidEncoding(String encoding) {
        for (Enum encode : Encode.values()) {
            if (encode.toString().equalsIgnoreCase(encoding)) {
                return true;
            }
        }
        logger.severe("ERROR: You must specify a valid Encoding format (BASE64/BINARY)...");
        return false;
    }

    public static String getfilepath(String pathname, String filename) {
        return pathname + "/" + filename;
    }

    /**
     * Writes a stream of bytes to the filesystem
     *
     * @param filepath The filepath in filesystem which the bytes will be stored
     * @param bytestream An array of bytes
     * @return True is the write was success or false if an error occurred
     */
    public static boolean writeTOdisk(String filepath, byte[] bytestream) {
        boolean isWriteSuccess = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filepath);
            fos.write(bytestream);
            fos.close();
            isWriteSuccess = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return isWriteSuccess;
    }
}
