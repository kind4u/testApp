package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

public class JoinActivity extends AppCompatActivity {

    CommunicationUtil c_util_join;

    String id;
    String pw;
    String name;
    String birth;
    String phone;

    EditText edit_id;
    EditText edit_pw;
    EditText edit_name;
    EditText edit_birth;
    EditText edit_phone;

    Button btn_signup;
    Button btn_duplicate;

    JoinCallBackInterface joinCallBackInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        edit_id = findViewById(R.id.edit_id);
        edit_pw = findViewById(R.id.edit_pw);
        edit_name = findViewById(R.id.edit_name);
        edit_birth = findViewById(R.id.edit_birth);
        edit_phone = findViewById(R.id.edit_phone);

        btn_signup = findViewById(R.id.signUpButton);
        bindview2();
    }
    private void bindview2()    {

        joinCallBackInstance = new JoinCallBackInterface() {
            boolean signup_result;

            @Override
            public void signUpResult(boolean insert, String result) {
                if (insert) {
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                            btn_signup.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                    btn_signup.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void signUpResult(boolean insert, String result, String message) {
                if (insert) {
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                            btn_signup.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    // TODO : 예외처리 고려
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                            btn_signup.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        c_util_join = new CommunicationUtil(joinCallBackInstance);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 회원가입 구현
                String id_text = edit_id.getText().toString();
                String pw_text = edit_pw.getText().toString();
                String name_text = edit_name.getText().toString();
                String birth_text = edit_birth.getText().toString();
                String phone_text = edit_phone.getText().toString();

                if(id_text.equals("") || pw_text.equals("") || name_text.equals("") || birth_text.equals("") || phone_text.equals("")){
                    Toast.makeText(JoinActivity.this, "모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return;
                    // TODO : 아이디 중복 처리를 하지 않았을 때의 예외처리
                }   else    {
                    id = id_text;
                    pw = pw_text;
                    name = name_text;
                    birth = birth_text;
                    phone = phone_text;
                }

                btn_duplicate.setEnabled(false);
                btn_signup.setEnabled(false);
                findViewById(R.id.joinLoadingPannel).setVisibility(View.VISIBLE);
                Log.d("id",id); Log.d("pw",pw);Log.d("name",name);Log.d("birth",birth);Log.d("phone",phone);
                c_util_join.signUp(id, pw, name, birth, phone);  // TODO : signUp 함수 구현

//                Toast.makeText(getApplicationContext(),"회원가입 한 척", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
//                startActivity(intent);
            }
        });
        btn_duplicate = findViewById(R.id.btn_duplicate);
        btn_duplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 아이디 중복 확인
            }
        });
    }
}
