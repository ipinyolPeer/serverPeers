package com.peersnet.core.service;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

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
    
    /**
     * Updates the date of the last connection
     * @param UUID
     * @throws EntityNotFoundException if the entity is not found
     * @throws PersistenceException if there is an error when persisting the entity
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setLastConnection(String UUID) throws EntityNotFoundException, PersistenceException {
        Peer peer = this.db.getPeerDB().findById(UUID);
        if (peer == null) {
            throw new EntityNotFoundException();  
        }
        peer.setLastConnection(new Date());
        try {
            this.db.makePersistent(peer);
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }
    
    /**
     * Updates the date of the last message sent
     * @param UUID
     * @throws EntityNotFoundException if the entity is not found
     * @throws PersistenceException if there is an error when persisting the entity
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setLastMessage(String UUID) throws EntityNotFoundException, PersistenceException {
        Peer peer = this.db.getPeerDB().findById(UUID);
        if (peer == null) {
            throw new EntityNotFoundException();  
        }
        peer.setLastMessage(new Date());
        try {
            this.db.makePersistent(peer);
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }
    
}
