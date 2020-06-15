package com.nids.util.interfaces;

import com.nids.data.VOUser;

public interface JoinCallBackInterface {
    void getUserResult(boolean result, String message, VOUser userinfo);
    void carResult(boolean insert, String result, String message);
    void deleteCarResult(boolean delete, String result, String message);
    void editCarResult(boolean edit, String result, String message);
    void checkCarResult(String result, boolean exist);
    void signUpResult(boolean insert, String result, String message);
    void positionResult(boolean position_result, String data);
    void existResult(String result, boolean exist);
}
