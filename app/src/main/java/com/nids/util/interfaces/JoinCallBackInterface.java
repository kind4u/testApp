package com.nids.util.interfaces;

import com.nids.data.VOUser;

public interface JoinCallBackInterface {
    public void joinResult(boolean insert, String result);
    public void joinResult(boolean insert, String result, String message);
    public void positionResult(boolean position_result, String data);
    public void existResult(String result, boolean exist);
}
