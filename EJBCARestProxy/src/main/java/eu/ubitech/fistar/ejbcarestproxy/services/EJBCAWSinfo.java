/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ubitech.fistar.ejbcarestproxy.services;

import eu.ubitech.fistar.ejbca.proxy.client.EjbcaWSClientImpl;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import javax.ws.rs.core.Response;

/**
 *
 * @author promitheas
 */
@Path("/info")
public class EJBCAWSinfo {

    EjbcaWSClientImpl ejbcaWSClient = new EjbcaWSClientImpl();
    @GET
    public Response getEJBCAVersion() {

    String output = ejbcaWSClient.getEjbcaRAWS().getEjbcaVersion();

        return Response.status(200).entity(output).build();

    }

}
