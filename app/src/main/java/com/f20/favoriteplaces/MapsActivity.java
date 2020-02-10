package com.f20.favoriteplaces;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public final static int REQUEST_CODE = 1;
    public static final String TAG = "MapsActivity";

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    double latitude;
    double longitude;
    double dest_lat, dest_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initMap();
        getUserLocation();
        if(!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


        Spinner spinner = findViewById(R.id.spinner_map_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case Constants.MAP_TYPE_DEFAULT:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case Constants.MAP_TYPE_SATELITE:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case Constants.MAP_TYPE_HYBRID:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case Constants.MAP_TYPE_TERRAIN:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    default:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();
    }

    private void setHomeMarker() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location: locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    latitude = userLocation.latitude;
                    longitude = userLocation.longitude;
                    String address = getAddress(userLocation);

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(userLocation)
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(userLocation)
                            .title(address)
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.user_position)));
                }
            }
        };
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorDrawableResourceId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private boolean checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Location location = new Location("Your Destination");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                dest_lat = latLng.latitude;
                dest_lng = latLng.longitude;

                setMarker(location);
            }
        });
    }

    public void btnClick(View v) {

    }


    private String getAddress(LatLng location) {
        String address = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Log.i(TAG,"onLocationResult: " + addresses.get(0));
                address = "";
                if(addresses.get(0).getAdminArea() != null)
                    address += addresses.get(0).getAdminArea() + " ";
                if(addresses.get(0).getCountryName() != null)
                    address += addresses.get(0).getCountryName() + " ";
                if(addresses.get(0).getLocality() != null)
                    address += addresses.get(0).getLocality() + " ";
                if(addresses.get(0).getPostalCode() != null)
                    address += addresses.get(0).getPostalCode() + " ";
                if(addresses.get(0).getThoroughfare() != null)
                    address += addresses.get(0).getThoroughfare() + " ";

                Log.i(TAG,"getAddress: " + address);
            }
        } catch (IOException e) {
            e.printStackTrace(); }
        return address;
    }


    private void setMarker(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLatLng)
                .title("Your Destination")
                .snippet(getAddress(userLatLng))
                .draggable(true)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.destination));

        mMap.addMarker(options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
    }
}
