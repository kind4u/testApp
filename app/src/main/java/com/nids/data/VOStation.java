package com.nids.data;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;

public class VOStation implements Serializable {
    private String station_name;
    public VOStation(){}
    public VOStation(String json_str){
        parseJSON(json_str);
    }
    private void parseJSON(String json_str){

        //Log.d("VOStation", json_str);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json_str);
        JsonObject jsonObj = element.getAsJsonObject();
        JsonArray jsonArr = jsonObj.getAsJsonArray("list");
        station_name = jsonArr.get(0).getAsJsonObject().get("stationName").getAsString();
        Log.d("VOStation", station_name);
    }
    public String getStationName() { return this.station_name; }
}
