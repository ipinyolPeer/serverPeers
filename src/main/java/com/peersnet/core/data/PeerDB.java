package com.peersnet.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.peersnet.core.model.Peer;



/**
 * the Peer DB repository, to access Peers
 * @author ipinyol
 *
 */
@RequestScoped
public class PeerDB {
    @Inject
    private EntityManager em;
    
    /**
     * Returns a {@link Peer} from the given UUID
     * @param UUID The UUID of the {@link Peer}
     * @author ipinyol
     */
    public Peer findById(String UUID) {
        em.flush();
        try {
            return em.find(Peer.class, UUID);
        } catch (Exception e) {
            return null;
        }
    }
}
