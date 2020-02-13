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
import android.graphics.Canvas;
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

    private RecyclerView recyclerView;
    private PlaceAdapterRecycler mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeController swipeController;

    private boolean updatingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabase = new DatabaseHelper(this);
        placeList = new ArrayList<>();
        updatingList = false;

        setupRecyclerView();
        loadPlaces();

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

            mAdapter = new PlaceAdapterRecycler(this, placeList);
            recyclerView.setAdapter(mAdapter);

        }
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.lv_places);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

         swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                deletePlace(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }

             @Override
             public void onLeftClicked(int position) {
                 editPlace(position);
             }
         });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    private void deletePlace(final int position) {
        final Place place = mAdapter.getData().get(position);
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
//                mAdapter.removeItem(position);
//                mAdapter.restoreItem(place, position);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void editPlace(final int position) {
        final Place place = mAdapter.getData().get(position);
        Toast.makeText(MainActivity.this, String.format("Address: %s", place.getAddr()), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("modPlace", place);
        intent.putExtra("position", position);
        startActivity(intent);
    }

}

