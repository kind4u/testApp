package com.nids.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nids.kind4u.testapp.R;

public class CarActivity extends AppCompatActivity {

  TextView ex_text;
  CheckBox checkCar;

  Button registCar;
  Button registLater;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Intent intent = getIntent();
        String strParamId = intent.getStringExtra("id");

        ex_text= (TextView) findViewById(R.id.ex_ext_view);
        ex_text.setText(strParamId);

        checkCar = (CheckBox) findViewById(R.id.checkCar);
        checkCar.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v){
                if(((CheckBox)v).isChecked()){
                    //TODO: CheckBox is checked.
                    changeView(0);
                }
                else{
                    //TODO: CheckBox is unchecked
                    changeView(1);
                }
            }
        });

        registCar = (Button) findViewById(R.id.registCar);
        registCar.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO: 차량정보 등록
            }
        });


        registLater = (Button) findViewById(R.id.registLater);
        registLater.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CarActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        changeView(1);
    }

    private void changeView(int index){

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        if(frame.getChildCount()>0) {
            frame.removeViewAt(0);
        }

        View view = null;
        switch (index){
            case 0:
                view = inflater.inflate(R.layout.fragment_oldcar, frame, false);
                break ;
            case 1:
                view = inflater.inflate(R.layout.fragment_newcar, frame, false);
                break;
        }
        if(view != null){
            frame.addView(view);
        }
    }
}
