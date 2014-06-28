package com.peersnet.core.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PostTest {

    Post post;
    String UUID = "UUID";
    Date creation = new Date();
    Peer from = new Peer();
    Post referTo = new Post();
    String url = "http//something";
    
    @Before
    public void setUp() throws Exception {
        post = new Post();
        post.setUUID(UUID);
        post.setCreationDate(creation);
        post.setFrom(from);
        post.setReferTo(referTo);
        post.setUrl(url);
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testGetUUID() {
        assertEquals(UUID,post.getUUID());
    }
    
    @Test
    public void testGetCreationDate() {
        assertEquals(creation, post.getCreationDate());
    }
    
    @Test
    public void testGetFrom() {
        assertEquals(from, post.getFrom());
    }
    
    @Test
    public void testGetReferTo() {
        assertEquals(referTo, post.getReferTo());
    }
    
    @Test
    public void testGetUrl() {
        assertEquals(url, post.getUrl());
    }
}
