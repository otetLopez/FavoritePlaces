package com.f20.favoriteplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper mDatabase;
    List<Place> placeList;
    ListView listView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //listView = findViewById(R.id.lv_places);
        recyclerView = (RecyclerView) findViewById(R.id.lv_places);
        mDatabase = new DatabaseHelper(this);
        placeList = new ArrayList<>();


        loadPlaces();

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)





        findViewById(R.id.btn_add_place).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        loadPlaces();
    }

    private void loadPlaces() {
        placeList.clear();
        Cursor cursor = mDatabase.getAllPlaces();
        if (cursor.moveToFirst()) {
            do {
                placeList.add(new Place(
                        cursor.getInt(0),       //id
                        cursor.getString(1),    //addr
                        cursor.getString(2),    //date
                        cursor.getInt(3) > 0,   //visited
                        cursor.getDouble(4),    //lat
                        cursor.getDouble(5)    //lng
                ));
            } while (cursor.moveToNext());
            cursor.close();

            // show items in a listView
            // we use a custom adapter to show employees
            //PlaceAdapter placeAdapter = new PlaceAdapter(this, R.id.lv_places, placeList, mDatabase);
            //listView.setAdapter(placeAdapter);

            mAdapter = new PlaceAdapterRecycler(this, placeList);
            recyclerView.setAdapter(mAdapter);

        }
    }
}
