package com.nids.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Timestamp;

public class VOSensorData {
    private int idx;
    private String data;
    private String date;
    private String id;
    private int amount;
    private Float pm100;

    public VOSensorData(){}

    public VOSensorData(String json_str, String time){
        parseJSON(json_str);
        setDate(time);
    }

    private void parseJSON(String json_str){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json_str);
        JsonObject jsonObj = element.getAsJsonObject();

        JsonArray jsonArr = jsonObj.getAsJsonArray("raw");
        JsonObject rawObj = jsonArr.get(0).getAsJsonObject();
        JsonObject dataObj = rawObj.get("data").getAsJsonObject();
        pm100 = dataObj.get("PM10").getAsFloat();
    }

    public int getIdx(){ return this.idx; }
    public void setIdx(int idx) { this.idx = idx; }
    public String getData() { return this.data; }
    public void setData(String data){ this.data = data; }
    public String getDate() { return this.date; }
    public void setDate(String date){ this.date = date; }
    public String getId() { return this.id; }
    public void setId(String id){ this.id = id; }
    public int getAmount() { return this.amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public Float getPm100() {
        return pm100;
    }

    public void setPm100(Float pm100) {
        this.pm100 = pm100;
    }
}
