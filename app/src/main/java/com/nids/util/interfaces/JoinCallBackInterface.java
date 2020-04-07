package com.nids.util.interfaces;

import com.nids.data.VOUser;

public interface JoinCallBackInterface {
    public void carResult(boolean insert, String result, String message);
    public void signUpResult(boolean insert, String result, String message);
    public void positionResult(boolean position_result, String data);
    public void existResult(String result, boolean exist);
}
