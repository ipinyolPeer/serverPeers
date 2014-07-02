package com.peersnet.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
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
    
    // Happy path
    @Test
    public void getPostsByToAndState() throws Exception {
        Peer peer = createPeer("Peer");
        Peer peerA = createPeer("PeerA");
        Peer peerB = createPeer("PeerB");
        Post postX = createPost("PostX", peerA);
        Post postY = createPost("PostY", peerA);
        Post postZ = createPost("PostY", peerB);
        PostWindow postWin1 = createPostWindow(1L, peer, postX, PostWindow.State.PENDING);
        PostWindow postWin2 = createPostWindow(2L, peer, postY, PostWindow.State.PENDING);
        PostWindow postWin3 = createPostWindow(3L, peer, postZ, PostWindow.State.PENDING);
        List<PostWindow> postWins = new ArrayList<PostWindow>();
        postWins.add(postWin1);
        postWins.add(postWin2);
        postWins.add(postWin3);
        when(db.getPostWindowDB().findByToAndState(peer.getUUID(), PostWindow.State.PENDING)).thenReturn(postWins);
        
        List<Post> posts = pwc.getPostsByToAndState(peer.getUUID(),PostWindow.State.PENDING , PostWindow.State.SENT);
        assertEquals(3, posts.size());
        for (PostWindow postWin: postWins) {
            assertEquals(PostWindow.State.SENT, postWin.getState());
        }
        verify(em).persist(postWin1);
        verify(em).persist(postWin2);
        verify(em).persist(postWin3);
    }
    
    // Happy path - when the state is equal, no persist is launch
    @Test
    public void getPostsByToAndStateEqualStates() throws Exception {
        Peer peer = createPeer("Peer");
        Peer peerA = createPeer("PeerA");
        Peer peerB = createPeer("PeerB");
        Post postX = createPost("PostX", peerA);
        Post postY = createPost("PostY", peerA);
        Post postZ = createPost("PostY", peerB);
        PostWindow postWin1 = createPostWindow(1L, peer, postX, PostWindow.State.PENDING);
        PostWindow postWin2 = createPostWindow(2L, peer, postY, PostWindow.State.PENDING);
        PostWindow postWin3 = createPostWindow(3L, peer, postZ, PostWindow.State.PENDING);
        List<PostWindow> postWins = new ArrayList<PostWindow>();
        postWins.add(postWin1);
        postWins.add(postWin2);
        postWins.add(postWin3);
        when(db.getPostWindowDB().findByToAndState(peer.getUUID(), PostWindow.State.PENDING)).thenReturn(postWins);
        
        List<Post> posts = pwc.getPostsByToAndState(peer.getUUID(),PostWindow.State.PENDING , PostWindow.State.PENDING);
        assertEquals(3, posts.size());
        for (PostWindow postWin: postWins) {
            assertEquals(PostWindow.State.PENDING, postWin.getState());
        }
        verify(em,times(0)).persist((PostWindow)Matchers.anyObject());
    }
    
    public PostWindow createPostWindow(Long id, Peer toPeer, Post post, PostWindow.State state) {
        PostWindow postWin = new PostWindow();
        postWin.setId(id);
        postWin.setTo(toPeer);
        postWin.setState(state);
        postWin.setPost(post);
        return postWin;
    }
    
    public Peer createPeer(String UUID) {
        Peer peer = new Peer();
        peer.setUUID(UUID);
        return peer;
    }
    
    public Post createPost(String UUID, Peer fromPeer) {
        Post post = new Post();
        post.setUUID(UUID);
        post.setFrom(fromPeer);
        return post;
    }
}
