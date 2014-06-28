package com.peersnet.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * @author ipinyol
 * Implements an N:M relation between Peer and Post, indicating the that message Post
 * must be sent to peer 'to'. It includes a state to indicate whether the message has been received 
 * or not.
 */

@Entity
public class PostWindow implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum State {PENDING, SENT, ACK};

    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(optional=false) 
    @JoinColumn(name="UUID", nullable=false, updatable=true)
    private Peer to;
    
    //@ManyToOne(optional=false) 
    //@JoinColumn(name="UUID", nullable=false, updatable=true)
    private Post post;
    
    @NotNull
    private State state = State.PENDING;    // The default value
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChange;            
    
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Peer getTo() {
        return to;
    }

    public void setTo(Peer to) {
        this.to = to;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }
    
    
}
