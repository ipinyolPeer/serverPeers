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
import com.peersnet.core.model.Post;
import com.peersnet.core.model.PostWindow;
import com.peersnet.core.service.PeerController;
import com.peersnet.core.service.PostWindowController;

@RunWith(Arquillian.class)
public class PostWindowRestIT extends AbtractIT {
    @Inject 
    PostWindowRest postWindowRest;
    
    @Inject 
    PeerController peerController;
    
    @Inject
    PostWindowController pwc;
    
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
        assertEquals(resp.getStatus(),Response.Status.OK.getStatusCode());
        PostWindowRest.ReturnPostDTO retDTO = (PostWindowRest.ReturnPostDTO) resp.getEntity();
        assertEquals(retDTO.numberOfMessages,3);
        assertEquals(retDTO.notSent.size(),0);
        
        // We perform the call again with one extra peerTo that does not exists
        String badUUID = "UUID don't exists"; 
        aux.add(badUUID);
        postWinDTO.UUID_toList=aux;
        Response resp2 = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp2.getStatus(),Response.Status.OK.getStatusCode());
        PostWindowRest.ReturnPostDTO retDTO2 = (PostWindowRest.ReturnPostDTO) resp2.getEntity();
        assertEquals(retDTO2.numberOfMessages,3);
        assertEquals(retDTO2.notSent.size(),1);
        assertEquals(retDTO2.notSent.get(0), badUUID);
        
        // TODO: check that all PostWindow entities are created
    }
    
    // Testing exceptions
    @Test
    public void postMessageITErrors() throws Exception {
        //Peer peer = peerController.newPeer("testtt@test.com");
        //String UUID_peer = peer.getUUID();
        
        // We check that when the user does not exists a BAD_REQUEST is sent!
        when(principal.getName()).thenReturn("HHHH");    // To simulate authentication
        Response resp = postWindowRest.newPost(sc, null);
        assertEquals(resp.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        
        
        // we check that the given refereed post does not exists we get a Bad Request
        Peer peer = peerController.newPeer("testtt@test.com");
        String UUID_peer = peer.getUUID();
        when(principal.getName()).thenReturn(UUID_peer);    // To simulate authentication
        
        PostWindowRest.PostWindowDTO postWinDTO = new PostWindowRest.PostWindowDTO(); 
        postWinDTO.message = "Hello world!";
        postWinDTO.url = "http://www.hola.com";
        postWinDTO.UUID_toList=new ArrayList<String>();
        postWinDTO.UUID_referTo="BAD ONE";
        resp = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        
        // we check that if UUID_toList is null we get a BAD request
        postWinDTO.UUID_referTo=null;
        postWinDTO.UUID_toList = null;
        resp = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        
        // we check that is the message is null we get a BAD request as well
        postWinDTO.UUID_toList = new ArrayList<String>();
        postWinDTO.message = null;
        resp = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        
        // we check that if the UUID_toList is empty, we get a BAD Request
        postWinDTO.message = "message";
        resp = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp.getStatus(),Response.Status.BAD_REQUEST.getStatusCode());
        
        // Finally, we add one element to UUID_toList to make sure that all constraints are fulfilled
        // and check that that we recieve a OK
        String UUID_bad = "NO ID";
        postWinDTO.UUID_toList.add(UUID_bad);
        resp = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp.getStatus(),Response.Status.OK.getStatusCode());
        PostWindowRest.ReturnPostDTO retDTO = (PostWindowRest.ReturnPostDTO) resp.getEntity();
        assertEquals(retDTO.numberOfMessages,0);
        assertEquals(retDTO.notSent.size(),1);
        assertEquals(retDTO.notSent.get(0), UUID_bad );
    }
    
    @Test
    public void getPostsIT() throws Exception {

        // We create some peers
        Peer peer = peerController.newPeer("testZ@test.com");
        Peer peerA = peerController.newPeer("testAA@test.com");
        Peer peerB = peerController.newPeer("testBB@test.com");
        Peer peerC = peerController.newPeer("testCC@test.com");
        
        // We create a post: peerA will post to peer and peerB
        String M1 = "M1";
        List<String> aux = new ArrayList<String>();
        aux.add(peer.getUUID());
        aux.add(peerB.getUUID());
        PostWindowRest.PostWindowDTO postWinDTO = new PostWindowRest.PostWindowDTO(); 
        postWinDTO.message = M1;
        postWinDTO.url = "http://www.hola.com";
        postWinDTO.UUID_toList=aux;
        when(principal.getName()).thenReturn(peerA.getUUID());    // To simulate authentication
        Response resp = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp.getStatus(),Response.Status.OK.getStatusCode());
        
        // we create a post: peerC will post to peer
        String M2 = "M2";
        List<String> aux2 = new ArrayList<String>();
        aux2.add(peer.getUUID());
        postWinDTO.message = M2;
        postWinDTO.url = "http://www.hola.com";
        postWinDTO.UUID_toList=aux2;
        when(principal.getName()).thenReturn(peerC.getUUID());    // To simulate authentication
        Response resp2 = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp2.getStatus(),Response.Status.OK.getStatusCode());
        
        // we create a post: peer B will post to peerA
        
        String M3 = "M3";
        List<String> aux3 = new ArrayList<String>();
        aux3.add(peerA.getUUID());
        //aux3.add(peerC.getUUID());
        postWinDTO.message = M3;
        postWinDTO.url = "http://www.hola.com";
        postWinDTO.UUID_toList=aux3;
        when(principal.getName()).thenReturn(peerB.getUUID());    // To simulate authentication
        Response resp3 = postWindowRest.newPost(sc, postWinDTO);
        assertEquals(resp3.getStatus(),Response.Status.OK.getStatusCode());
        
        // Now we do the tested calls
        // We gather posts for peer (only two) should be there
        when(principal.getName()).thenReturn(peer.getUUID());
        Response out = postWindowRest.getPosts(sc);
        assertEquals(out.getStatus(),Response.Status.OK.getStatusCode());
        PostWindowRest.ReturnPostMessagesDTO postsDTO = (PostWindowRest.ReturnPostMessagesDTO) out.getEntity();
        assertEquals(2,postsDTO.postsDTO.size());
        int a0=0;
        int a1=0;
        for (PostWindowRest.ReturnPostMessageDTO p:postsDTO.postsDTO) {
            if (p.UUID_from.equals(peerA.getUUID())){
                assertEquals(M1,p.message);
                a0++;
            } else if (p.UUID_from.equals(peerC.getUUID())) {
                assertEquals(M2,p.message);
                a1++;
            }
        }
        assertEquals(1, a0);
        assertEquals(1, a1);
        // Now whe check that the state have been properly modified
        List<Post> posts = pwc.getPostsByToAndState(peer.getUUID(), PostWindow.State.SENT, PostWindow.State.SENT);
        assertEquals(2, posts.size());
        posts = pwc.getPostsByToAndState(peer.getUUID(), PostWindow.State.PENDING, PostWindow.State.PENDING);
        assertEquals(0, posts.size());
        posts = pwc.getPostsByToAndState(peer.getUUID(), PostWindow.State.ACK, PostWindow.State.ACK);
        
        // Now we do the same for PeerB, which only receives one
        // We gather posts for peer (only two) should be there
        when(principal.getName()).thenReturn(peerB.getUUID());
        out = postWindowRest.getPosts(sc);
        assertEquals(out.getStatus(),Response.Status.OK.getStatusCode());
        postsDTO = (PostWindowRest.ReturnPostMessagesDTO) out.getEntity();
        assertEquals(1,postsDTO.postsDTO.size());
        assertEquals(M1,postsDTO.postsDTO.get(0).message);
        // Now whe check that the state have been properly modified
        posts = pwc.getPostsByToAndState(peerB.getUUID(), PostWindow.State.SENT, PostWindow.State.SENT);
        assertEquals(1, posts.size());
        posts = pwc.getPostsByToAndState(peerB.getUUID(), PostWindow.State.PENDING, PostWindow.State.PENDING);
        assertEquals(0, posts.size());
        posts = pwc.getPostsByToAndState(peerB.getUUID(), PostWindow.State.ACK, PostWindow.State.ACK);
        
        // Finally we do the same for peerC who does not receive anything
        when(principal.getName()).thenReturn(peerC.getUUID());
        out = postWindowRest.getPosts(sc);
        assertEquals(out.getStatus(),Response.Status.OK.getStatusCode());
        postsDTO = (PostWindowRest.ReturnPostMessagesDTO) out.getEntity();
        assertEquals(0,postsDTO.postsDTO.size());
        // Now whe check that the state have been properly modified
        posts = pwc.getPostsByToAndState(peerC.getUUID(), PostWindow.State.SENT, PostWindow.State.SENT);
        assertEquals(0, posts.size());
        posts = pwc.getPostsByToAndState(peerC.getUUID(), PostWindow.State.PENDING, PostWindow.State.PENDING);
        assertEquals(0, posts.size());
        posts = pwc.getPostsByToAndState(peerC.getUUID(), PostWindow.State.ACK, PostWindow.State.ACK);
    }
    
    public PostWindow createPostWindow(Long id, Peer toPeer, Post post, PostWindow.State state) {
        PostWindow postWin = new PostWindow();
        postWin.setId(id);
        postWin.setTo(toPeer);
        postWin.setState(state);
        postWin.setPost(post);
        
        return postWin;
    }
    
}
