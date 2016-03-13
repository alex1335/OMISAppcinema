package com.alexismargueritte.appycinema.model;


public class Movie {
    private String title, thumbnailUrl, year, realisateur, genre, synopsis;

    public Movie() {
    }

    public Movie(String name, String thumbnailUrl, String year, String realisateur,
                 String genre, String synopsis) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.year = year;
        this.realisateur = realisateur;
        this.genre = genre;
        this.synopsis = synopsis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDirector() {
        return realisateur;
    }

    public void setDirector(String realisateur) {
        this.realisateur = realisateur;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

}