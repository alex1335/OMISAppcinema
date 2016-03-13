package com.alexismargueritte.appycinema;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.alexismargueritte.appycinema.adapter.CustomListAdapter;
import com.alexismargueritte.appycinema.app.AppController;
import com.alexismargueritte.appycinema.fragments.DetailsFragment;
import com.alexismargueritte.appycinema.fragments.ListFragment;
import com.alexismargueritte.appycinema.model.Movie;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListFragment.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<>();
    private CustomListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("À l'affiche");
        final ViewFlipper flippy = (ViewFlipper) findViewById(R.id.view_flipper);

        // Liste des films à l'affiche
        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the right movie in the listView
                Movie m = movieList.get(position);

                TextView detailText = (TextView) findViewById(R.id.detailText);
                TextView titreText = (TextView) findViewById(R.id.textViewTitre);
                TextView dateText = (TextView) findViewById(R.id.textViewDate);
                TextView genreText = (TextView) findViewById(R.id.textViewGenre);

                detailText.setText(m.getSynopsis());
                titreText.setText(m.getTitle());
                dateText.setText(m.getYear());
                genreText.setText(m.getGenre());

                // get & set poster of the movie
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                NetworkImageView thumbNailDetails = (NetworkImageView) findViewById(R.id.thumbnailDetails);
                thumbNailDetails.setImageUrl(m.getThumbnailUrl(), imageLoader);

                // get & set images of the movie for Details fragment
                JSONArray medias = m.getListMedias();
                ArrayList listpath = new ArrayList();// list of String to store url path_j
                for (int j=0; j < medias.length(); j++){
                    try {
                        JSONObject path = medias.getJSONObject(j);// number j+1 of JSONArray medias
                        String path_j = path.getString("path");// get the url
                        listpath.add(path_j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Gallery loading
                NetworkImageView thumbNailMedias0 = (NetworkImageView) findViewById(R.id.thumbnailMedias0);
                thumbNailMedias0.setImageUrl(m.getPath(listpath, 0), imageLoader);
                NetworkImageView thumbNailMedias1 = (NetworkImageView) findViewById(R.id.thumbnailMedias1);
                thumbNailMedias1.setImageUrl(m.getPath(listpath, 1), imageLoader);
                NetworkImageView thumbNailMedias2 = (NetworkImageView) findViewById(R.id.thumbnailMedias2);
                thumbNailMedias2.setImageUrl(m.getPath(listpath, 2), imageLoader);
                NetworkImageView thumbNailMedias3 = (NetworkImageView) findViewById(R.id.thumbnailMedias3);
                thumbNailMedias3.setImageUrl(m.getPath(listpath, 3), imageLoader);
                NetworkImageView thumbNailMedias4 = (NetworkImageView) findViewById(R.id.thumbnailMedias4);
                thumbNailMedias4.setImageUrl(m.getPath(listpath, 4), imageLoader);
                NetworkImageView thumbNailMedias5 = (NetworkImageView) findViewById(R.id.thumbnailMedias5);
                thumbNailMedias5.setImageUrl(m.getPath(listpath, 5), imageLoader);
                NetworkImageView thumbNailMedias6 = (NetworkImageView) findViewById(R.id.thumbnailMedias6);
                thumbNailMedias6.setImageUrl(m.getPath(listpath, 6), imageLoader);

                // swipe to Details with animations
                flippy.setInAnimation(MainActivity.this.getApplicationContext(), R.anim.in_from_right);
                flippy.setOutAnimation(MainActivity.this.getApplicationContext(), R.anim.out_to_left);
                flippy.showNext();
                setTitle("Détails");
        }
        });

        Button back = (Button) findViewById(R.id.buttonPrevious);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flippy.setInAnimation(MainActivity.this.getApplicationContext(), R.anim.in_from_left);
                flippy.setOutAnimation(MainActivity.this.getApplicationContext(), R.anim.out_to_right);
                flippy.showPrevious();
                setTitle("À l'affiche");
            }
        });

        // hover effect on buttonback
        back.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(Color.parseColor("#3a9ac7"), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        // Loading screen implementation
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Download from filmSeance
        String filmSeanceURL = "http://centrale.corellis.eu/filmseances.json";
        JsonArrayRequest filmSeance = new JsonArrayRequest(filmSeanceURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(obj.getString("titre"));
                                movie.setThumbnailUrl(obj.getString("affiche"));
                                movie.setDirector(obj.getString("realisateur"));
                                movie.setYear(obj.getString("annee"));
                                movie.setGenre(obj.getString("genre"));
                                movie.setSynopsis(obj.getString("synopsis"));
                                movie.setListMedias(obj.getJSONArray("medias"));
                                // adding movie to movies array
                                movieList.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(filmSeance);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Menu between activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_one:
                    Intent mainActivity = new Intent(this,MainActivity.class);
                    startActivity(mainActivity);
                return true;
            case R.id.menu_activity_two:
                    //Intent prochainementActivity = new Intent(this,ProchainementActivity.class);
                    //startActivity(prochainementActivity);
                Toast.makeText(this.getApplicationContext(),"En cours de développement.",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_activity_three:
                    //Intent eventActivity = new Intent(this,EventActivity.class);
                    //startActivity(eventActivity);
                Toast.makeText(this.getApplicationContext(),"En cours de développement",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_settings:
                // Comportement du bouton "Paramètres"
                Toast.makeText(this.getApplicationContext(),"En cours de développement",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRssItemSelected(String link) {
        DetailsFragment fragment = (DetailsFragment) getFragmentManager()
                .findFragmentById(R.id.detailFragment);
        if (fragment != null && fragment.isInLayout()) {
            fragment.setText(link);
        }
    }

    @Override
    public void onClick(View v) {
    }
}