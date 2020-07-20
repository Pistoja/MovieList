package com.example.movielist.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.movielist.R;
import com.example.movielist.adapters.FavoritiAdapter;
import com.example.movielist.Data.models.Film;
import com.example.movielist.database.FavoritiTableHelper;
import com.example.movielist.database.MoviesProvider;
import com.example.movielist.database.MoviesTableHelper;
import com.example.movielist.fragments.ConfirmDialogFragment;
import com.example.movielist.fragments.ConfirmDialogFragmentListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityFavoriti extends AppCompatActivity implements ConfirmDialogFragmentListener, FavoritiAdapter.OnFilmListener{


    Toolbar toolbar;
    private static final int MY_ID = 3;

    ListView listView;
    FavoritiAdapter favoriteAdapter;

    GridLayoutManager manager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferiti);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favoriti");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.coloreTesto));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        recyclerView = findViewById(R.id.recycler_favoriti);
        favoriteAdapter = new FavoritiAdapter(this,this);

        // Controllo l'orientamento dello schermo
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            // Se è portrait, allora uso 2 colonne
            manager = new GridLayoutManager(this,2);
        }
        else{
            // Se è landscape, allore uso 3 colonne
            manager = new GridLayoutManager(this,3);
        }

        //manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(favoriteAdapter);

        fetchDatiDatabase();

    }
    
    @Override
    public void onFilmCLick(int position) {

    }

    @Override
    public void onLongFilmClick(int position) {
        int idFilm = position + 1;
        Cursor mCursor = getContentResolver().query(MoviesProvider.FAVORITES_URI, null, FavoritiTableHelper._ID + " =" + idFilm, null,null);
        mCursor.moveToFirst();
        String titoloFilm = mCursor.getString(mCursor.getColumnIndex(MoviesTableHelper.TITLE));

        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment("Rimuovi dai Preferiti",
                "Vuoi rimuovere il film " + titoloFilm + " dai preferiti ?",
                position);
        dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
    }

    @Override
    public void onPositivePressed(long id) {
        deleteFilm(id);
    }

    @Override
    public void onNegativePressed() {
        Toast.makeText(this,"Operazione annullata",Toast.LENGTH_LONG).show();
    }

    private void deleteFilm(long filmId) {
        Log.d("PROVA", "ID: " + filmId);
        if (filmId > 0) {
            String whereClause = FavoritiTableHelper._ID + "=?";
            String[] whereArgs = new String[] { String.valueOf(filmId) };
            int deletedRows = getContentResolver().delete(MoviesProvider.FAVORITES_URI, whereClause, whereArgs);
            Log.d("PROVA", "deleteFilm: " + deletedRows);
            if (deletedRows > 0) {
                Toast.makeText(ActivityFavoriti.this, "Eliminato con successo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ActivityFavoriti.this, "Errore durante la cancellazione", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ActivityFavoriti.this, "Errore", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch dei dati nel database
    private void fetchDatiDatabase() {
        // Se il fetch dei dati mi ritorna un errore allora esegua query sul db per recuperare tutti i flim dal db interno
        Cursor mCursor = getContentResolver().query(MoviesProvider.FAVORITES_URI, null, null, null,null);
        List<Film> films1 = new ArrayList<>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            films1.add(new Film(mCursor.getString(mCursor.getColumnIndex(MoviesTableHelper.POSTER_PATH))));
        }
        // Setto la recyclerview con i film recuperati dal database
        favoriteAdapter.setFilms(films1);
        favoriteAdapter.notifyDataSetChanged();
        //loadingBar.setVisibility(View.GONE);
    }





}
