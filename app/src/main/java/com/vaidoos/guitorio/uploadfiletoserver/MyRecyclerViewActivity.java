package com.vaidoos.guitorio.uploadfiletoserver;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Guitorio on 5/7/2018.
 */

public class MyRecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recycler_view);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // actually called indiv items at database ref...
                String fileName = dataSnapshot.getKey(); // return the file name
                String url = dataSnapshot.getValue().toString(); // return url for file

                ((MyAdapter)recyclerView.getAdapter()).update(fileName,url);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


         recyclerView = findViewById(R.id.recyclerView);

        //custom adapter always
        // populate the recycler view with items

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyAdapter myAdapter = new MyAdapter(recyclerView,getApplicationContext(),new ArrayList<String>(),new ArrayList<String>());
        recyclerView.setAdapter(myAdapter);

    }
}
