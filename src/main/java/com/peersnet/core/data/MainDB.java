package com.peersnet.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class MainDB {

	@Inject private EntityManager em;
	
	public void makePersistent(Object obj) throws Exception {
	    	em.persist(obj);
	    	em.flush();
	    	
	 }
	
}
