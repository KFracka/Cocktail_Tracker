package com.example.cocktail_tracker;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SearchView searchView;
    private int ACCESS_LOCATION_REQUEST_CODE = 1001;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private Marker marker;
    public LatLng[] savedMarkers = new LatLng[0];
    public String[] markersTitles = new String[0];

    SharedPreferences sharedPreferences;
    int locationCount = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();


                List<Address> addressList = null;
                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    //After getting the location from search bar, add to database
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    sharedPreferences = getSharedPreferences("loc", 0);

                    String key = mDatabase.child("coordinates").push().getKey();

                    Coordinates coordinates = new Coordinates(location,latLng);

                    Map<String, Object> postValues = coordinates.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/coordinates/" + key, postValues);

                    mDatabase.updateChildren(childUpdates);

                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    marker.setTag(0);
                    for (int i = 0; i < savedMarkers.length; i++){
                        for (int j = 0; j <markersTitles.length; j++) {
                            savedMarkers[i] = latLng;
                            markersTitles[j] = location;
                        }
                    }

//                    mDatabase.child("coordinates").setValue(coordinates);
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

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
        if (ContextCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) == getPackageManager().PERMISSION_GRANTED){
            enableUserLocation();
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }

        // Add a marker in Sydney and move the camera
        LatLng wzr = new LatLng(54.442684, 18.555919);
        mMap.addMarker(new MarkerOptions().position(wzr).title("Marker in University of Gdansk - WZR"));
        for (LatLng savedMarke: savedMarkers
             ) {
            for (String markeTitle : markersTitles
                 ) {
                mMap.addMarker(new MarkerOptions().position(savedMarke).title("Marker in " + markeTitle).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(savedMarke, 20));
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(wzr));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wzr, 20));



    }
    private void enableUserLocation(){
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED){
                enableUserLocation();
            }
            else{

            }
        }
    }
}