package com.peersnet.core.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.peersnet.core.model.Peer;
import com.peersnet.core.service.PeerController;

@RunWith(Arquillian.class)
public class PostWindowRestIT extends AbtractIT {
    @Inject 
    PostWindowRest postWindowRest;
    
    @Inject 
    PeerController peerController;
    
    @Mock
    SecurityContext sc;
    
    @Mock 
    private Principal principal;                  
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(sc.getUserPrincipal()).thenReturn(principal);
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    // Happy path
    @Test
    public void postMessageIT() throws Exception {
        // We create the peer that will post
        Peer peer = peerController.newPeer("test@test.com");
        String UUID_peer = peer.getUUID();
        when(principal.getName()).thenReturn(UUID_peer);    // To simulate authentication
        
        // we create the peer friends
        Peer peerA = peerController.newPeer("testA@test.com");
        Peer peerB = peerController.newPeer("testB@test.com");
        Peer peerC = peerController.newPeer("testC@test.com");
        
        List<String> aux = new ArrayList<String>();
        aux.add(peerA.getUUID());
        aux.add(peerB.getUUID());
        aux.add(peerC.getUUID());
        
        // We create the post
        PostWindowRest.PostWindowDTO postWinDTO = new PostWindowRest.PostWindowDTO(); 
        postWinDTO.message = "Hello world!";
        postWinDTO.url = "http://www.hola.com";
        postWinDTO.UUID_toList=aux;
        
        // we perform the call. Its a happy path, so all messages should be sent
        Response resp = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp.getStatus(),Response.Status.OK);
        PostWindowRest.ReturnPostDTO retDTO = (PostWindowRest.ReturnPostDTO) resp.getEntity();
        assertEquals(retDTO.numberOfMessages,3);
        assertEquals(retDTO.notSent.size(),0);
        
        // We perform the call again with one extra peerTo that does not exists
        String badUUID = "UUID don't exists"; 
        aux.add(badUUID);
        postWinDTO.UUID_toList=aux;
        Response resp2 = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp2.getStatus(),Response.Status.OK);
        PostWindowRest.ReturnPostDTO retDTO2 = (PostWindowRest.ReturnPostDTO) resp2.getEntity();
        assertEquals(retDTO2.numberOfMessages,3);
        assertEquals(retDTO2.notSent.size(),1);
        assertEquals(retDTO2.notSent.get(0), badUUID);
        
        // TODO: check that all PostWindow entities are created
    }
    
}
