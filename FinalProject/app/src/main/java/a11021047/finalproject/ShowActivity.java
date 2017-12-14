package a11021047.finalproject;

import android.content.Intent;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
//        mDatabase = database.getReference("message");

//        myRef.setValue("Hello, World!");
    }

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

    private void parseResponse(String response) {
//        {"Title":"Pitch Perfect","Year":"2012","Rated":"PG-13","Released":"05 Oct 2012","Runtime":"112 min","Genre":"Comedy, Music, Romance","Director":"Jason Moore","Writer":"Kay Cannon (screenplay), Mickey Rapkin (based on the book by)","Actors":"Anna Kendrick, Skylar Astin, Ben Platt, Brittany Snow","Plot":"Beca, a freshman at Barden University, is cajoled into joining The Bellas, her school's all-girls singing group. Injecting some much needed energy into their repertoire, The Bellas take on their male rivals in a campus competition.","Language":"English","Country":"USA","Awards":"7 wins & 20 nominations.","Poster":"https://images-na.ssl-images-amazon.com/images/M/MV5BMTcyMTMzNzE5N15BMl5BanBnXkFtZTcwNzg5NjM5Nw@@._V1_SX300.jpg","Ratings":[{"Source":"Internet MyMovie Database","Value":"7.2/10"},{"Source":"Rotten Tomatoes","Value":"80%"},{"Source":"Metacritic","Value":"66/100"}],"Metascore":"66","imdbRating":"7.2","imdbVotes":"234,041","imdbID":"tt1981677","Type":"movie","DVD":"18 Dec 2012","BoxOffice":"$61,100,000","Production":"Universal Studios","Website":"http://www.pitchperfectmovie.com","Response":"True"}
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

    public void addToWatchlist(View view) {
//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user == null) {
//                    Toast.makeText(ShowActivity.this, "Please log in to add this movie to your personal watchlist.", Toast.LENGTH_LONG).show();
//                }
//                else {
//                    Log.d("Add to firebase", movie.getTitle());
//                    DatabaseReference myRef = database.getReference("watchlist");
//                    myRef.child(movie.getTitle()).setValue(movie);
//                }
//            }
//        };

        if (user == null) {
            Toast.makeText(ShowActivity.this, "Please log in to save to your watchlist", Toast.LENGTH_LONG).show();
        }
        else {
            Log.d("Add", "addToWatchlist: firebase");
            database.getReference().child("watchlist").child(user.getUid()).child(movie.getId()).setValue(movie);
        }

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue(movie);
    }
}
