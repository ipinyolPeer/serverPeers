package com.peersnet.core.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.peersnet.core.data.MainDB;
import com.peersnet.core.data.PeerDB;
import com.peersnet.core.data.PostDB;
import com.peersnet.core.model.Peer;
import com.peersnet.core.model.Post;
import com.peersnet.core.model.PostWindow;
import com.peersnet.core.service.AbstractController;
import com.peersnet.core.service.PeerController;
import com.peersnet.core.service.PostController;
import com.peersnet.core.util.Constants;
import com.peersnet.core.util.MapUtils;
import com.peersnet.core.util.Resources;

public abstract class AbtractIT {

    @Deployment
    public static Archive<?> createTestArchive() {
       return ShrinkWrap.create(WebArchive.class, "test.war")
               .addClasses(MainDB.class, PostDB.class, PeerDB.class, Peer.class, Post.class, PostWindow.class,
                       EchoRest.class, JaxRsActivator.class, PeerRest.class,
                       AbstractController.class, PeerController.class, PostController.class,
                       Constants.class, MapUtils.class, Resources.class,
                       AbtractIT.class, PeerRestIT.class)
               .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
               .addAsWebInfResource("arquillian-ds.xml")
               .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      }
}
