package com.peersnet.core.rest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.peersnet.core.model.Peer;
import com.peersnet.core.service.PeerController;


@RunWith(Arquillian.class)
public class PeerRestIT extends AbtractIT{
    @Inject 
    PeerRest peerRest;
    
    @Inject
    PeerController peerController;
    
    @Before
    public void setUp() throws Exception {}
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void newPeerIT() throws Exception {
        // Happy Path 
        String eMail = "test@test.com";
        Response rest = peerRest.newPeer(eMail);
        assertEquals(rest.getStatus(), Response.Status.OK.getStatusCode());
        PeerRest.PeerDTO peerDTO = (PeerRest.PeerDTO) rest.getEntity();
        String UUID = peerDTO.UUID;
        
        Peer peer = peerController.getPeer(UUID);
        assertEquals(peer.getEMail(), eMail);
        
        // Error BAD_REQUEST when email already exists!
        rest = peerRest.newPeer(eMail);
        assertEquals(rest.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }
    
    @Test
    public void getPeerIT() throws Exception {
        String eMail = "test2@test.com";
        Response rest = peerRest.newPeer(eMail);
        assertEquals(rest.getStatus(), Response.Status.OK.getStatusCode());
        PeerRest.PeerDTO peerDTO = (PeerRest.PeerDTO) rest.getEntity();
        String UUID = peerDTO.UUID;
        
        // We check we get the rigth information - Happy Path
        Response rest2 = peerRest.getPeer(UUID);
        assertEquals(rest2.getStatus(), Response.Status.OK.getStatusCode());
        PeerRest.PeerDTO peerDTO2 = (PeerRest.PeerDTO) rest2.getEntity();
        assertEquals(peerDTO2.UUID, UUID);
        
        // We check we get the BAD_REQUEST when UUID does not exists
        Response rest3 = peerRest.getPeer("NOEXISTS");
        assertEquals(rest3.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }
    
}
