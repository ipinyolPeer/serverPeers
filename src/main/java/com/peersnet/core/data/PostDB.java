package com.peersnet.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.peersnet.core.model.Post;

/**
 * the Post DB repository, to access Posts
 * @author ipinyol
 *
 */
@RequestScoped
public class PostDB {

    @Inject
    private EntityManager em;
    
    /**
     * Returns a {@link Post} from the given UUID
     * @param UUID The UUID of the {@link Post}
     * @author ipinyol
     */
    public Post findById(String UUID) {
        em.flush();
        try {
            return em.find(Post.class, UUID);
        } catch (Exception e) {
            return null;
        }
    }
}
