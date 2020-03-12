package com.nids.util.interfaces;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;

import java.util.List;

public interface NetworkCallBackInterface  {
    public void joinResult(String result, boolean insert, VOUser userinfo); //매개변수는 다시 체크하기
    public void signInResult(boolean result, String message, VOUser userinfo);
    public void findStation(boolean result, VOStation station_info);
    public void dataReqResult(boolean result, List<VOSensorData> data);
    public void dataReqResultOutdoor(boolean result, VOOutdoor data);

}


