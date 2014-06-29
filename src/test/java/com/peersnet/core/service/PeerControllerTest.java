package com.peersnet.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Matchers;

import com.peersnet.core.data.MainDB;
import com.peersnet.core.data.PeerDB;
import com.peersnet.core.model.Peer;

public class PeerControllerTest {
    
    private PeerController pc;
    private MainDB db;
    
    @Mock
    private Logger LOG;
    
    @Mock
    private EntityManager em;
    
    @Mock 
    private PeerDB peerDB;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        db = new MainDB();
        db.setEntityManager(em);
        db.setPeerDB(peerDB);
        
        pc = new PeerController();
        pc.setMainDB(db);
        pc.setLog(LOG);
    }

    @Test
    public void newPeerTest() throws Exception {
        String eMail = "test@test.com";
        Peer peer = pc.newPeer(eMail);
        assertEquals(peer.getEMail(), eMail);
        assertEquals(peer.getCreationDate(), peer.getLastConnection());
        assertEquals(peer.getLastMessage(), null);
        verify(em).persist((Peer)Matchers.anyObject());
    }
    
    //Happy Path setLastConnection
    @Test
    public void setLastConnectionTest() throws Exception {
        String UUID = "ident";
        Peer peer = createPeer(UUID);
        when(db.getPeerDB().findById(UUID)).thenReturn(peer);
        pc.setLastConnection(UUID);
        
        assertTrue(peer.getLastConnection() != null);
        verify(em).persist((Peer)Matchers.anyObject());
    }
    
    //Exception Path one setLastConnection
    @Test
    public void setLastConnectionExceptionTest() throws Exception {
        String UUID = "ident";
        when(db.getPeerDB().findById(UUID)).thenReturn(null);
        try {
            pc.setLastConnection(UUID);
            fail();
        } catch (EntityNotFoundException e) {
            // Fine
        } catch (Exception e) {
            fail();
        }
        verify(em,times(0)).persist((Peer)Matchers.anyObject());
    }

    //Exception Path two setLastConnection
    @Test
    public void setLastConnectionException2Test() throws Exception {
        String UUID = "ident";
        Peer peer = createPeer(UUID);
        when(db.getPeerDB().findById(UUID)).thenReturn(peer);
        doThrow(new TransactionRequiredException()).when(em).persist(peer);
        try {
            pc.setLastConnection(UUID);
            fail();
        } catch (PersistenceException e) {
            // Fine
        } catch (Exception e) {
            fail();
        }
    }
    
    
  //Happy Path setLastMessage
    @Test
    public void setLastMessageTest() throws Exception {
        String UUID = "ident";
        Peer peer = createPeer(UUID);
        when(db.getPeerDB().findById(UUID)).thenReturn(peer);
        pc.setLastMessage(UUID);
        
        assertTrue(peer.getLastMessage() != null);
        verify(em).persist((Peer)Matchers.anyObject());
    }
    
    //Exception Path one setLastConnection
    @Test
    public void setLastMessageExceptionTest() throws Exception {
        String UUID = "ident";
        when(db.getPeerDB().findById(UUID)).thenReturn(null);
        try {
            pc.setLastMessage(UUID);
            fail();
        } catch (EntityNotFoundException e) {
            // Fine
        } catch (Exception e) {
            fail();
        }
        verify(em,times(0)).persist((Peer)Matchers.anyObject());
    }

    //Exception Path two setLastConnection
    @Test
    public void setLastMessageException2Test() throws Exception {
        String UUID = "ident";
        Peer peer = createPeer(UUID);
        when(db.getPeerDB().findById(UUID)).thenReturn(peer);
        doThrow(new TransactionRequiredException()).when(em).persist(peer);
        try {
            pc.setLastMessage(UUID);
            fail();
        } catch (PersistenceException e) {
            // Fine
        } catch (Exception e) {
            fail();
        }
    }
    
    private Peer createPeer(String UUID) {
        Peer peer = new Peer();
        peer.setUUID(UUID);
        return peer;
    }
}
