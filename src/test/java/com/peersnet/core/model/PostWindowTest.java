package com.peersnet.core.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PostWindowTest {
    PostWindow postWin = new PostWindow();
    Post post = new Post();
    Long id = new Long(12);
    Peer to = new Peer();
    PostWindow.State state = PostWindow.State.SENT;
    Date lastChange = new Date();
    
    @Before
    public void setUp() throws Exception {
        postWin.setId(id);
        postWin.setLastChange(lastChange);
        postWin.setPost(post);
        postWin.setState(state);
        postWin.setTo(to);
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testGetId() {
        assertEquals(id, postWin.getId());
    }
    
    @Test
    public void testGetLastChange() {
        assertEquals(lastChange, postWin.getLastChange());
    }
    
    @Test
    public void testGetPost() {
        assertEquals(post, postWin.getPost());
    }
    
    @Test
    public void testGetState() {
        assertEquals(state, postWin.getState());
    }
   
    @Test
    public void testGetTo() {
        assertEquals(to, postWin.getTo());
    }
}
