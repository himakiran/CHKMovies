package com.example.android.chkmovies;

import java.util.ArrayList;

/**
 * Created by userhk on 29/11/16.
 */

public class DetailElementsArray {
    public String MovieName;
    public String MoviePoster;
    public String MovieReleaseDate;
    public Integer MovieRunTime;
    public String MovieRating;
    public String MovieReview;
    public String MovieTrailer;
    public ArrayList<String> List_Of_Trailers;

    public DetailElementsArray(String mvName, String mvPoster, String mvReleaseDate, Integer mvRunTime,
                               String mvRating, String mvReview, String mvTrailer, ArrayList<String> trl) {
        this.MovieName = mvName;
        this.MoviePoster = mvPoster;
        this.MovieReleaseDate = mvReleaseDate;
        this.MovieRunTime = mvRunTime;
        this.MovieRating = mvRating;
        this.MovieReview = mvReview;
        this.MovieTrailer = mvTrailer;
        this.List_Of_Trailers = trl;
    }

}
