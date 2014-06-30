package com.peersnet.core.service;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import com.peersnet.core.model.Peer;
import com.peersnet.core.model.Post;

/**
 * @author ipinyol
 *
 * The controller class for Post Messages
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PostController extends AbstractController {

    @Inject 
    PeerController peerController;
    
    /**
     * Creates a new Post
     * @param UUID_from         The id of the {@link Peer} origin
     * @param message           The message
     * @param UUID_referTo      The id of the {@link Post} that this one is refered to 
     * @param url               An optional URL
     * @return The newly created {@link Post}
     * @throws EntityNotFoundException  If UUID_from does not exists or UUID_referTo does not exists 
     * @throws PersistenceException     If an error occurs when persisting the entity
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Post newPost(String UUID_from, String message, String UUID_referTo, String url) throws EntityNotFoundException, PersistenceException {
        Date now = new Date();
        Peer fromPeer = peerController.getPeer(UUID_from);
        Post referToPost = null;
        if (UUID_referTo!=null) {
            referToPost = this.getPost(UUID_referTo);
        }
        Post post = new Post();
        post.setCreationDate(now);
        post.setFrom(fromPeer);
        post.setMessage(message);
        post.setUrl(url);
        post.setReferTo(referToPost);
        try {
            this.db.makePersistent(post);
        } catch (Exception e) {
            throw new PersistenceException();
        }
        return post;
    }
    
    /**
     * Remove from DB the Post
     * @param UUID
     * @throws EntityNotFoundException  If UUID is not found 
     * @throws PersistenceException     If error during delete operation
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePost(String UUID) throws EntityNotFoundException, PersistenceException {
        Post post = this.getPost(UUID);
        try {
            this.db.delete(post);
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }
        
    /**
     * Get the Post element
     * @param UUID
     * @return The {@link Post} element
     * @throws EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Post getPost(String UUID) throws EntityNotFoundException {
        Post post = db.getPostDB().findById(UUID);
        if (post == null) {
            throw new EntityNotFoundException();  
        }
        return post;
    }
    
    // For testing purposes only
    public void setPeerController(PeerController peerController) {
        this.peerController = peerController;
    }
}
