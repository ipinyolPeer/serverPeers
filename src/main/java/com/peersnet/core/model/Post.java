package com.peersnet.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

public class Post {
    
    // The ID. We use UUIDs
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "uuid", unique = true)
    private String UUID;
    
    // The source of the post
    @ManyToOne(optional=false) 
    @JoinColumn(name="UUID", nullable=false, updatable=true)
    private Peer from;
    
    // The literal message
    @Column(length = 255)
    @NotNull
    private String message;
    
    // A possible url. The limit of IE is 2083, which is the longest...
    @Column(length = 3000)
    private String url;
    
    // A possible reference to another post
    @ManyToOne(optional=true) 
    @JoinColumn(name="UUID", nullable=true, updatable=true)
    private Post referTo;
    
    // The TIMESTAMP of the creation of the post
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    public String getUUID() {
        return this.UUID;
    }
    
    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
    
    public Peer getFrom() {
        return this.from;
    }
    
    public void setFrom(Peer from) {
        this.from = from;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Post getReferTo() {
        return this.referTo;
    }
    
    public void setReferTo(Post referTo) {
        this.referTo = referTo;
    }
    
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
