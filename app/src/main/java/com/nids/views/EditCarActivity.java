package com.nids.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.network.CommunicationUtil;

public class EditCarActivity extends AppCompatActivity implements NewCarFragment.OnNewCarSetListener, OldCarFragment.OnOldCarSetListener{

  JoinCallBackInterface joinCallBackInterface;
  CommunicationUtil c_util_car;

  CheckBox checkCar;

  Button editCar;
  Button deleteCar;

  String num;
  String id;
  int m;
  int model;
  String strParamId;
  int check;

  OldCarFragment oldCarFragment;
  NewCarFragment newCarFragment;
  FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcar);

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
        editCar =(Button) findViewById(R.id.editCar);
        deleteCar = (Button) findViewById(R.id.deleteCar);

        bindView();
    }

        private void bindView(){

            joinCallBackInterface = new JoinCallBackInterface() {
                @Override
                public void getUserResult(boolean result, String message, VOUser userinfo){  }

                @Override
                public void carResult(boolean insert, String result, String message) { }

                @Override
                public void deleteCarResult(boolean delete, String result, String message){
                    if (delete) {
                        EditCarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "차량 삭제 성공", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // TODO : 예외처리 고려
                        EditCarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "차량 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void editCarResult(boolean edit, String result, String message){
                    if (edit) {
                        EditCarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editCar.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "차량 정보 수정 성공", Toast.LENGTH_SHORT).show();
                                findViewById(R.id.carLoadingPannel).setVisibility(View.GONE);
                            }
                        });
                    } else {
                        // TODO : 예외처리 고려
                        EditCarActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "차량 정보 수정 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void checkCarResult(String result, boolean exist){ }

                @Override
                public void signUpResult(boolean insert, String result, String message) { }

                @Override
                public void naverSignUpResult(boolean insert, String result, String message) {
                    //TODO: 네이버 연동 로그인
                }

                @Override
                public void positionResult(boolean position_result, String data) { }

                @Override
                public void existResult(String result, boolean exist) { }
           };
            c_util_car = new CommunicationUtil(joinCallBackInterface);

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
            editCar.setOnClickListener(new Button.OnClickListener(){ //차량 정보 수정 버튼 눌렀을 때
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

                        findViewById(R.id.carLoadingPannel).setVisibility(View.VISIBLE);
                        c_util_car.editCar(num, id, model);
                        Intent intent = new Intent(EditCarActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public void OnClickHandler(View view){ //차량 정보 삭제 버튼 클릭시 알림창
        c_util_car = new CommunicationUtil(joinCallBackInterface);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("차량 정보 삭제").setMessage("차량 정보를 정말 삭제하시겠습니까?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: 차량 정보 삭제
                id = strParamId;
                c_util_car.deleteCar(num, id, model);

                Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditCarActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onNewCarSet(String newcarNumResult){
        this.num = newcarNumResult;
    }

    @Override
    public void onOldCarSet(String oldcarNumResult){
        this.num = oldcarNumResult;
    }
}
