package com.nids.util;

public class InfoItem {
    String infoName;
    String desc;

    public InfoItem(String infoName, String desc) {
        this.infoName = infoName;
        this.desc = desc;
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
