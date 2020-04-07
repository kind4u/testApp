package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.util.List;

public class LoginActivity extends AppCompatActivity  {

    Button btn_set;
    Button button;
    Button btn_signin;
    Button btn_join;

    EditText edit_id;
    EditText edit_pw;

    String id;
    String pw;

    CommunicationUtil c_util;
    NetworkCallBackInterface callbackInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        bindView();
    }

    private void bindView(){
        callbackInstance = new NetworkCallBackInterface() {
            VOUser user;
            @Override
            public void signInResult(boolean result, String message, VOUser userInfo) {
                if(result)
                {
                    user = userInfo;
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.loginLoadingPannel).setVisibility(View.GONE);
                            btn_signin.setEnabled(true);
                            btn_join.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("id",user.getId());
                            startActivity(intent);
                        }
                    });
                }
                else
                {
                    if(!message.equals("Connection Error")) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.loginLoadingPannel).setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                btn_signin.setEnabled(true);
                                btn_join.setEnabled(true);
                            }
                        });
                    }
                    else{
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.loginLoadingPannel).setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "연결 실패", Toast.LENGTH_SHORT).show();
                                btn_signin.setEnabled(true);
                                btn_join.setEnabled(true);
                            }
                        });
                    }
                }
            }

            @Override
            public void findStation(boolean result, final VOStation station_info) { }
            @Override
            public void dataReqResult(String result, List<VOSensorData> data) { }
            @Override
            public void dataReqResultOutdoor(boolean result, VOOutdoor data) { }
        };

        c_util = new CommunicationUtil(callbackInstance);

        btn_signin = (Button)findViewById(R.id.btn_signin);
        btn_join = (Button)findViewById(R.id.btn_join);
        edit_id = (EditText)findViewById(R.id.edit_id);
        edit_pw = (EditText)findViewById(R.id.edit_pw);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id_text = edit_id.getText().toString();
                String pw_text = edit_pw.getText().toString();
                if(!id_text.equals("")){ id = id_text; }
                else{ Toast.makeText(LoginActivity.this, "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }

                if(!pw_text.equals("")){ pw = pw_text; }
                else{ Toast.makeText(LoginActivity.this, "패스워드를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return; }


                btn_signin.setEnabled(false);
                btn_join.setEnabled(false);
                findViewById(R.id.loginLoadingPannel).setVisibility(View.VISIBLE);
                c_util.signIn(id, pw);
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(joinIntent);
            }
        });

      //차량 등록 테스트
        button =(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent carIntent = new Intent(LoginActivity.this, CarActivity.class);
                startActivity(carIntent);
                }
        });
    }

}