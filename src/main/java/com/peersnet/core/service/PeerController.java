package com.peersnet.core.service;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.peersnet.core.model.Peer;

/**
 * @author ipinyol
 *
 * The controller class for Peers
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PeerController extends AbstractController {

    /**
     * Creates a new Peer ad returns it.
     * @param eMail
     * @return Peer
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Peer newPeer(String eMail) throws Exception {
        Date now = new Date();
        Peer peer = new Peer();
        peer.setEMail(eMail);
        peer.setLastConnection(now);
        peer.setLastMessage(null);
        peer.setCreationDate(now);
        this.db.makePersistent(peer);
        return peer;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setLastConnection(String UUID) throws Exception {
        
    }
    
}
