package com.peersnet.core.rest;

import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.peersnet.core.model.Peer;
import com.peersnet.core.service.PeerController;
import com.peersnet.core.util.Constants;

@Path(Constants.baseURL)
@RequestScoped
@Stateful
public class PeerRest {
    @Inject PeerController pc;
    
    @GET
    @Path(Constants.newPeer + "/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newPeer(@PathParam("email") String eMail) {
        PeerDTO peerDTO = null;
        try {
            Peer peer = pc.newPeer(eMail);
            peerDTO = new PeerDTO();
            peerDTO.convert(peer);
            return Response.status(Response.Status.OK).entity(peerDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    @GET
    @Path(Constants.getPeer + "/{UUID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPeer(@PathParam("UUID") String UUID) {
        PeerDTO peerDTO = null;
        try {
            Peer peer = pc.getPeer(UUID);
            peerDTO = new PeerDTO();
            peerDTO.convert(peer);
            return Response.status(Response.Status.OK).entity(peerDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    static public class PeerDTO {
        public String UUID;
        public Date creationDate;
        public String eMail;
        public Date lastConnection;
        public Date lastMessage;
        
        public void convert(Peer peer) {
            this.UUID = peer.getUUID();
            this.creationDate = peer.getCreationDate();
            this.eMail = peer.getEMail();
            this.lastConnection = peer.getLastConnection();
            this.lastMessage = peer.getLastMessage();
        }
    }
}
