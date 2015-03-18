package com.scytl.hibernate;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

import org.apache.openejb.OpenEJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MoviesServiceTest {

    @Deployment
    public static WebArchive createDeploymentPackage() {
        WebArchive deploymentFile =
            ShrinkWrap
                .create(WebArchive.class)
                .addPackage("com.scytl.hibernate")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-persistence.xml",
                    "persistence.xml")
                .addAsLibraries(getHibernateDependencies());
        return deploymentFile;
    }

    private static JavaArchive[][] getHibernateDependencies() {

        JavaArchive[][] dependencies = new JavaArchive[3][];

        PomEquippedResolveStage maven =
            Maven.resolver().loadPomFromFile("pom.xml");
        JavaArchive[] hibernateDependencies =
            maven.resolve("org.hibernate:hibernate-core")
                .withTransitivity().as(JavaArchive.class);
        dependencies[0] = hibernateDependencies;
        JavaArchive[] validatorDependencies =
            maven.resolve("org.hibernate:hibernate-validator")
                .withTransitivity().as(JavaArchive.class);
        dependencies[1] = validatorDependencies;
        JavaArchive[] entityManagerDependencies =
            maven.resolve("org.hibernate:hibernate-entitymanager")
                .withTransitivity().as(JavaArchive.class);
        dependencies[2] = entityManagerDependencies;

        return dependencies;
    }

    @EJB
    MoviesService moviesService;

    @Test
    @UsingDataSet("datasets/movies.yml")
    public void shouldUpdateYearImplicitUpdate() {
        moviesService.updateReleasedYearImplicit(1L, 2000);
    }

    @Test
    @UsingDataSet("datasets/movies.yml")
    public void shouldUpdateYearReferenceUpdate() {
        moviesService.updateReleasedYearReference(1L, 2000);
    }

    @Test
    @UsingDataSet("datasets/movies.yml")
    @ShouldMatchDataSet("datasets/expected-movies-update.yml")
    public void shouldUpdateYearMerge() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("The Matrix");
        movie.setReleasedYear(2000);
        moviesService.updateReleasedYear(movie);
    }

    @Test
    @ApplyScriptBefore("scripts/drop-referential-integrity.sql")
    @UsingDataSet("datasets/movies-with-comments.yml")
    @ShouldMatchDataSet("datasets/expected-movies-with-comments.yml")
    public void shouldAddCommentNonePerformant() {
        Comment comment = new Comment();
        comment.setReview("must see");
        moviesService.addCommentNonePerformant(1L, comment);
    }

    @Test
    @ApplyScriptBefore("scripts/drop-referential-integrity.sql")
    @UsingDataSet("datasets/movies-with-comments.yml")
    @ShouldMatchDataSet("datasets/expected-movies-with-comments.yml")
    public void shouldAddCommentPerformant() {
        Comment comment = new Comment();
        comment.setReview("must see");
        moviesService.addCommentPerformant(1L, comment);
    }
    
}
