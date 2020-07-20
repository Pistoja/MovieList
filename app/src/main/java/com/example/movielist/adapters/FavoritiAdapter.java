package com.example.movielist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movielist.R;
import com.example.movielist.Data.models.Film;

import java.util.ArrayList;
import java.util.List;

public class FavoritiAdapter extends RecyclerView.Adapter<FavoritiAdapter.MyViewHolder> {

    private Context context;
    private List<Film> films = new ArrayList<>();
    private OnFilmListener mOnFilmListener;

    public FavoritiAdapter(Context context, OnFilmListener onFilmListener) {
        this.context = context;
        this.mOnFilmListener = onFilmListener;
    }

    public void setFilms(List<Film> films)  {
        this.films = films;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.immagine_film,parent,false);

        return new MyViewHolder(view, mOnFilmListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Film currentFilm = films.get(position);

        // Glide for image
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/"+currentFilm.getPosterPath())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    // Classe ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView image;
        OnFilmListener onFilmListener;

        public MyViewHolder(@NonNull View itemView, OnFilmListener onFilmListener) {
            super(itemView);
            this.onFilmListener = onFilmListener;
            image = itemView.findViewById(R.id.immagineFilm);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }



        @Override
        public void onClick(View v) {
            onFilmListener.onFilmCLick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onFilmListener.onLongFilmClick(getAdapterPosition());
            return true;
        }
    }


    // Interface per il clickListener
    public interface OnFilmListener {
        void onFilmCLick(int position);
        void onLongFilmClick(int position);
    }

}
