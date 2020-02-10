package com.f20.favoriteplaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaceData extends AsyncTask<Object, String, String> {
    GoogleMap googleMap;
    String placeData;
    String url;
    Context context;

    public GetNearbyPlaceData(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        Log.i("GetNearbyPlaceData", "doInBackground: " + url);


        FetchUrl fetchUrl = new FetchUrl();
        try {
            placeData = fetchUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placeData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser dataParser = new DataParser();
        nearbyPlaceList = dataParser.parse(s);
        googleMap.clear();
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyList) {
        if(nearbyList.size() <= 0 )
            Toast.makeText(this.context, "No nearby of type inputted", Toast.LENGTH_SHORT).show();
        for(int i = 0; i<nearbyList.size(); ++i) {
            HashMap<String, String> place = nearbyList.get(i);
            String placeName = place.get("name");
            String vicinity = place.get("vicinity");
            double lat = Double.parseDouble(place.get("latitude"));
            double lng = Double.parseDouble(place.get("longitude"));
            String reference = place.get("reference");

            LatLng latLng = new LatLng(lat, lng);

            //Marker options
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(placeName + " : " + vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            googleMap.addMarker(markerOptions);


        }
    }
}
