package com.nids.util.interfaces;

import com.nids.data.VOCar;

public interface CarCallBackInterface {
    public void carResult(boolean insert, String result);
    public void carResult(boolean insert, String result, String message);
}
