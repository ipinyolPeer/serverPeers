package com.peersnet.core.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

//import org.jboss.shrinkwrap.resolver.DependencyResolvers;
//import org.jboss.shrinkwrap.resolver.MavenDependencyResolver;

import com.peersnet.core.data.MainDB;
import com.peersnet.core.data.PeerDB;
import com.peersnet.core.data.PostDB;
import com.peersnet.core.data.PostWindowDB;
import com.peersnet.core.model.Peer;
import com.peersnet.core.model.Post;
import com.peersnet.core.model.PostWindow;
import com.peersnet.core.service.AbstractController;
import com.peersnet.core.service.PeerController;
import com.peersnet.core.service.PostController;
import com.peersnet.core.service.PostWindowController;
import com.peersnet.core.util.Constants;
import com.peersnet.core.util.MapUtils;
import com.peersnet.core.util.Resources;


public abstract class AbtractIT {
    
    @Deployment
    public static Archive<?> createTestArchive() {
       MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
                .loadMetadataFromPom("pom.xml");  
       return ShrinkWrap.create(WebArchive.class, "test.war")
               .addAsLibraries(resolver.artifact("org.mockito:mockito-all:1.8.3").resolveAsFiles())
               .addClasses(MainDB.class, PostDB.class, PeerDB.class, PostWindowDB.class, Peer.class, Post.class, PostWindow.class,
                       EchoRest.class, JaxRsActivator.class, PeerRest.class, PostWindowRest.class,
                       AbstractController.class, PeerController.class, PostController.class, PostWindowController.class,
                       Constants.class, MapUtils.class, Resources.class,
                       AbtractIT.class, PeerRestIT.class, PostWindowRestIT.class)
               .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
               .addAsWebInfResource("arquillian-ds.xml")
               .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      }
}
