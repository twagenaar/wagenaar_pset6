/*
 * WatchlistFragment
 * This fragment collects the movies the user saved
 * and displays them in a listview. The movies can be
 * removed from the watchlist by longpressing the title
 * of the movie. The movie will then be removed from the
 * firebase database and the listview will be updated to
 * display the new information.
 */

package a11021047.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class WatchlistFragment extends DialogFragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ListView listView;
    ArrayAdapter<String> list;
    ArrayList<String> movies;

    public WatchlistFragment() {
        // Required empty public constructor
    }

    /*
     * onViewStateRestored
     * Call the original method for this function and set
     * the original listview to an empty arraylist.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        movies = new ArrayList<>();
        list = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                movies);
        listView.setAdapter(list);

        updateUI();
    }

    /*
     * updateUI
     * Fill the arraylist with movie titles collected
     * from the firebase database. If the user is not logged
     * in redirect the user back to the main activity and
     * tell them they are not logged in anymore.
     */
    private void updateUI() {
        if (user == null) {
            this.dismiss();
            Toast.makeText(getActivity(), "You seem to be logged out.", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    movies.clear();
                    if (user != null) {
                        DataSnapshot myMovies = dataSnapshot.child("watchlist").child(user.getUid());
                        for (DataSnapshot myMovie : myMovies.getChildren()) {
                            MyMovie movie = myMovie.getValue(MyMovie.class);
                            String title = movie.getTitle();
                            movies.add(title);
                        }
                        list.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getActivity(), "Failed to read form database.", Toast.LENGTH_SHORT).show();
                    Log.w("read", "Failed to read value.", error.toException());
                }
            });
        }
    }

    /*
     * onCreateView
     * Inflate the view and set a longclicklistener to the listview.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);

        listView = view.findViewById(R.id.watchlist);
        listView.setOnItemLongClickListener(new WatchListener());

        return view;
    }

    /*
     * WatchListener
     * Listen for a longclick on a movie title and remove
     * the selected movie from the database.
     */
    public class WatchListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            String title = listView.getItemAtPosition(i).toString();
            if (user != null) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                String id = title + user.getUid();
                myRef.child("watchlist").child(user.getUid()).child(id).removeValue();
                updateUI();
            }
            return true;
        }
    }
}