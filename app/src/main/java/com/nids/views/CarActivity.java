package com.nids.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.network.CommunicationUtil;

public class CarActivity extends AppCompatActivity implements NewCarFragment.OnNewCarSetListener, OldCarFragment.OnOldCarSetListener {

    JoinCallBackInterface joinCallBackInterface;
    CommunicationUtil c_util_car;

    CheckBox checkCar;

    Button registCar;
    Button registLater;

    String num;

    String id;
    int m;
    int model;
    String strParamId;
    String strParmPage;

    OldCarFragment oldCarFragment;
    NewCarFragment newCarFragment;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        oldCarFragment = new OldCarFragment();
        newCarFragment = new NewCarFragment();
        m = 1;
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
            public void commitNow() {
            }

            @Override
            public void commitNowAllowingStateLoss() {
            }
        };

        Intent intent = getIntent();
        strParamId = intent.getStringExtra("id"); //차량등록시 join 혹은 main에서 아이디 받아옴
        strParmPage = intent.getStringExtra("page"); //이전 페이지가 join 인지 main인지 string 으로 받아옴

        checkCar = findViewById(R.id.checkCar);
        registCar = findViewById(R.id.registCar);
        registLater = findViewById(R.id.registLater);

        bindView();
    }

    private void bindView() {

        joinCallBackInterface = new JoinCallBackInterface() {
            @Override
            public void getUserResult(boolean result, String message, VOUser userinfo){  }

            @Override
            public void carResult(boolean insert, String result, String message) {
                if (insert) {
                    CarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                            registCar.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "차량등록 성공", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.carLoadingPannel).setVisibility(View.GONE);
                            if (strParmPage.equals("main")) {
                                Intent intent = new Intent(CarActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else if (strParmPage.equals("car")) {
                                Intent intent = new Intent(CarActivity.this, MainActivity.class);
                                intent.putExtra("id", strParamId);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
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

            @Override
            public void deleteCarResult(boolean delete, String result, String message) {
            }

            @Override
            public void editCarResult(boolean edit, String result, String message) {
            }

            @Override
            public void checkCarResult(String result, boolean exist) {
            }

            @Override
            public void signUpResult(boolean insert, String result, String message) {
            }

            @Override
            public void positionResult(boolean position_result, String data) {
            }

            @Override
            public void existResult(String result, boolean exist) {
            }
        };

        c_util_car = new CommunicationUtil(joinCallBackInterface);

        checkCar.setOnClickListener(new CheckBox.OnClickListener() { //차량 번호판 신형or구형 선택했을때
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) { //구형 번호판 선택한 경우 (Checkbox is checked)
                    m = 0;
                    changeView(0);
                } else { //신형 번호판 선택한 경우 (Checkbox is unchecked)
                    m = 1;
                    changeView(1);
                }
            }
        });
        registCar.setOnClickListener(new Button.OnClickListener() { //차량 등록 버튼 눌렀을 때
            @Override
            public void onClick(View v) {

                if (m == 0) { //차량이 구형 번호판일 경우
                    fragmentTransaction.add(oldCarFragment, "oldCarFragment");
                    OldCarFragment nf0 = (OldCarFragment) getSupportFragmentManager().findFragmentByTag("oldCarFragment");
                    assert nf0 != null;
                    nf0.getCarInfo0();
                } else if (m == 1) { //차량이 신형 번호판일 경우
                    fragmentTransaction.add(newCarFragment, "newCarFragment");
                    NewCarFragment nf1 = (NewCarFragment) getSupportFragmentManager().findFragmentByTag("newCarFragment");
                    assert nf1 != null;
                    nf1.getCarInfo1();
                }

                model = m;
                id = strParamId;

                findViewById(R.id.carLoadingPannel).setVisibility(View.VISIBLE);
                c_util_car.registCar(num, id, model);
            }
        });
        registLater.setOnClickListener(new Button.OnClickListener() { //차량 등록 나중에하기 버튼 클릭시
            @Override
            public void onClick(View v) {
                switch (strParmPage) {
                    case "main":
                        Intent intent = new Intent(CarActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case "car":
                        finish();
                        break;
                }
            }
        });

        changeView(1);
    }


    private void changeView(int index) {  //신형 구형 뷰 선택
        switch (index) {
            case 0: //구형
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, oldCarFragment).commit();
                break;
            case 1: //신형
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, newCarFragment).commit();
                break;
        }

    }

    @Override
    public void onNewCarSet(String newcarNumResult) {
        this.num = newcarNumResult;
    }

    @Override
    public void onOldCarSet(String oldcarNumResult) {
        this.num = oldcarNumResult;
    }
}
