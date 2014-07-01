package com.peersnet.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class MainDB {

	@Inject private EntityManager em;
	@Inject private PeerDB peerDB;
	@Inject private PostDB postDB;
	@Inject private PostWindowDB postWindowDB;
	
	public void makePersistent(Object obj) throws Exception {
	    em.persist(obj);
	    em.flush();
	 }
	
	public void delete(Object obj) throws Exception {
	    em.remove(obj);
	    em.flush();
	}
	
	public PeerDB getPeerDB() {
	    return this.peerDB;
	}
	
	public PostDB getPostDB() {
	    return this.postDB;
	}
	
	public PostWindowDB getPostWindowDB() {
	    return this.postWindowDB;
	}
	
	// For testing purposes only
	public void setEntityManager(EntityManager em) {
	    this.em = em;
	}
	
	public void setPeerDB(PeerDB peerDB) {
	    this.peerDB = peerDB;
	}
	
	public void setPostDB(PostDB postDB) {
	    this.postDB = postDB;
	}
	
	public void setPostWindowDB(PostWindowDB postWindowDB) {
	    this.postWindowDB = postWindowDB;
	}
}
