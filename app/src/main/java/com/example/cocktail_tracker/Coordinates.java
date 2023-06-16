package com.example.cocktail_tracker;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

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

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("coordinates", latlon);
        result.put("name", locationName);

        return result;
    }
}
