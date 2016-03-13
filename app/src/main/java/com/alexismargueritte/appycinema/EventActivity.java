package com.alexismargueritte.appycinema;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.alexismargueritte.appycinema.adapter.CustomListAdapter;
import com.alexismargueritte.appycinema.app.AppController;
import com.alexismargueritte.appycinema.model.Movie;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = EventActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<>();
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Download from filmSeance
        String eventURL = "http://centrale.corellis.eu/events.json";
        JsonArrayRequest filmEvents = new JsonArrayRequest(eventURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                JSONObject events = obj.getJSONObject("events");
                                JSONObject films = obj.getJSONObject("films");
                                Movie movie = new Movie();
                                movie.setTitle(events.getString("titre"));
                                movie.setThumbnailUrl(events.getString("affiche"));
                                movie.setDirector(films.getString("realisateur"));
                                movie.setYear(events.getString("date_deb"));
                                movie.setGenre(obj.getString("type"));

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
        AppController.getInstance().addToRequestQueue(filmEvents);
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
        //ajoute les entrées de menu_test à l'ActionBar
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
                //Intent prochainementActivity = new Intent(this,ProchainementActivity.class);
                //startActivity(prochainementActivity);
                Toast.makeText(this.getApplicationContext(), "En cours de développement.",
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
}