package com.peersnet.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.peersnet.core.data.MainDB;
import com.peersnet.core.data.PeerDB;
import com.peersnet.core.data.PostDB;
import com.peersnet.core.data.PostWindowDB;
import com.peersnet.core.model.Peer;
import com.peersnet.core.model.Post;
import com.peersnet.core.model.PostWindow;

public class PostWindowControllerTest {
    private PostWindowController pwc;
    private MainDB db;
    
    @Mock
    private PeerController peerController;
    
    @Mock
    private PostController postController;
    
    @Mock
    private Logger LOG;
    
    @Mock
    private EntityManager em;
    
    @Mock 
    private PeerDB peerDB;
    
    @Mock
    private PostDB postDB;

    @Mock
    private PostWindowDB postWindowDB;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        db = new MainDB();
        db.setEntityManager(em);
        db.setPeerDB(peerDB);
        db.setPostDB(postDB);
        db.setPostWindowDB(postWindowDB);
        
        pwc = new PostWindowController();
        pwc.setMainDB(db);
        pwc.setLog(LOG);
        pwc.setPeerController(peerController);
        pwc.setPostController(postController);
    }
    
    //Happy path
    @Test
    public void newPostWindow() throws Exception {
        String UUID_to = "uuid_to";
        String UUID_post = "uuid_post";
        Peer to = new Peer();
        to.setUUID(UUID_to);
        Post post = new Post();
        post.setUUID(UUID_post);
        when(peerController.getPeer(UUID_to)).thenReturn(to);
        when(postController.getPost(UUID_post)).thenReturn(post);
        
        PostWindow postWin = pwc.newPostWindow(UUID_post, UUID_to);
        assertEquals(postWin.getTo(), to);
        assertEquals(postWin.getPost(), post);
        assertEquals(postWin.getState(), PostWindow.State.PENDING);
        assertTrue(postWin.getLastChange()!=null);
        verify(em).persist((PostWindow)Matchers.anyObject());
    }
    
    //Persistance Exception
    @Test
    public void newPostWindowException() throws Exception {
        String UUID_to = "uuid_to";
        String UUID_post = "uuid_post";
        Peer to = new Peer();
        to.setUUID(UUID_to);
        Post post = new Post();
        post.setUUID(UUID_post);
        when(peerController.getPeer(UUID_to)).thenReturn(to);
        when(postController.getPost(UUID_post)).thenReturn(post);
        doThrow(new TransactionRequiredException()).when(em).persist(Matchers.anyObject());
        try {
            PostWindow postWin = pwc.newPostWindow(UUID_post, UUID_to);
            fail();
        } catch (PersistenceException e) {
            // fine
        } catch (Exception e) {
            fail();
        }
    }
    
    //Happy Path
    @Test
    public void changeState() throws Exception {
        Long id = new Long(10L);
        PostWindow.State currState =  PostWindow.State.PENDING;
        PostWindow.State newState =  PostWindow.State.SENT;
        PostWindow postWin = new PostWindow();
        postWin.setId(id);
        postWin.setState(currState);
        postWin.setLastChange(null);
        when(db.getPostWindowDB().findById(id)).thenReturn(postWin);
        
        pwc.changeState(id, newState);
        assertEquals(postWin.getState(),newState);
        assertTrue(postWin.getLastChange()!=null);
        verify(em).persist(postWin);
    }
    
    //Exception when same state is passed
    @Test
    public void changeStateException() throws Exception {
        Long id = new Long(10L);
        PostWindow.State currState =  PostWindow.State.PENDING;
        PostWindow.State newState =  PostWindow.State.PENDING;
        PostWindow postWin = new PostWindow();
        postWin.setId(id);
        postWin.setState(currState);
        postWin.setLastChange(null);
        when(db.getPostWindowDB().findById(id)).thenReturn(postWin);
        
        try {
            pwc.changeState(id, newState);
            fail();
        } catch (PersistenceException e) {
            // fine
        } catch (Exception e) {
            fail();
        }
    }
    
  //Exception when persist error
    @Test
    public void changeStateException2() throws Exception {
        Long id = new Long(10L);
        PostWindow.State currState =  PostWindow.State.SENT;
        PostWindow.State newState =  PostWindow.State.ACK;
        PostWindow postWin = new PostWindow();
        postWin.setId(id);
        postWin.setState(currState);
        postWin.setLastChange(null);
        when(db.getPostWindowDB().findById(id)).thenReturn(postWin);
        doThrow(new TransactionRequiredException()).when(em).persist(Matchers.anyObject());
        try {
            pwc.changeState(id, newState);
            fail();
        } catch (PersistenceException e) {
            // fine
        } catch (Exception e) {
            fail();
        }
    }
}
