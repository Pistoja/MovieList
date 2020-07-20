package com.example.movielist.Data.services;

import android.util.Log;

import com.example.movielist.Data.models.Films;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

    private String FILM_BASE_URL = "https://api.themoviedb.org/3/";
    private String API_KEY = "b914e4a5b343ce75e93469d427fc65b8";
    private String LANGUAGE = "it-IT";
    private static WebService instance;
    private FilmService filmService;

    private WebService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FILM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        filmService = retrofit.create(FilmService.class);
    }

    public static WebService getInstance() {
        if (instance == null)
            instance = new WebService();
        return instance;
    }

    public void getFilms(final IWebServer callBack, int page) {
        Call<Films> filmRequest = filmService.getFilms(API_KEY,LANGUAGE,String.valueOf(page));
        filmRequest.enqueue(new Callback<Films>() {
            @Override
            public void onResponse(Call<Films> call, Response<Films> response) {
                if (response.code() == 200) {
                    callBack.onFilmsFetched(true,response.body(),-1,null, response.body().getResults());
                } else {
                    try {
                        callBack.onFilmsFetched(true,null,response.code(),response.errorBody().string(), response.body().getResults());
                    } catch (IOException ex) {
                        Log.e("WebService", ex.toString() );
                        callBack.onFilmsFetched(true, null, response.code(),"Generic error message", response.body().getResults());
                    }
                }
            }

            @Override
            public void onFailure(Call<Films> call, Throwable t) {
                callBack.onFilmsFetched(false, null, -1, t.getLocalizedMessage(), null);
            }
        });
    }

}
