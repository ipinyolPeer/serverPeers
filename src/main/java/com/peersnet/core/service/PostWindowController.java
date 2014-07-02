package com.peersnet.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import com.peersnet.core.data.MainDB;
import com.peersnet.core.model.Peer;
import com.peersnet.core.model.Post;
import com.peersnet.core.model.PostWindow;

/**
 * @author ipinyol
 *
 * The controller class for PostWindow entities
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PostWindowController extends AbstractController {
    @Inject 
    private PeerController peerController;
    
    @Inject
    private PostController postController;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PostWindow newPostWindow(String UUID_post, String UUID_to) throws EntityNotFoundException, PersistenceException {
        Post post = postController.getPost(UUID_post);
        Peer peer = peerController.getPeer(UUID_to);
        PostWindow postWin = new PostWindow();
        postWin.setLastChange(new Date());
        postWin.setPost(post);
        postWin.setTo(peer);
        postWin.setState(PostWindow.State.PENDING);
        try {
            this.db.makePersistent(postWin);
        } catch (Exception e) {
            throw new PersistenceException();
        }
        return postWin;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changeState(Long id, PostWindow.State state) throws EntityNotFoundException, PersistenceException {
        PostWindow postWin = this.db.getPostWindowDB().findById(id);
        if (postWin.getState().equals(state)) {
            throw new PersistenceException("changeState called with the same state");
        }
        chageStateBasic(postWin, state);
    }
    
    // Transaction is required!
    private void chageStateBasic(PostWindow postWin, PostWindow.State state) throws PersistenceException {
        postWin.setState(state);
        postWin.setLastChange(new Date());
        try {
            this.db.makePersistent(postWin);
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }
    
    /**
     * Return the PostWindow entity given its Id 
     * @param id                            The Id
     * @return                              {@link PostWindow}
     * @throws EntityNotFoundException      If Id does not exists
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PostWindow getPostWindow(Long id) throws EntityNotFoundException {
        PostWindow postWin = this.db.getPostWindowDB().findById(id);
        if (postWin == null) {
            throw new EntityNotFoundException();
        }
        return postWin;
    }
    
    /**
     * Remove from DB the PostWindows
     * @param Id
     * @throws EntityNotFoundException  If Id is not found 
     * @throws PersistenceException     If error during delete operation
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePostWindow(Long id) throws EntityNotFoundException, PersistenceException {
        PostWindow postWin = this.getPostWindow(id);
        try {
            this.db.delete(postWin);
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }
    
    /**
     * Get the List of {@link Post} whose destiny is UUID_to and with state currState
     * and updates the state of all PostWindow entities to newState
     * @param UUID_to       The UUID of the Peer
     * @param currState     The state of the PostWindow 
     * @param newState      The new state to be updated
     * @return              List of Post messages for Peer with state currState
     * @throws PersistenceException When there is an error trying to persist the {@link PostWindow} entity 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Post> getPostsByToAndState(String UUID_to, PostWindow.State currState, PostWindow.State newState) throws PersistenceException{
        List<PostWindow> postWinList = db.getPostWindowDB().findByToAndState(UUID_to, currState);
        List<Post> posts = new ArrayList<Post>();
        for (PostWindow postWin:postWinList) {
            Post post = postWin.getPost();
            posts.add(post);
            if (currState!=newState) {
                chageStateBasic(postWin, newState);
            }
        }
        return posts;
    }
    
    // Only for testing purposes
    public void setPeerController(PeerController peerController) {
        this.peerController = peerController;
    }
    
    public void setPostController(PostController postController) {
        this.postController = postController;
    }
}