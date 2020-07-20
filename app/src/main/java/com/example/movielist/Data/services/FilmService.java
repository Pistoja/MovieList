package com.example.movielist.Data.services;

import com.example.movielist.Data.models.Film;
import com.example.movielist.Data.models.Films;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FilmService {

    @GET("movie/popular")
    Call<Films> getFilms(@Query("api_key") String apiKey, @Query("language") String lang, @Query("page") String page);
}
