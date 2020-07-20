package com.example.movielist.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.movielist.R;
import com.example.movielist.adapters.MovieAdapterRecycler;
import com.example.movielist.Data.models.Film;
import com.example.movielist.Data.models.Films;
import com.example.movielist.Data.services.IWebServer;
import com.example.movielist.Data.services.WebService;
import com.example.movielist.database.FavoritiTableHelper;
import com.example.movielist.database.MoviesProvider;
import com.example.movielist.database.MoviesTableHelper;
import com.example.movielist.fragments.ConfirmDialogFragment;
import com.example.movielist.fragments.ConfirmDialogFragmentListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements MovieAdapterRecycler.OnFilmListener, ConfirmDialogFragmentListener {





    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    GridLayoutManager manager;
    int page = 1;

    Toolbar toolbar;

    private ProgressBar loadingBar;

    private RecyclerView recyclerView;
    private MovieAdapterRecycler adapterRecycler;

    private WebService webService;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm) {
            // Se il fetch dei dati va a buon fine
            if (success) {
                Log.d("PROVA", "onFilmsFetched: " + films.getPage());
                // Eseguo query, che ritorna l'intera tabella del db
                Cursor cursor = getContentResolver().query(MoviesProvider.FILMS_URI, null, null, null,null);
                // Se ritorna 0, allora è vuota, quindi deve popolare il db
                if (cursor.getCount() == 0) {
                    Log.d("PROVA", "SAVE DB");
                    saveDataOnDB(responseFilm);
                    fetchDatiDatabase();
                } else {
                    Log.d("PROVA", "READ DB");

                    // TODO: da modificare per visualizzare correttamente
                    saveDataOnDB(responseFilm);

                    fetchDatiDatabase();
                }
                // Toast.makeText(MainActivity.this,"E' andato tutto bene", Toast.LENGTH_SHORT).show();
            } else {
                fetchDatiDatabase();
                Toast.makeText(MainActivity.this,"OFFLINE : " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lista Film");
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setTitleTextColor(getResources().getColor(R.color.coloreTesto));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.button_favoriti: {
                        startActivity(new Intent(MainActivity.this, ActivityFavoriti.class));
                    }
                }
                return false;
            }
        });

        webService = WebService.getInstance();

        loadingBar = findViewById(R.id.barra_caricamento);
        recyclerView = findViewById(R.id.recycler_film);

        adapterRecycler = new MovieAdapterRecycler(this,this);

        // Controllo l'orientamento dello schermo
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            // Se è portrait, allora uso 2 colonne
            manager = new GridLayoutManager(this,2);
        }
        else{
            // Se è landscape, allore uso 3 colonne
            manager = new GridLayoutManager(this,3);
        }

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterRecycler);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems ==  totalItems)) {
                    isScrolling = false;
                    loadFilmsScrool();
                }
            }
        });

        loadFilms();
    }


    // Carico i film
    private void loadFilms() {
        loadingBar.setVisibility(View.VISIBLE);
        webService.getFilms(webServerListener,page);
        page++;
    }

    // Carico i film
    private void loadFilmsScrool() {
        loadingBar.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this,"Caricamento film...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webService.getFilms(webServerListener,page);
                adapterRecycler.notifyDataSetChanged();
                page++;
            }
        },2000);

    }

    // Gestione del click sulla locandina del film nella lista
    @Override
    public void onFilmCLick(int position) {
        Intent intent = new Intent(this,ActivityDettaglio.class);
        intent.putExtra("ID", position+1);
        startActivity(intent);
    }

    // Gestione del longClick sulla Locandina del film nella lista
    @Override
    public void onLongFilmClick(final int position) {
        int id = position + 1;
        Cursor mCursor = getContentResolver().query(MoviesProvider.FILMS_URI, null, MoviesTableHelper._ID + " =" + id, null,null);
        mCursor.moveToFirst();
        String titoloFilm = mCursor.getString(mCursor.getColumnIndex(MoviesTableHelper.TITLE));

        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment("Aggiungi ai Preferiti",
                "Vuoi aggiungere il film " +  titoloFilm + " ai preferiti ?",
                position);
        dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
    }

    // Salvo i film nel database
    private void saveDataOnDB(List<Film> responseFilm) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < responseFilm.size(); i++) {
            values.put(MoviesTableHelper.TITLE, responseFilm.get(i).getTitle());
            values.put(MoviesTableHelper.RELEASE_DATE, responseFilm.get(i).getReleaseDate());
            values.put(MoviesTableHelper.DESCRIPTION, responseFilm.get(i).getOverview());
            values.put(MoviesTableHelper.POSTER_PATH, responseFilm.get(i).getPosterPath());
            values.put(MoviesTableHelper.BACKDROP_PATH, responseFilm.get(i).getBackdropPath());
            getContentResolver().insert(MoviesProvider.FILMS_URI, values);
        }
    }

    // Fetch dei dati nel database
    private void fetchDatiDatabase() {
        // Se il fetch dei dati mi ritorna un errore allora esegua query sul db per recuperare tutti i flim dal db interno
        Cursor mCursor = getContentResolver().query(MoviesProvider.FILMS_URI, null, null, null,null);
        List<Film> films1 = new ArrayList<>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            films1.add(new Film(mCursor.getString(mCursor.getColumnIndex(MoviesTableHelper.POSTER_PATH))));
        }
        // Setto la recyclerview con i film recuperati dal database
        adapterRecycler.setFilms(films1);
        adapterRecycler.notifyDataSetChanged();
        loadingBar.setVisibility(View.GONE);
    }

    // Click su conferma di aggiungere il film ai preferiti
    @Override
    public void onPositivePressed(long id) {
        Toast.makeText(this,"Film aggiunto",Toast.LENGTH_LONG).show();
        insertUser(id);
    }

    // Click su annula aggiunta del fiml ai preferiti
    @Override
    public void onNegativePressed() {
        Toast.makeText(this,"Operazione annullata",Toast.LENGTH_LONG).show();
    }


    private void insertUser(long id) {
        long idFilm = id + 1;
        // Eseguo una ricerca tramite l'id all'interno del db per recuperare le informazioni del film
        Cursor cursor = getContentResolver().query(MoviesProvider.FILMS_URI, null, MoviesTableHelper._ID + " = " + idFilm, null,null);
        cursor.moveToNext();

        ContentValues values = new ContentValues();
        values.put(FavoritiTableHelper.TITLE, cursor.getString(cursor.getColumnIndex(MoviesTableHelper.TITLE)));
        values.put(FavoritiTableHelper.RELEASE_DATE, cursor.getString(cursor.getColumnIndex(MoviesTableHelper.RELEASE_DATE)));
        values.put(FavoritiTableHelper.DESCRIPTION, cursor.getString(cursor.getColumnIndex(MoviesTableHelper.DESCRIPTION)));
        values.put(FavoritiTableHelper.POSTER_PATH, cursor.getString(cursor.getColumnIndex(MoviesTableHelper.POSTER_PATH)));
        values.put(FavoritiTableHelper.BACKDROP_PATH, cursor.getString(cursor.getColumnIndex(MoviesTableHelper.BACKDROP_PATH)));
        getContentResolver().insert(MoviesProvider.FAVORITES_URI, values);
    }


}
