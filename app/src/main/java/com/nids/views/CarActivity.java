package com.nids.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.CarCallBackInterface;
import com.nids.util.network.CommunicationUtil;

public class CarActivity extends AppCompatActivity implements NewCarFragment.OnNewCarSetListener, OldCarFragment.OnOldCarSetListener{

    CarCallBackInterface carCallBackInstance;
    CommunicationUtil c_util_car;

  TextView ex_text;
  CheckBox checkCar;

  Button registCar;
  Button registLater;

  String num;

  String id;
  int m;
  int model;
  String strParamId;

  OldCarFragment oldCarFragment;
  NewCarFragment newCarFragment;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        oldCarFragment = new OldCarFragment();
        newCarFragment = new NewCarFragment();
        m=1;
        fragmentTransaction = new FragmentTransaction() {
            @Override
            public int commit() {
                return 0;
            }

            @Override
            public int commitAllowingStateLoss() {
                return 0;
            }

            @Override
            public void commitNow() { }

            @Override
            public void commitNowAllowingStateLoss() { }
        };

        Intent intent = getIntent();
        strParamId = intent.getStringExtra("id"); //회원가입 후 아이디 받아옴

        checkCar = (CheckBox) findViewById(R.id.checkCar);
        registCar =(Button) findViewById(R.id.registCar);
        registLater = (Button) findViewById(R.id.registLater);

        bindView();

    }

        private void bindView(){

            carCallBackInstance = new CarCallBackInterface(){
                @Override
                public void carResult(boolean insert, String result) {
                    if (insert) {
                        CarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                                registCar.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "차량등록 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CarActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    } else {
                        //findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                        registCar.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "차량등록 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void carResult(boolean insert, String result, String message) {
                    if (insert) {
                        CarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                                registCar.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "차량등록 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CarActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    } else {
                        // TODO : 예외처리 고려
                        CarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                                registCar.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "차량등록 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            };

            c_util_car = new CommunicationUtil(carCallBackInstance);

            checkCar.setOnClickListener(new CheckBox.OnClickListener(){ //차량 번호판 신형or구형 선택했을때
                @Override
                public void onClick(View v){
                    if(((CheckBox)v).isChecked()){ //구형 번호판 선택한 경우
                        //TODO: CheckBox is checked.
                        m=0;
                        changeView(0);
                    }
                    else{ //신형 번호판 선택한 경우
                        //TODO: CheckBox is unchecked
                        m=1;
                        changeView(1);
                    }
                }
            });
            registCar.setOnClickListener(new Button.OnClickListener(){ //차량 등록 버튼 눌렀을 때
                @Override
                public void onClick(View v){
                    //TODO: 차량정보 등록

                    if(m == 0){ //차량이 구형 번호판일 경우
                        fragmentTransaction.add(oldCarFragment,"oldCarFragment");
                        OldCarFragment nf0 = (OldCarFragment) getSupportFragmentManager().findFragmentByTag("oldCarFragment");
                        nf0.getCarInfo0();
                    }else if(m == 1){ //차량이 신형 번호판일 경우
                        fragmentTransaction.add(newCarFragment,"newCarFragment");
                        NewCarFragment nf1 = (NewCarFragment) getSupportFragmentManager().findFragmentByTag("newCarFragment");
                        nf1.getCarInfo1();
                    }

                    model = m;
                    id = strParamId;

                    c_util_car.registCar(num, id, model);

                    Intent intent1 = new Intent(CarActivity.this, LoginActivity.class);
                    startActivity(intent1);
                }
            });
            registLater.setOnClickListener(new Button.OnClickListener(){ //차량 등록 나중에하기 버튼 클릭시
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(CarActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            changeView(1);
        }


    private void changeView(int index){

        switch (index){
            case 0: //구형
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, oldCarFragment).commit();
                break ;
            case 1: //신형
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, newCarFragment).commit();
                break;
        }

    }

    @Override
    public void onNewCarSet(String newcarNumResult){
        this.num =newcarNumResult;
    }

    @Override
    public void onOldCarSet(String oldcarNumResult){
        this.num =oldcarNumResult;
    }
}
