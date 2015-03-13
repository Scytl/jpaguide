// tag::onetomany[]
package com.scytl.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Comment {

    @Id @GeneratedValue
    private long id;

    @NotNull
    private String review;

    @ManyToOne //<1>
    private Movie movie;

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    public Movie getMovie() {
        return movie;
    }
    public void setReview(String review) {
        this.review = review;
    }
    public String getReview() {
        return review;
    }
// end::onetomany[]
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((movie == null) ? 0 : movie.hashCode());
        result =
            prime * result + ((review == null) ? 0 : review.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Comment other = (Comment) obj;
        if (id != other.id)
            return false;
        if (movie == null) {
            if (other.movie != null)
                return false;
        } else if (!movie.equals(other.movie))
            return false;
        if (review == null) {
            if (other.review != null)
                return false;
        } else if (!review.equals(other.review))
            return false;
        return true;
    }
// tag::onetomany[]
}
// end::onetomany[]