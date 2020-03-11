package com.nids.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VOOutdoor {
    String station_name;
    String measure_date;
    float pm100;
    float pm025;

    public VOOutdoor(){    }

    public VOOutdoor(String json_str){
        parseJSON(json_str);
    }

    public void setDataAsJSONString(String json_str){
        parseJSON(json_str);
    }

    private void parseJSON(String json_str){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json_str);
        JsonObject jsonObj = element.getAsJsonObject();

        JsonArray jsonArr = jsonObj.getAsJsonArray("list");
        measure_date = jsonArr.get(0).getAsJsonObject().get("dataTime").getAsString();
        pm100 = jsonArr.get(0).getAsJsonObject().get("pm10Value").getAsFloat();
        pm025 = jsonArr.get(0).getAsJsonObject().get("pm25Value").getAsFloat();
    }

    public void setStationName(String station) { this.station_name = station; }
    public String getStationName(){
        return this.station_name;
    }
    public String getMeasureDate(){
        return this.measure_date;
    }
    public float getPM100() { return this.pm100; }
    public float getPM025() { return this.pm025; }
}
