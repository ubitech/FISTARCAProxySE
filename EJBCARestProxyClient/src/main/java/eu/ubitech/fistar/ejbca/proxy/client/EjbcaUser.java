/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbca.proxy.client;

import java.util.HashMap;
import java.util.Map;
import org.cesecore.certificates.endentity.EndEntityConstants;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;

/**
 *
 * @author promitheas
 */
public final class EjbcaUser {

    private final Map<Enum, String> entityMap = new HashMap<Enum, String>();

    
    public enum Token {

        USERGENERATED, P12, JKS, PEM
    };

    public enum Arguments {

        USERNAME,
        PASSWORD,
        CLEARPWD,
        SUBJECTDN,
        SUBJECTALTNAME,
        EMAIL,
        CA,
        TYPE,
        TOKEN,
        STATUS,
        ENDENTITYPROFILE,
        CERTIFICATEPROFILE,
        ISSUERALIAS,
        PKCS10,
        ENCODING,
        HARDTOKENSN,
        OUTPUTPATH
    };

    public EjbcaUser() {
        this.setDefaultValues();
    }

    public void setDefaultValues() {
        setEntityArgument(Arguments.ISSUERALIAS, "NONE");
        setEntityArgument(Arguments.SUBJECTALTNAME, "NULL");
        setEntityArgument(Arguments.HARDTOKENSN, "NONE");
        setEntityArgument(Arguments.ENCODING, "PEM");
        setEntityArgument(Arguments.OUTPUTPATH, "");
        setEntityArgument(Arguments.STATUS, Integer.toString(UserDataVOWS.STATUS_NEW));
        //setEntityArgument(Arguments.CLEARPWD, "TRUE");
        setEntityArgument(Arguments.EMAIL, "NULL");
        setEntityArgument(Arguments.TOKEN,UserDataVOWS.TOKEN_TYPE_JKS); // "USERGENERATED" , "P12" ,  "JKS" , "PEM"
    }

    public void setEntityArgument(Enum key, String value) {
        if (Arguments.valueOf(key.toString()) != null) {
            this.entityMap.put(key, value);
        }

    }

    public String getEntityArgument(Enum key) {
        return (Arguments.valueOf(key.toString()) != null ? this.entityMap.get(key) : "");

    }

}
