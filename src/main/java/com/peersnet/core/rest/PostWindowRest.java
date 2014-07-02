package com.peersnet.core.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.peersnet.core.model.Peer;
import com.peersnet.core.model.Post;
import com.peersnet.core.model.PostWindow;
import com.peersnet.core.rest.PeerRest.PeerDTO;
import com.peersnet.core.service.PeerController;
import com.peersnet.core.service.PostController;
import com.peersnet.core.service.PostWindowController;
import com.peersnet.core.util.Constants;

@Path(Constants.baseURL)
@RequestScoped
@Stateful
public class PostWindowRest {
    @Inject 
    PostWindowController pwc;
    
    @Inject 
    PeerController peerController;
    
    @Inject
    PostController postController;
    
    /**
     * Post a new update for a list of Peers
     * @param sc
     * @param postWinDTO
     * @return
     */
    @POST
    @Path(Constants.post)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newPost(@Context SecurityContext sc, PostWindowDTO postWinDTO) {
        
        // We check that the user is authenticated
        if (sc.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (sc.getUserPrincipal().getName()==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String UUID_from = sc.getUserPrincipal().getName();
        
        // We check that the peer exists
        try {
            Peer p = peerController.getPeer(UUID_from);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();        
        }
        // We check that if UUID_referTo is informed,the Post really exists
        if (postWinDTO.UUID_referTo!=null) {
            try {
                Post p = postController.getPost(postWinDTO.UUID_referTo);
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        
        // we check that all parameters are there
        if(postWinDTO.message==null || postWinDTO.UUID_toList==null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // we check that the UUID_toList is not empty
        if(postWinDTO.UUID_toList.size()==0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // Now we start the real task
        Post post = null;
        try {
            post = postController.newPost(UUID_from, postWinDTO.message, postWinDTO.UUID_referTo, postWinDTO.url);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        int count = 0;
        List<String> notSent = new ArrayList<String>();
        for(String UUID_peerTo:postWinDTO.UUID_toList) {
            try {
                pwc.newPostWindow(post.getUUID(), UUID_peerTo);
                count++;
            } catch (Exception e) {  
                notSent.add(UUID_peerTo); 
            }
        }
        ReturnPostDTO ret = new ReturnPostDTO();
        ret.numberOfMessages = count;
        ret.notSent = notSent;
        return Response.status(Response.Status.OK).entity(ret).build();
    }
    
    /**
     * Gets the Posts of state PENDING for the logged in Peer, and changes the state to SENT
     * @param sc The SecurityContext
     * @return
     */
    @GET
    @Path(Constants.getPost)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPosts(@Context SecurityContext sc) {
        // We check that the user is authenticated
        if (sc.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (sc.getUserPrincipal().getName()==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String UUID = sc.getUserPrincipal().getName();
        List<Post> posts = null;
        try {
            posts = pwc.getPostsByToAndState(UUID, PostWindow.State.PENDING, PostWindow.State.SENT);
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        ReturnPostMessagesDTO ret = new ReturnPostMessagesDTO();
        ret.convert(posts);
        return Response.status(Response.Status.OK).entity(ret).build();
    }
    
    /**
     * POST to indicate that all posts after the call getPosts have been received by the logged in Peer 
     * @param sc The SecurityContext
     * @return
     */
    @POST
    @Path(Constants.getPost)
    @Produces(MediaType.APPLICATION_JSON)
    public Response receivedAllPosts(@Context SecurityContext sc) {
        if (sc.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (sc.getUserPrincipal().getName()==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String UUID = sc.getUserPrincipal().getName();
        try {
            pwc.getPostsByToAndState(UUID, PostWindow.State.SENT, PostWindow.State.ACK);
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }
    
    static public class ReturnPostMessagesDTO {
        public List<ReturnPostMessageDTO> postsDTO;
        
        public void convert(List<Post> posts) {
            postsDTO = new ArrayList<ReturnPostMessageDTO>();
            for(Post post:posts) {
                ReturnPostMessageDTO postDTO = new ReturnPostMessageDTO();
                postDTO.UUID = post.getUUID();
                postDTO.message = post.getMessage();
                postDTO.creationDate = post.getCreationDate();
                postDTO.UUID_from = post.getFrom().getUUID();
                postDTO.UUID_referTo = post.getReferTo().getUUID();
                postsDTO.add(postDTO);
            }
        }
    }
    
    static public class ReturnPostMessageDTO {
        String UUID;
        Date creationDate;
        String UUID_from;
        String message;
        String UUID_referTo;
    }
    
    static public class ReturnPostDTO{
        int numberOfMessages;
        List<String> notSent;
    }
    
    static public class PostWindowDTO {
        public String message;
        public String url;
        public String UUID_referTo;
        public List<String> UUID_toList;
    }
}
