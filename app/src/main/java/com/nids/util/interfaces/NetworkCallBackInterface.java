package com.nids.util.interfaces;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;

import java.util.List;

public interface NetworkCallBackInterface  {

    public void signInResult(boolean result, String message, VOUser userinfo);
    public void modifyResult(boolean result);
    public void findStation(boolean result, VOStation station_info);
    public void dataReqResult(String result, List<VOSensorData> dataList);
    public void dataReqResultOutdoor(boolean result, VOOutdoor data);
}

