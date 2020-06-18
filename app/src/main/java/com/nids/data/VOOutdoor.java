package com.nids.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VOOutdoor {
    private String station_name;
    private String measure_date;
    private float pm100;
    private float pm025;

    public VOOutdoor(){
//        this.station_name="미측정";
//        this.pm100 = 0;
    }

    public VOOutdoor(String json_str){
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
    public boolean isNull() {
        return station_name == null || measure_date == null || pm100 == 0.0 || pm025 == 0.0;
    }
}
