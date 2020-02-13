package com.f20.favoriteplaces;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.SearchView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public boolean directionRequested;
    private boolean userLocationChanged;
    private boolean newDirectionSet;

    Place place;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        place = (Place) intent.getExtras().get("modPlace");
        position = (int) intent.getExtras().get("position");

        initMap();
        directionRequested = false;
        userLocationChanged = false;
        newDirectionSet = false;
        getUserLocation();
        if(!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        final SearchView searchView = findViewById(R.id.sv_nearby_key);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i(TAG, "onCreate: " + "This is the nearby type inputted: " + s);
                showNearby(s.replace(" ", "_"));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Log.i(TAG, "onCreate: onQueryTextChange, " + s);
                return false;
            }
        });


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

    private void configureView() {
        if(this.position >= 0) {
            Log.i(TAG, "configureView: " +place.getId()+ ": "+ place.getAddr() + "in " + String.valueOf(place.getLat()) + "," + String.valueOf(place.getLng()));
            //Set the visited
            if (place.isVisited()) {
                CheckBox cb = findViewById(R.id.cb_visited);
                cb.setChecked(true);
            }

            //Set previous user location
            if (latitude != place.getUser_lat() && longitude != place.getUser_lng()) {
                userLocationChanged = true;
                LatLng oldUserLocation = new LatLng(place.getUser_lat(), place.getUser_lng());
                setPreviousHomeMarker(oldUserLocation);
            }

            // Set Marker for existing destination
            // Set the route and get direction
            dest_lat = place.getLat();
            dest_lng = place.getLng();
            directionRequested = true;

            Location location = new Location("Saved Destination");
            location.setLatitude(dest_lat);
            location.setLongitude(dest_lng);
            setMarker(location);
        }
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

    public  void reSetHomeMarker() {
        LatLng userLocation = new LatLng(latitude, longitude);
        setHomeMarker(userLocation);
    }

    private void setPreviousHomeMarker(LatLng userLocation) {
        Log.i(TAG, "setPreviousHomeMarker: previous user location");
        String address = getAddress(userLocation);
        mMap.addMarker(new MarkerOptions().position(userLocation)
                .title(address)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.user_prev)));

    }

    private void setHomeMarker(LatLng userLocation) {
        String address = getAddress(userLocation);
        mMap.addMarker(new MarkerOptions().position(userLocation)
                .title(address)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.user_position)));

    }

    private void setHomeMarker() {
        Log.i(TAG, "setHomeMarker: current user location");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location: locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());


                    latitude = userLocation.latitude;
                    longitude = userLocation.longitude;
                    Log.i(TAG, "latlng set to " + String.valueOf(latitude) + "," + String.valueOf(longitude));
                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(userLocation)
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mMap.clear();
                    setHomeMarker(userLocation);
                    configureView();

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

        //configureView();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Location location = new Location("Your Destination");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                dest_lat = latLng.latitude;
                dest_lng = latLng.longitude;

                if(position >= 0 && place.getUser_lat() != latitude && place.getUser_lng() != longitude) {
                    alertUserForNewDirection(location);
                } else {
                    mMap.clear();
                    reSetHomeMarker();
                    setMarker(location);
                }
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                LatLng latLng = marker.getPosition();

                Location location = new Location("Your Destination");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                dest_lat = latLng.latitude;
                dest_lng = latLng.longitude;

                mMap.clear();
                reSetHomeMarker();
                setMarker(location);
            }
        });
    }

    private void alertUserForNewDirection(final Location location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose origin for new direction");

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newDirectionSet = false;
            }
        });

        builder.setPositiveButton("User Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newDirectionSet = true;
                setMarker(location);
            }
        });

        builder.setNegativeButton("Saved Destination", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newDirectionSet = true;
                latitude = place.getUser_lat();
                longitude = place.getUser_lng();
                setMarker(location);

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_location:
                if (directionRequested) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    String dateCreated = sdf.format(calendar.getTime());
                    String addr = getAddress(new LatLng(dest_lat, dest_lng));
                    addr = addr.isEmpty() ? dateCreated : addr;
                    CheckBox cb = findViewById(R.id.cb_visited);
                    boolean checked = cb.isChecked() ? true : false;

                    //Place place = new Place(addr, dateCreated, checked, dest_lat, dest_lng);
                    place.setAddr(addr);
                    place.setDate(dateCreated);
                    place.setVisited(checked);
                    place.setUser_lat(latitude);
                    place.setUser_lng(longitude);
                    place.setLat(dest_lat);
                    place.setLng(dest_lng);
                    DatabaseHelper mDatabase = new DatabaseHelper(this);
                    if(this.position >= 0 && newDirectionSet != true) {
                        if(mDatabase.updatePlace(place.getId(), place)) {
                            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "Changes not saved", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mDatabase.addPlace(place))
                            Toast.makeText(this, "Destination saved", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(this, "Destination not saved", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Cannot save.  No destination set!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear:
                mMap.clear();
                directionRequested = false;
                LatLng userLocation = new LatLng(this.latitude, this.longitude);
                setHomeMarker(userLocation);
                configureView();
                break;
            default:
                break;
        }
    }

    private void showNearby(String type) {
        String url = getUrl(latitude, longitude, type);
        Object[] dataTransfer = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        GetNearbyPlaceData getNearbyPlaceData = new GetNearbyPlaceData(this);
        getNearbyPlaceData.execute(dataTransfer);
        Toast.makeText(this, "Showing nearby " + type, Toast.LENGTH_SHORT).show();

        setHomeMarker();
        if (directionRequested) {
            Location location = destinationMarker();
            setMarker(location);
        }
    }

    private Location destinationMarker() {
        Location location = new Location("Your Destination");
        location.setLatitude(dest_lat);
        location.setLongitude(dest_lng);

        return location;
    }

    private String getAddress(LatLng location) {
        String address = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Log.i(TAG,"onLocationResult: " + addresses.get(0));
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


                Log.i(TAG,"getAddress: " + address);
            }
        } catch (IOException e) {
            e.printStackTrace(); }
        return address;
    }


    private void setDirection(String address) {
        double originLat = 0;
        double originLng = 0;
        double dLat = 0;
        double dLng = 0;

        if(newDirectionSet) {
            originLat = latitude;
            originLng = longitude;
            dLat = dest_lat;
            dLng = dest_lng;
        } else {
            originLat = place.getUser_lat();
            originLng = place.getUser_lng();
            dLat = place.getLat();
            dLng = place.getLng();
        }

        String url = userLocationChanged ? getSavedDirectionURL(new LatLng(originLat, originLng),
                new LatLng(dLat, dLng)) : getDirectionUrl();
        Object[] dataTransfer = new Object[3];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(dest_lat, dest_lng);

        GetDirectionData getDirectionData = new GetDirectionData(getApplicationContext(), address);
        getDirectionData.execute(dataTransfer);

    }

    private void setMarker(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        directionRequested = true;
        setDirection(getAddress(userLatLng));
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

    private String getSavedDirectionURL(LatLng origin, LatLng destination) {
        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin="+origin.latitude+","+origin.longitude);
        directionUrl.append("&destination="+destination.latitude+","+destination.longitude);
        directionUrl.append("&key="+ getString(R.string.api_key));
        Log.i(TAG, "getSavedDirectionURL: " + directionUrl.toString());
        return directionUrl.toString();
    }

    private String getDirectionUrl() {
        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin="+latitude+","+longitude);
        directionUrl.append("&destination="+dest_lat+","+dest_lng);
        directionUrl.append("&key="+ getString(R.string.api_key));
        Log.i(TAG, "getUrl direction" + directionUrl.toString());
        return directionUrl.toString();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeUrl.append("location="+latitude+","+longitude);
        placeUrl.append("&radius="+Constants.RADIUS);
        placeUrl.append("&type="+nearbyPlace);
        placeUrl.append("&key="+ getString(R.string.api_key));
        Log.i(TAG, "getUrl" + placeUrl.toString());
        return placeUrl.toString();
    }



}
