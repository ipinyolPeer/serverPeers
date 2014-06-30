package com.peersnet.core.service;

import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import com.peersnet.core.data.MainDB;
import com.peersnet.core.data.PeerDB;
import com.peersnet.core.data.PostDB;
import com.peersnet.core.model.Peer;
import com.peersnet.core.model.Post;

public class PostControllerTest {

    private PostController pc;
    private MainDB db;
    
    @Mock
    private PeerController peerController;
    
    @Mock
    private Logger LOG;
    
    @Mock
    private EntityManager em;
    
    @Mock 
    private PeerDB peerDB;
    
    @Mock
    private PostDB postDB;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        db = new MainDB();
        db.setEntityManager(em);
        db.setPeerDB(peerDB);
        db.setPostDB(postDB);
        
        pc = new PostController();
        pc.setMainDB(db);
        pc.setLog(LOG);
        pc.setPeerController(peerController);
    }

    //Happy path
    @Test
    public void newPost() throws Exception {
        String UUID_peer = "id_peer";
        String UUID_referTo = "if_referTo";
        String message = "message";
        String url = "http://url.com";
        
        Peer peer = new Peer();
        peer.setUUID(UUID_peer);
        Post referTo = new Post();
        referTo.setUUID(UUID_referTo);
        when(peerController.getPeer(UUID_peer)).thenReturn(peer);
        when(db.getPostDB().findById(UUID_referTo)).thenReturn(referTo);
        
        Post post = pc.newPost(UUID_peer, message, UUID_referTo, url);
        assertEquals(post.getFrom(),peer);
        assertEquals(post.getMessage(),message);
        assertEquals(post.getReferTo(),referTo);
        assertEquals(post.getUrl(),url);
        verify(em).persist((Peer)Matchers.anyObject());
    }

    // PersistenceException
    @Test
    public void newPostException() throws Exception {
        String UUID_peer = "id_peer";
        String UUID_referTo = "if_referTo";
        String message = "message";
        String url = "http://url.com";
        
        Peer peer = new Peer();
        peer.setUUID(UUID_peer);
        Post referTo = new Post();
        referTo.setUUID(UUID_referTo);
        when(peerController.getPeer(UUID_peer)).thenReturn(peer);
        when(db.getPostDB().findById(UUID_referTo)).thenReturn(referTo);
        doThrow(new TransactionRequiredException()).when(em).persist(Matchers.anyObject());
        try {
            Post post = pc.newPost(UUID_peer, message, UUID_referTo, url);
            fail();
        } catch (PersistenceException e) {
            // fine
        } catch (Exception e) {
            fail();
        }
    }
}
