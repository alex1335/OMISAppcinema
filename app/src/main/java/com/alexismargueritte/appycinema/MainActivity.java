package com.alexismargueritte.appycinema;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ViewFlipper;

import com.alexismargueritte.appycinema.adapter.CustomListAdapter;
import com.alexismargueritte.appycinema.app.AppController;
import com.alexismargueritte.appycinema.model.Movie;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.NetworkImageView;

public class MainActivity extends AppCompatActivity implements ListFragment.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private String eventURL = "http://centrale.corellis.eu/events.json";
    private String filmSeanceURL = "http://centrale.corellis.eu/filmseances.json";
    private String prochainnementURL = "http://centrale.corellis.eu/prochainement.json";
    private String seancesURL = "http://centrale.corellis.eu/seances.json";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<>();
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewFlipper flippy = (ViewFlipper) findViewById(R.id.view_flipper);

        Button back = (Button) findViewById(R.id.buttonPrevious);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flippy.showPrevious();
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = movieList.get(position);
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();
                TextView detailText = (TextView) findViewById(R.id.detailText);
                TextView titreText = (TextView) findViewById(R.id.textViewTitre);
                TextView dateText = (TextView) findViewById(R.id.textViewDate);
                detailText.setText(m.getSynopsis());
                titreText.setText(m.getTitle());
                dateText.setText(m.getYear());
                NetworkImageView thumbNailDetails = (NetworkImageView) findViewById(R.id.thumbnailDetails);
                thumbNailDetails.setImageUrl(m.getThumbnailUrl(), imageLoader);

                JSONArray medias = m.getListMedias();
                ArrayList listpath = new ArrayList();
                for (int j=0; j < medias.length(); j++){
                    try {
                        JSONObject path = medias.getJSONObject(j);
                        String path_j = path.getString("path");
                        listpath.add(path_j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                NetworkImageView thumbNailMedias0 = (NetworkImageView) findViewById(R.id.thumbnailMedias0);
                thumbNailMedias0.setImageUrl(m.getPath(listpath, 0), imageLoader);
                NetworkImageView thumbNailMedias1 = (NetworkImageView) findViewById(R.id.thumbnailMedias1);
                thumbNailMedias1.setImageUrl(m.getPath(listpath, 1), imageLoader);
                NetworkImageView thumbNailMedias2 = (NetworkImageView) findViewById(R.id.thumbnailMedias2);
                thumbNailMedias2.setImageUrl(m.getPath(listpath, 2), imageLoader);
                NetworkImageView thumbNailMedias3 = (NetworkImageView) findViewById(R.id.thumbnailMedias3);
                thumbNailMedias3.setImageUrl(m.getPath(listpath, 3), imageLoader);

                flippy.showNext();
        }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Download from filmSeance
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_one:
                    Intent mainActivity = new Intent(this,MainActivity.class);
                    startActivity(mainActivity);
                return true;
            case R.id.menu_activity_two:
                    Intent prochainementActivity = new Intent(this,ProchainementActivity.class);
                    startActivity(prochainementActivity);
                return true;
            case R.id.menu_activity_three:
                    Intent eventActivity = new Intent(this,EventActivity.class);
                    startActivity(eventActivity);
                return true;
            case R.id.menu_settings:
                // Comportement du bouton "Paramètres"
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