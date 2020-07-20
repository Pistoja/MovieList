package com.example.movielist.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movielist.R;
import com.example.movielist.database.MoviesProvider;
import com.example.movielist.database.MoviesTableHelper;

public class ActivityDettaglio extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dettaglio");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.coloreTesto));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Recupero dall'intent ricevuto dalla mainactivity
        Intent intent = getIntent();
        int id = intent.getExtras().getInt("ID");

        // Eseguo una ricerca tramite l'id all'interno del db per recuperare le informazioni del film
        Cursor cursor = getContentResolver().query(MoviesProvider.FILMS_URI, null, MoviesTableHelper._ID + " = " + id, null,null);
        cursor.moveToNext();

        TextView textViewTitle = findViewById(R.id.titoloDettagli);
        textViewTitle.setText(cursor.getString(cursor.getColumnIndex(MoviesTableHelper.TITLE)));

        TextView releaseDate = findViewById(R.id.datarilascio);
        releaseDate.setText(cursor.getString(cursor.getColumnIndex(MoviesTableHelper.RELEASE_DATE)));

        TextView description = findViewById(R.id.descrizione);
        description.setText(cursor.getString(cursor.getColumnIndex(MoviesTableHelper.DESCRIPTION)));

        ImageView imageView = findViewById(R.id.immagineDettagli);
        // Glide for image
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/"+cursor.getString(cursor.getColumnIndex(MoviesTableHelper.BACKDROP_PATH)))
                .placeholder(new ColorDrawable(Color.BLUE))
                .into(imageView);
    }
}
