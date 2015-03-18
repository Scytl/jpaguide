// tag::onetoone[]
package com.scytl.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class MovieDetail {

    @Id @GeneratedValue
    private long id;

    @OneToOne //<1>
    @PrimaryKeyJoinColumn //<2>
    private Movie movie;

    private String directedBy;
    private String producedBy;

    public Movie getMovie() {
        return movie;
    }
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    public String getDirectedBy() {
        return directedBy;
    }
    public void setDirectedBy(String directedBy) {
        this.directedBy = directedBy;
    }
    public String getProducedBy() {
        return producedBy;
    }
    public void setProducedBy(String producedBy) {
        this.producedBy = producedBy;
    }
// end::onetoone[]
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((directedBy == null) ? 0 : directedBy.hashCode());
        result = prime * result + ((movie == null) ? 0 : movie.hashCode());
        result =
            prime * result
                + ((producedBy == null) ? 0 : producedBy.hashCode());
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
        MovieDetail other = (MovieDetail) obj;
        if (directedBy == null) {
            if (other.directedBy != null)
                return false;
        } else if (!directedBy.equals(other.directedBy))
            return false;
        if (movie == null) {
            if (other.movie != null)
                return false;
        } else if (!movie.equals(other.movie))
            return false;
        if (producedBy == null) {
            if (other.producedBy != null)
                return false;
        } else if (!producedBy.equals(other.producedBy))
            return false;
        return true;
    }
// tag::onetoone[]
}
// end::onetoone[]