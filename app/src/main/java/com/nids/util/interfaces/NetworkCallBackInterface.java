package com.nids.util.interfaces;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;

import java.util.List;

public interface NetworkCallBackInterface  {
    void signInResult(boolean result, String message, VOUser userinfo);
    void modifyResult(boolean result);
    void findStation(boolean result, VOStation station_info);
    void dataReqResult(String result, List<VOSensorData> dataList);
    void dataReqResultOutdoor(boolean result, VOOutdoor data);
}

