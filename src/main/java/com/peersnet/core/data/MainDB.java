package com.peersnet.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class MainDB {

	@Inject private EntityManager em;
	@Inject private PeerDB peerDB;
	
	public void makePersistent(Object obj) throws Exception {
	    	em.persist(obj);
	    	em.flush();
	 }
	
	public PeerDB getPeerDB() {
	    return this.peerDB;
	}
	
	// For testing purposes only
	public void setEntityManager(EntityManager em) {
	    this.em = em;
	}
	
	public void setPeerDB(PeerDB peerDB) {
	    this.peerDB = peerDB;
	}
	
}
