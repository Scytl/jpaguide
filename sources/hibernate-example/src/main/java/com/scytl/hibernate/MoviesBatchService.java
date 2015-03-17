package com.scytl.hibernate;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

// tag::simplebatch[]
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MoviesBatchService {

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    UserTransaction transaction;

// end::simplebatch[]
// tag::multipletx[]
    public void createMoviesInSeveralTransactions() throws Exception {
        transaction.begin();
        for(int i=1;i<10000;i++) {
            Movie movie = new Movie();
            movie.setTitle(Integer.toString(i));
            movie.setReleasedYear(1901);
            entityManager.persist(movie);
            if ((i % 10000) == 0) {
                transaction.commit(); //<1>
                entityManager.clear();
                transaction.begin();
            }
        }
        transaction.commit();
    }
// end::multipletx[]

 // tag::simplebatch[]
    public void createMoviesInBatch() throws Exception {
       transaction.begin();
       for(int i=1;i<10000;i++) {
           Movie movie = new Movie();
           movie.setTitle(Integer.toString(i));
           movie.setReleasedYear(1901);
           entityManager.persist(movie);
           if ((i % 10000) == 0) {
               entityManager.flush(); //<1>
               entityManager.clear();
           }
       }
       transaction.commit();
    }

}
// end::simplebatch[]