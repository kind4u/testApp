package com.nids.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.fragment.app.Fragment;

import com.nids.kind4u.testapp.R;

public class NewCarFragment extends Fragment {

    CarActivity caractivity;

    public interface OnNewCarSetListener{
        void onNewCarSet(String newcarNumResult);
    }

    private OnNewCarSetListener onNewCarSetLisener;

    private EditText edit_newcarNum1;
    private EditText edit_newcarNum2;
    private EditText edit_newcarNum3;

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

    private void bindComponents(View v) {
        edit_newcarNum1 = v.findViewById(R.id.edit_newcarNum1);
        edit_newcarNum2 = v.findViewById(R.id.edit_newcarNum2);
        edit_newcarNum3 = v.findViewById(R.id.edit_newcarNum3);
    }

    void getCarInfo1(){
        // TODO: 사용자가 입력한 차량 정보 받아오는 함수
        String newcarNum1 = edit_newcarNum1.getText().toString();
        String newcarNum2 = edit_newcarNum2.getText().toString();
        String newcarNum3 = edit_newcarNum3.getText().toString();

        String result = newcarNum1 +" "+ newcarNum2 +" "+ newcarNum3;
        passCarInfo(result);
    }

    private void passCarInfo(String newcarNumResult){        // 차량 등록 lisener
        onNewCarSetLisener.onNewCarSet(newcarNumResult);
    }

}
