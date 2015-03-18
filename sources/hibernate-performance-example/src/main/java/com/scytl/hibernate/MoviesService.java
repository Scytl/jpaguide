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

@Stateless
public class MoviesService {

    @PersistenceContext
    EntityManager entityManager;

    // tag::merge[]
    public void updateReleasedYear(Movie movie) {
        entityManager.merge(movie); //<1>
    }
    // end::merge[]

    // tag::implicit[]
    public void updateReleasedYearImplicit(Long movieId, int releasedYear) {
        Movie movie = entityManager.find(Movie.class, movieId); //<1>
        movie.setReleasedYear(releasedYear); //<2>
    }
    // end::implicit[]
    
 // tag::reference[]
    public void updateReleasedYearReference(Long movieId, int releasedYear) {
        Movie movie = entityManager.getReference(Movie.class, movieId); //<1>
        movie.setReleasedYear(releasedYear); //<2>
    }
    // end::reference[]
    
    // tag::NPonetomany[]
   public void addCommentNonePerformant(Long id, Comment comment) {
       Movie movie = entityManager.find(Movie.class, id); //<1>
       movie.addComment(comment); //<2>
   }
   // end::NPonetomany[]

   // tag:: Ponetomany[]
   public void addCommentPerformant(Long id, Comment comment) {
       Movie movie = entityManager.getReference(Movie.class, id);
       movie.addComment(comment);
   }
   // end::Ponetomany[]
   public void createMovie(Movie m) {
       entityManager.persist(m);
   }
}
