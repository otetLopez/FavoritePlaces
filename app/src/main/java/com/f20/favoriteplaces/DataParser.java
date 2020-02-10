package com.f20.favoriteplaces;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String,String> getPlace(JSONObject jsonObject) {
        HashMap<String, String> place = new HashMap<>();
        String placeName = "N/A";
        String vicinity = "N/A";
        String latitude = "";
        String longitude = "";
        String reference = "";

        /** Note on this : https://developers.google.com/places/web-service/search
         * Then change the API key to fetch JSON sample data
         *
         * get the API from
         * https://console.cloud.google.com/google/maps-apis/apis/places-backend.googleapis.com/credentials?project=gentle-analyst-266820 */

        try {
            if (!jsonObject.isNull("name"))
                placeName = jsonObject.getString("name");

            if(!jsonObject.isNull("vicinity"))
                vicinity = jsonObject.getString("vicinity");

            latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = jsonObject.getString("reference");
            place.put("placeName", placeName);
            place.put("vicinity", vicinity);
            place.put("latitude", latitude);
            place.put("longitude", longitude);
            place.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String,String> place = null;

        for(int i =0; i<count; ++i) {
            try {
                place = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(place);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return placesList;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    /** Assign an on-click in xml to make this work */


    public HashMap<String, String> parseDistance(String jsonData) {
        /** Check JSON routes in accessing the data*/
        JSONArray jsonArray = null;
        try {

            JSONObject jsonObject = new JSONObject(jsonData);
            Log.i("DataParser", "parseDistance: " + "trying to parse distance");
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
            Log.i("DataParser", "parseDistance: " + "got legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getDuration(jsonArray);
    }

    public HashMap<String, String> getDuration(JSONArray directionJsonData) {
        HashMap<String, String> directionMap = new HashMap<>();
        String duration = "";
        String distance = "";
        try {
            Log.i("DataParser", "getDuration: " + "trying to get duration");
            duration = directionJsonData.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = directionJsonData.getJSONObject(0).getJSONObject("distance").getString("text");
            Log.i("DataParser", "getDuration: " + "fucking got it");

            directionMap.put("duration", duration);
            directionMap.put("distance", distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return directionMap;
    }


    public String[] parseDirections(String jsonData) {

        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    public String[] getPaths(JSONArray jsonArray) {
        int count = jsonArray.length();
        String[] polilines = new String[count];

        for (int i=0; i< count; ++i) {
            try {
                polilines[i] = getPath(jsonArray.getJSONObject((i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polilines;
    }

    private String getPath(JSONObject jsonObject) {
        String polyline = "";
        try {
            polyline = jsonObject.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  polyline;
    }
}
