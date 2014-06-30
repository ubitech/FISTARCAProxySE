/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbcarestproxy.services;

import eu.ubitech.fistar.ejbca.proxy.client.EjbcaWSClientImpl;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Chris Paraskeva - www.ubitech.eu
 */
@Path("/certificate_status")
public class EjbcaServiceCERTIFICATESTATUS {

    private final EjbcaWSClientImpl ejbcaWSclient = new EjbcaWSClientImpl();


    @GET
    @Produces(MediaType.TEXT_HTML)
    /**
     * Fetch the latest Certificate Revocation List of the given Certificate
     * Authority.
     *
     * @param name The name of the CA to fetch the CRL.
     *
     * @return CRL The CRL of the specific CA.(in DER format)
     */
    public Response getCertificateStatus(@QueryParam(value = "issuerDN") final String issuerDN, @QueryParam(value = "certSN") final String certSN) {



        return Response.status(RESTfulServiceStatus.OK).entity("").build();

    }



    @PUT
    @Produces(MediaType.TEXT_HTML)
    /**
     * Updates a CRL for the given Certificate Authority.
     *
     * @param name The name of the CA to update the CRL.
     */
    public Response updateCRL(@PathParam("name") String name) {

        boolean isCreateSuccess = ejbcaWSclient.createCRL(name);
        if (!isCreateSuccess) {
            return Response.status(RESTfulServiceStatus.INTERNAL_SERVER_ERROR).entity("Could not update CRL with name : " + name).build();
        }
        return Response.status(RESTfulServiceStatus.OK).entity("CRL of CA: " + name + " was updated!").build();

    }

}
//@QueryParam(value = "certSN") final String certSN,
