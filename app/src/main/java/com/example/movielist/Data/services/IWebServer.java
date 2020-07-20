package com.example.movielist.Data.services;

import com.example.movielist.Data.models.Film;
import com.example.movielist.Data.models.Films;

import java.util.List;

public interface IWebServer {

    void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm);
}
