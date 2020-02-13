package com.f20.favoriteplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class GetDirectionData extends AsyncTask<Object, String, String> {

    String directionData;
    GoogleMap mMap;
    MarkerOptions markerOptions;
    String url;

    String distance;
    String duration;
    LatLng latLng;

    String address;
    Polyline polyline;
    Context context;

    int polycolor = Color.RED;

    GetDirectionData(Context context, String address) {
        this.context = context;
        this.address = address;
    }

    GetDirectionData(Context context, String address, int polycolor) {
        this.context = context;
        this.address = address;
        this.polycolor = polycolor;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        FetchUrl fetchUrl = new FetchUrl();
        try {
            directionData = fetchUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directionData;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String > distanceData = null;
        DataParser distanceParser = new DataParser();
        distanceData = distanceParser.parseDistance(s);
        Log.i("GetDirectionData", "onPostExecute: " + "trying to get parsed data");

        distance = distanceData.get("distance");
        duration = distanceData.get("duration");

        Log.i("MapsActivity", "onPostExecute: " + "Done clearing map");
        //Create a new marker with new title and snippet
        markerOptions = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(this.address)
                .snippet("Distance: " + distance + ", Duration: " + duration)
                .icon(bitmapDescriptorFromVector(this.context, R.drawable.destination));


        mMap.addMarker(markerOptions);

        Log.i("MapsActivity", "onPostExecute: " + "Done adding marker");

        String[] directionList;
        DataParser directionParser = new DataParser();
        directionList = directionParser.parseDirections(s);
        displayDirections(directionList);

    }

    private void displayDirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i =0; i<count; ++i) {
            PolylineOptions options = new PolylineOptions()
                    .color(polycolor)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            this.polyline = mMap.addPolyline(options);

        }
        Log.i("MapsActivity", "displayDirections: " + "Done adding polyline");
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorDrawableResourceId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String getAddress(LatLng location) {
        String address = "";
        Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                address = "";
                if(addresses.get(0).getThoroughfare() != null)
                    address += addresses.get(0).getThoroughfare() + " ";
                if(addresses.get(0).getLocality() != null)
                    address += addresses.get(0).getLocality() + " ";
                if(addresses.get(0).getAdminArea() != null)
                    address += addresses.get(0).getAdminArea() + " ";
                if(addresses.get(0).getCountryName() != null)
                    address += addresses.get(0).getCountryName() + " ";
                if(addresses.get(0).getPostalCode() != null)
                    address += addresses.get(0).getPostalCode() + " ";
            }
        } catch (IOException e) {
            e.printStackTrace(); }
        return address;
    }

}
