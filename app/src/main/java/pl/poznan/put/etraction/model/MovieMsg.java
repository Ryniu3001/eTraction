package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Marcin on 14.04.2017.
 */

public class MovieMsg {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("genre")
    private String genre;
    @SerializedName("length")
    private int length;
    @SerializedName("poster")
    private String posterUrl;
    @SerializedName("filename")
    private String filename;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public static class MoviesMsg{
        @SerializedName("movies")
        List<MovieMsg> movies;

        public List<MovieMsg> getMovies() {
            return movies;
        }

        public void setMovies(List<MovieMsg> movies) {
            this.movies = movies;
        }
    }
}
