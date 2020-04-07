package com.nids.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import androidx.fragment.app.Fragment;

import com.nids.kind4u.testapp.R;

public class NewCarFragment extends Fragment {

    CarActivity caractivity;

    public interface OnNewCarSetListener{
        void onNewCarSet(String newcarNumResult);
    }

    OnNewCarSetListener onNewCarSetLisener;

    String newcarNum1;
    String newcarNum2;
    String newcarNum3;

    EditText edit_newcarNum1;
    EditText edit_newcarNum2;
    EditText edit_newcarNum3;

    public NewCarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof OnNewCarSetListener){
            onNewCarSetLisener = (OnNewCarSetListener) context;
        }else{
            throw new RuntimeException(context.toString()+"must implement OnNewCarSetListener");
          }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        onNewCarSetLisener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_newcar, container, false);
        bindComponents(v);
        return v;
    }

    public void bindComponents(View v) {
        edit_newcarNum1 = v.findViewById(R.id.edit_newcarNum1);
        edit_newcarNum2 = v.findViewById(R.id.edit_newcarNum2);
        edit_newcarNum3 = v.findViewById(R.id.edit_newcarNum3);
    }

    public void getCarInfo1(){
        // TODO: 사용자가 입력한 차량 정보 받아오는 함수
        newcarNum1 = edit_newcarNum1.getText().toString();
        newcarNum2 = edit_newcarNum2.getText().toString();
        newcarNum3 = edit_newcarNum3.getText().toString();

        String result = newcarNum1+" "+newcarNum2+" "+newcarNum3;
        passCarInfo(result);
    }

    public void passCarInfo(String newcarNumResult){        // 차량 등록 lisener
        onNewCarSetLisener.onNewCarSet(newcarNumResult);
    }

}
