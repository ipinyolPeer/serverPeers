package com.peersnet.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Peer implements Serializable{

    // The ID. We use UUIDs
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "uuid", unique = true)
    private String UUID;
    
    // The TIMESTAMP of the last message sent
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMessage;
    
    // The TIMESTAMP of the last connection
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date lastConnection;
    
    // The TIMESTAMP of the last connection
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date creationDate;
    
    @NotNull
    @NotEmpty
    @Email(message = "Invalid format")
    @Column(unique = true)
    private String eMail;
    
    public String getUUID() {
        return this.UUID;
    }
    
    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
    
    public Date getLastMessage() {
        return this.lastMessage;
    }
    
    public void setLastMessage(Date lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public Date getLastConnection() {
        return this.lastConnection;
    }
    
    public void setLastConnection(Date lastConnection) {
        this.lastConnection = lastConnection;
    }
    
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }
    private static final long serialVersionUID = 1L;

}
