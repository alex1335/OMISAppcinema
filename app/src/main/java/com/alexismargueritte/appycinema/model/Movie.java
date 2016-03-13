package com.alexismargueritte.appycinema.model;


import org.json.JSONArray;

import java.util.ArrayList;

public class Movie {
    private String title, thumbnailUrl, year, realisateur, genre, synopsis;
    private JSONArray listMedias;

    public Movie() {
    }

    public Movie(String name, String thumbnailUrl, String year, String realisateur,
                 String genre, String synopsis, JSONArray listMedias) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.year = year;
        this.realisateur = realisateur;
        this.genre = genre;
        this.synopsis = synopsis;
        this.listMedias = listMedias;
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

    public JSONArray getListMedias() {
        return listMedias;
    }

    public void setListMedias(JSONArray listMedias) {
        this.listMedias = listMedias;
    }

    public String getPath(ArrayList listpath, int i) {
        if (i + 1 > listpath.size()) {
            return "";
        } else {
            return (String) listpath.get(i);
        }
    }
}