package com.nids.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.nids.kind4u.testapp.R;

public class OldCarFragment extends Fragment {

    public interface OnOldCarSetListener{
        void onOldCarSet(String oldcarNumResult);
    }

    OnOldCarSetListener onOldCarSetLisener;

    CarActivity caractivity;

    String oldcarNum0;
    String oldcarNum1;
    String oldcarNum2;
    String oldcarNum3;

    EditText edit_oldcarNum0;
    EditText edit_oldcarNum1;
    EditText edit_oldcarNum2;
    EditText edit_oldcarNum3;


    public OldCarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof OldCarFragment.OnOldCarSetListener){
            onOldCarSetLisener = (OldCarFragment.OnOldCarSetListener) context;
        }else{
            throw new RuntimeException(context.toString()+"must implement OnOldCarSetListener");
        }
    }


    @Override
    public void onDetach(){
        super.onDetach();
        onOldCarSetLisener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_oldcar, container, false);
        bindComponents(v);
        return v;
    }

    public void bindComponents(View v) {
        edit_oldcarNum0 = v.findViewById(R.id.edit_oldcarNum0);
        edit_oldcarNum1 = v.findViewById(R.id.edit_oldcarNum1);
        edit_oldcarNum2 = v.findViewById(R.id.edit_oldcarNum2);
        edit_oldcarNum3 = v.findViewById(R.id.edit_oldcarNum3);
    }

    public void getCarInfo0(){
        // TODO: 사용자가 입력한 차량 정보 받아오는 함수
        oldcarNum0 = edit_oldcarNum0.getText().toString();
        oldcarNum1 = edit_oldcarNum1.getText().toString();
        oldcarNum2 = edit_oldcarNum2.getText().toString();
        oldcarNum3 = edit_oldcarNum3.getText().toString();

        String result = oldcarNum0+" "+oldcarNum1+" "+oldcarNum2+" "+oldcarNum3;
        passCarInfo(result);
    }

    public void passCarInfo(String oldcarNumResult){
        onOldCarSetLisener.onOldCarSet(oldcarNumResult);
    }


}
