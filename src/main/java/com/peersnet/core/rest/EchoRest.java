package com.peersnet.core.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/echo")
@RequestScoped
@Stateful
public class EchoRest {

    @GET
    @Path("/doEcho/{stuff}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doEcho(@PathParam("stuff") String stuff) {
        return stuff;
    }
}
