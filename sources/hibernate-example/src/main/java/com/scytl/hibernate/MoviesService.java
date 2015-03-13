package com.scytl.hibernate;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

// tag::jpql[]
@Stateless
public class MoviesService {

    @PersistenceContext
    EntityManager entityManager;

    // end::jpql[]
    public void createMovie(Movie movie) {
        entityManager.persist(movie);
    }

    public void removeMovieDetail(long id) {
        Movie movie = findMovieById(id);
        movie.removeMovieDetail();
    }

    // tag::jpql[]
    public List<Movie> findMoviesByTitle(String title) {
        TypedQuery<Movie> findMoviesQuery = entityManager.createNamedQuery("Movie.findByTitle", Movie.class); //<1>
        findMoviesQuery.setParameter("title", title); //<2>

        return findMoviesQuery.getResultList(); //<3>
    }

    // end::jpql[]
    public Movie findMovieById(long id) {
        return entityManager.find(Movie.class, id);
    }

    // tag::metamodel[]
    public List<Movie> findMoviesByTitleCriteriaMetamodel(String title) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> query = cb.createQuery(Movie.class);

        Root<Movie> movie = query.from(Movie.class);
        query.where(cb.equal(movie.get(Movie_.title), title)); //<1> <2>
        TypedQuery<Movie> findMoviesQuery = entityManager.createQuery(query);

        return findMoviesQuery.getResultList();
    }
    // end::metamodel[]
    
    // tag::criteria[]
    public List<Movie> findMoviesByTitleCriteria(String title) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); //<1>
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> movie = cq.from(Movie.class); //<2>

        ParameterExpression<String> titleParameter = cb.parameter(String.class); //<3>
        CriteriaQuery<Movie> where = cq.select(movie).where(cb.equal(movie.get("title"), titleParameter)); //<4>
        TypedQuery<Movie> findMoviesQuery = entityManager.createQuery(where);
        findMoviesQuery.setParameter(titleParameter, title);
        return findMoviesQuery.getResultList();
    }

    //end::criteria[]
    public List<Movie> findAllMovies() {
        TypedQuery<Movie> findAllMoviesQuery = entityManager.createNamedQuery("Movie.findAll", Movie.class);
        return findAllMoviesQuery.getResultList();
    }
}
