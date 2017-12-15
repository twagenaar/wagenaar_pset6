/*
 * MainActivity
 * Tessa Wagenaar
 * This activity contains a search field with search button.
 * Enter a title in the search bar to search for a movie.
 * When the movie is clicked you will be redirected to an
 * activity which will show you more information about the movie.
 */

package a11021047.finalproject;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser user;
    String url = "http://www.omdbapi.com/?apikey=afd37ac8&s=";
    ListView listView;
    Button button;
    EditText editText;
    private Menu menu;

    /*
     * Create the options menu at the top of the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        inflater.inflate(R.menu.actions, menu);
        this.menu = menu;
        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * updateMenu
     * Show the correct menu buttons
     * Logged in:
     *   Log out
     *   Watchlist
     * Logged out:
     *   Log in
     */
    public void updateMenu() {
        if (user != null) {
            menu.findItem(R.id.watchlist_button).setVisible(true);
            menu.findItem(R.id.login_button).setTitle(R.string.signout);
        }
        else {
            menu.findItem(R.id.watchlist_button).setVisible(false);
            menu.findItem(R.id.login_button).setTitle(R.string.login);
        }
    }

    /*
     * onOptionItemSelected
     * Show the correct fragment according to which button was clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        updateMenu();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.login_button:
                LoginFragment fragment = new LoginFragment();
                fragment.show(ft, "dialog");
                user = mAuth.getCurrentUser();
                break;
            case R.id.watchlist_button:
                WatchlistFragment fragment2 = new WatchlistFragment();
                fragment2.show(ft, "dialog");
                user = mAuth.getCurrentUser();
                break;
        }
        return true;
    }

    /*
     * OnCreate
     * Look up the layout fields needed for the rest of the app.
     * Also set a key listener on the editText to make it respond
     * when the enter button is hit.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.searchList);
        button = findViewById(R.id.searchButton);
        editText = findViewById(R.id.searchText);

        editText.setOnKeyListener(new enterListener());
    }

    /*
     * enterListener
     * Listen for when the enterbutton is hit and start the
     * search for movies corresponding to the search tag when
     * it is hit.
     */
    private class enterListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                    (i == KeyEvent.KEYCODE_ENTER)) {
                search_results(editText.getText().toString());
                return true;
            }
            return false;
        }
    }

    /*
     * parseResponse
     * Read the JSONobject received from the query to the API
     * Add all the movies which correspond the search tag to
     * the listview.
     */
    private void parseResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray("Search");
            ArrayList<String> titles = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                titles.add(item.getString("Title"));
            }
            ArrayAdapter<String> list = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    titles);
            listView.setOnItemClickListener(new searchClickListener());
            listView.setAdapter(list);
        }
        catch(Exception e) {
            Log.d("Error", e.toString());
        }
    }

    /*
     * searchClickListener
     * When a movie title in the listView is clicked, redirect the user to
     * the showActivity which shows the user more information about the movie
     */
    private class searchClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(MainActivity.this, ShowActivity.class);
            intent.putExtra("movie", listView.getItemAtPosition(i).toString());
            startActivity(intent);
        }
    }

    /*
     * When the search button is hit, start the search for
     * movies corresponding to the search tag.
     */
    public void button_search(View view) {
        search_results(editText.getText().toString());
    }

    public void search_results(String keyword) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + keyword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Request for data failed.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }
}