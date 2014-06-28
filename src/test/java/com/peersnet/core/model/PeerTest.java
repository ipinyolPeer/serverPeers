package com.peersnet.core.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PeerTest {
    
    Peer peer;
    String UUID = "IDENT";
    Date creation = new Date();
    Date lastCon = new Date();
    Date lastMessage = new Date();
    String eMail = "test@test.com";
    
    @Before
    public void setUp() throws Exception {
        peer = new Peer();
        peer.setUUID(UUID);
        peer.setCreationDate(creation);
        peer.setLastConnection(lastCon);
        peer.setLastMessage(lastMessage);
        peer.setEMail(eMail);
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testGetUUID() {
        assertEquals(UUID,peer.getUUID());
    }
    
    @Test
    public void testGetLastConnection() {
        assertEquals(lastCon, peer.getLastConnection());
    }
    
    @Test
    public void testGetLastMessage() {
        assertEquals(lastMessage, peer.getLastMessage());
    }
    
    @Test
    public void testCreationDate() {
        assertEquals(creation, peer.getCreationDate());
    }
    
    @Test
    public void testGetEMail() {
        assertEquals(eMail, peer.getEMail());
    }
}
