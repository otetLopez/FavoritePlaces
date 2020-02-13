package com.f20.favoriteplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private PlaceAdapterRecycler mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean updatingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //listView = findViewById(R.id.lv_places);
        recyclerView = (RecyclerView) findViewById(R.id.lv_places);
        mDatabase = new DatabaseHelper(this);
        placeList = new ArrayList<>();
        updatingList = false;


        loadPlaces();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        enableSwipeToDeleteAndUndo();

        findViewById(R.id.btn_add_place).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                Place place = new Place();
                intent.putExtra("modPlace", place);
                intent.putExtra("position", -1);
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
                        cursor.getDouble(4),    //user lat
                        cursor.getDouble(5),    // user lng
                        cursor.getDouble(6),    //lat
                        cursor.getDouble(7)    //lng
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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Place place = mAdapter.getData().get(position);

                deletePlace(place, position);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);


        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String addr = placeList.get(i).getAddr();
                Toast.makeText(MainActivity.this, addr, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void deletePlace(final Place place, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mDatabase.deletePlace(place.getId())) {
                    mAdapter.removeItem(position);
                }
                loadPlaces();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAdapter.removeItem(position);
                mAdapter.restoreItem(place, position);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}

