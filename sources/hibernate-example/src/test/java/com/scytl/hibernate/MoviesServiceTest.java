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
    @EJB
    MoviesBatchService moviesBatchService;

    @Resource
    UserTransaction userTransaction;

    @Test
    public void shouldBatch() throws Exception {
        moviesBatchService.createMoviesInSeveralTransactions();
    }

    @Test
    @ShouldMatchDataSet("datasets/expected-movies.yml")
    public void shouldCreateMovies() {
        Movie theMatrix = new Movie();
        theMatrix.setTitle("The Matrix");
        theMatrix.setReleasedYear(1999);

        moviesService.createMovie(theMatrix);

        Movie theMatrixReloaded = new Movie();
        theMatrixReloaded.setTitle("The Matrix Reloaded");
        theMatrixReloaded.setReleasedYear(2003);

        moviesService.createMovie(theMatrixReloaded);
    }

    // tag::onetoone[]
    @Test
    @ShouldMatchDataSet("datasets/expected-movies-with-detail.yml")
    public void shouldCreateMoviesWithDetails() {
        MovieDetail movieDetail = new MovieDetail();
        movieDetail.setProducedBy("Joel Silver");
        movieDetail.setDirectedBy("The Wachowski Brothers");

        Movie theMatrix = new Movie();
        theMatrix.setTitle("The Matrix");
        theMatrix.setReleasedYear(1999);
        theMatrix.setMovieDetail(movieDetail);

        moviesService.createMovie(theMatrix); // <1>
    }

    @Test
    @UsingDataSet("datasets/movies-with-detail.yml")
    public void shouldRemoveDetails() {
        moviesService.removeMovieDetail(1); // <2>
        Movie movie = moviesService.findMovieById(1);
        assertThat(movie.getMovieDetail(), nullValue());
    }

    // end::onetoone[]

    // tag::onetomany[]
    @Test
    @ApplyScriptBefore("scripts/drop-referential-integrity.sql")
    // <1>
    @ShouldMatchDataSet("datasets/expected-movies-with-comments.yml")
    public void shouldInsertMoviesAndComments() {
        Comment comment = new Comment();
        comment.setReview("Awesome movie");

        Movie theMatrix = new Movie();
        theMatrix.setTitle("The Matrix");
        theMatrix.setReleasedYear(1999);
        theMatrix.addComment(comment);

        moviesService.createMovie(theMatrix); // <2>
    }

    // end::onetomany[]

    // tag::jpql[]
    @Test
    @UsingDataSet("datasets/movies.yml")
    public void shouldFindMoviesByTitle() {
        Movie expectedMovie = new Movie();
        expectedMovie.setTitle("The Matrix");
        expectedMovie.setReleasedYear(1999);
        List<Movie> movies = moviesService.findMoviesByTitle("The Matrix");
        assertThat(movies, hasItem(expectedMovie));
    }

    // end::jpql[]

    // tag::criteria[]
    @Test
    @UsingDataSet("datasets/movies.yml")
    public void shouldFindMoviesByTitleCriteria() {
        Movie expectedMovie = new Movie();
        expectedMovie.setTitle("The Matrix");
        expectedMovie.setReleasedYear(1999);
        List<Movie> movies =
            moviesService.findMoviesByTitleCriteria("The Matrix");
        assertThat(movies, hasItem(expectedMovie));
    }

    // end::criteria[]

    // tag::metamodel[]
    @Test
    @UsingDataSet("datasets/movies.yml")
    public void shouldFindMoviesByTitleCriteriaMetamodel() {
        Movie expectedMovie = new Movie();
        expectedMovie.setTitle("The Matrix");
        expectedMovie.setReleasedYear(1999);
        List<Movie> movies =
            moviesService.findMoviesByTitleCriteriaMetamodel("The Matrix");
        assertThat(movies, hasItem(expectedMovie));
    }

    // end::metamodel[]

    @Test
    @UsingDataSet("datasets/movies.yml")
    public void shouldFindAllMovies() {
        Movie expectedMovie = new Movie();
        expectedMovie.setTitle("The Matrix");
        expectedMovie.setReleasedYear(1999);
        List<Movie> movies = moviesService.findAllMovies();
        assertThat(movies, hasItem(expectedMovie));
    }

    // tag::version[]
    @Test(expected=RollbackException.class)
    @Transactional(value = TransactionMode.DISABLED) //<1>
    @UsingDataSet("datasets/movies-with-version.yml")
    public void shouldThrowAnOptimisticLockingException()
            throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        userTransaction.begin();
        Movie movie = moviesService.findMovieById(1L);

        Future<Movie> modifyMovie = executor.submit(() -> { //<2>
            userTransaction.begin();
            Movie movie2 = moviesService.findMovieById(1L);
            movie2.setReleasedYear(2001);
            userTransaction.commit();
            return movie2;
        });
        modifyMovie.get();
        movie.setReleasedYear(2000);
        userTransaction.commit(); //<3>
    }
    // end::version[]

    // tag::workingversion[]
    @Test
    @UsingDataSet("datasets/movies-with-version.yml")
    @ShouldMatchDataSet("datasets/expected-movies-with-version.yml")
    public void shouldUpdateVersionField()
            throws Exception {
        Movie movie = moviesService.findMovieById(1L);
        movie.setReleasedYear(2000);
    }
    // end::workingversion[]
}
