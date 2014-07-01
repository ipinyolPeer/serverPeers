package com.peersnet.core.data;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.peersnet.core.model.Post;
import com.peersnet.core.model.PostWindow;

/**
 * the PostWindow DB repository, to access PostWindow table
 * @author ipinyol
 *
 */
@RequestScoped
public class PostWindowDB {
    @Inject
    private EntityManager em;
    
    /**
     * Returns a {@link PostWindow} from the given Id
     * @param id The Id of the {@link PostWindow}
     * @author ipinyol
     */
    public PostWindow findById(Long id) {
        em.flush();
        try {
            return em.find(PostWindow.class, id);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Returns a List of {@link postWindow} whose destiny is the PEER with UUID
     * @param UUID
     * @return
     */
    public List<PostWindow> findByTo(String UUID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PostWindow> criteria = cb.createQuery(PostWindow.class);
        Root<PostWindow> postWindow = criteria.from(PostWindow.class);
        Predicate p1 = cb.equal(postWindow.get("to").get("UUID"), UUID);      
        criteria.select(postWindow).where(p1);
        List<PostWindow> out = em.createQuery(criteria).getResultList();
        return out;
    }
    
    /**
     * Returns a List of {@link postWindow} whose destiny is the PEER with UUID
     * @param UUID
     * @return
     */
    public List<PostWindow> findByToAndState(String UUID, PostWindow.State state) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PostWindow> criteria = cb.createQuery(PostWindow.class);
        Root<PostWindow> postWindow = criteria.from(PostWindow.class);
        Predicate p1 = cb.equal(postWindow.get("to").get("UUID"), UUID);      
        Predicate p2 = cb.equal(postWindow.get("state"), state);
        criteria.select(postWindow).where(cb.and(p1,p2));
        List<PostWindow> out = em.createQuery(criteria).getResultList();
        return out;
    }
}
