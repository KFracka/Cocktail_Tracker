package com.example.cocktail_tracker;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Coordinates {
    public LatLng latlon;
    public String locationName;


    public Coordinates(){
    }

    public Coordinates(String locationName, LatLng latlon){
        this.locationName = locationName;
        this.latlon = latlon;

    }
}
