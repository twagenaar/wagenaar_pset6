package a11021047.finalproject;

import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class WatchlistFragment extends DialogFragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ListView listView;
    ArrayAdapter<String> list;
    ArrayList<String> movies;

    public WatchlistFragment() {
        // Required empty public constructor
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);

        listView = view.findViewById(R.id.watchlist);
        listView.setOnItemLongClickListener(new WatchListener());

        return view;
    }

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