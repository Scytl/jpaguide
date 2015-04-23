package com.scytl.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Proxy;

@Entity
@NamedQueries({
    @NamedQuery(name="Movie.findAll", query="SELECT m FROM Movie m"),
    @NamedQuery(name="Movie.findByTitle", 
                query="SELECT m FROM Movie m WHERE m.title = :title") //<1>
})
// tag::dynamicupdate[]
@Proxy(lazy=true)
public class Movie {
// end::dynamicupdate[]
    @Id @GeneratedValue
    private long id;

    @NotNull
    private String title;

    @Min(1900)
    private int releasedYear;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true) //<1>
    private MovieDetail movieDetail;

    public MovieDetail getMovieDetail() {
        return movieDetail;
    }

    public void setMovieDetail(MovieDetail movieDetail) { //<2>
        this.movieDetail = movieDetail;
        movieDetail.setMovie(this);
    }
    public void removeMovieDetail() { //<3>
        if(this.movieDetail != null) {
            this.movieDetail.setMovie(null);
        }
        this.movieDetail = null;
    }

    @OneToMany(mappedBy="movie", cascade = CascadeType.ALL, orphanRemoval = true) //<1>
    private List<Comment> comments = new ArrayList<Comment>();

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    public List<Comment> getComments() {
        return comments;
    }
    public void addComment(Comment comment) { //<2>
        comments.add(comment);
        comment.setMovie(this);
    }
    public void removeComment(Comment comment) { //<3>
        comment.setMovie(null);
        comments.remove(comment);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setReleasedYear(int releasedYear) {
        this.releasedYear = releasedYear;
    }

    public int getReleasedYear() {
        return releasedYear;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + releasedYear;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        Movie other = (Movie) obj;
        if (releasedYear != other.releasedYear)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Movie [title=" + title + ", releasedYear=" + releasedYear
            + "]";
    }
 // tag::dynamicupdate[]
}
// end::dynamicupdate[]
