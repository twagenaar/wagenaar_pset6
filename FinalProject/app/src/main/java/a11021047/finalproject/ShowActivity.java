/*
 * ShowActivity
 * Tessa Wagenaar
 * This activity displays the information of a movie.
 * It uses the DownloadImageTask class to load the poster image from
 * the URL.
 */

package a11021047.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class ShowActivity extends AppCompatActivity {

    String url = "http://www.omdbapi.com/?apikey=afd37ac8&t=";
    String movieTitle;
    TextView title;
    TextView releasedate;
    TextView plot;
    MyMovie movie;
    private FirebaseAuth authDB;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase database;
    private FirebaseUser user;

    /*
     * onCreate
     * Get the title of the selected movie and find the fields which
     * should be set with the information about the movie.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent intent = getIntent();
        movieTitle = intent.getStringExtra("movie");
        
        title = findViewById(R.id.movieTitle);
        releasedate = findViewById(R.id.movieReleased);
        plot = findViewById(R.id.moviePlot);
        showInfo(movieTitle);
        database = FirebaseDatabase.getInstance();
        authDB = FirebaseAuth.getInstance();
        user = authDB.getCurrentUser();
    }

    /*
     * showInfo
     * Do a request to the API to collect the extra information about
     * the movie and display it.
     */
    private void showInfo(String movie) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + movie,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    /*
     * parseResponse
     * Process the JSON answer of the query to the API. Display the received
     * data in the correct fields in the activity and create a MyMovie object
     * with the collected data.
     */
    private void parseResponse(String response) {
        Log.d("Response", response);
        try{
            JSONObject object = new JSONObject(response);
            String strTitle = object.getString("Title");
            String text = strTitle + " (" + object.getString("Year") + ")";
            title.setText(text);
            String strPlot = object.getString("Plot");
            plot.setText(strPlot);
            String strReleased = object.getString("Released");
            text = "Released: " + strReleased;
            releasedate.setText(text);
            String strPoster = object.getString("Poster");
            new DownloadImageTask((ImageView) findViewById(R.id.moviePoster))
                    .execute(strPoster);
            String id = strTitle + user.getUid();
            movie = new MyMovie(id, strTitle, strPlot, strReleased, strPoster);
        }
        catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    /*
     * addToWatchlist
     * If the user is logged in, add the movie to the firebase database.
     * Else tell the user that they need to log in before they can add
     * movies to their watchlist.
     */
    public void addToWatchlist(View view) {
        if (user == null) {
            Toast.makeText(ShowActivity.this, "Please log in to save to your watchlist", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(ShowActivity.this, "Movie saved to your watchlist", Toast.LENGTH_LONG).show();
            database.getReference().child("watchlist").child(user.getUid()).child(movie.getId()).setValue(movie);
        }
    }
}
