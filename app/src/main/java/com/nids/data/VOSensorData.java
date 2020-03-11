package com.nids.data;

import java.sql.Timestamp;

public class VOSensorData {
    private int idx;
    private String data;
    private Timestamp date;
    private String id;
    private int amount;

    public VOSensorData(){}

    public int getIdx(){ return this.idx; }
    public void setIdx(int idx) { this.idx = idx; }
    public String getData() { return this.data; }
    public void setData(String data){ this.data = data; }
    public Timestamp getDate() { return this.date; }
    public void setDate(Timestamp date){ this.date = date; }
    public String getId() { return this.id; }
    public void setId(String id){ this.id = id; }
    public int getAmount() { return this.amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
