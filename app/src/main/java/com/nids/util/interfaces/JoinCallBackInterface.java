package com.nids.util.interfaces;

public interface JoinCallBackInterface  {
    public void signUpResult(boolean insert, String result);
    public void signUpResult(boolean insert, String result, String message);
    public void positionResult(boolean position_result, String data);
}
