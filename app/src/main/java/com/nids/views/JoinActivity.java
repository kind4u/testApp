package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android .text.Editable;
import android .text.TextWatcher;

import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

public class JoinActivity extends AppCompatActivity {

    private EditText idText_;
    private EditText pwText_;
    private EditText pwConfirm_;
//    private EditText nameText_;
//    private EditText birthText_;
//    private  EditText phoneText_
//    private  EditText zipCodeText_;
//    private EditText adText_;
//    private EditText addtText_;

    Button btn_signup;
    //Button btn_duplicate;

    CommunicationUtil c_util;
    NetworkCallBackInterface callbackInstance;

    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idText_ = (EditText)findViewById(R.id.idText);
        pwText_ = (EditText)findViewById(R.id.pwText);
        pwConfirm_ = (EditText)findViewById(R.id.pwConfirm);
//        nameText_ = (EditText)findViewById(R.id.nameText);
//        birthText_ = (EditText)findViewById(R.id.birthText);
//        phoneText_ = (EditText)findViewById(R.id.phoneText);
//        zipCodeText_ = (EditText)findViewById(R.id.zipCodeText);
//        adText_ = (EditText)findViewById(R.id.adText);
//        addtText_ = (EditText)findViewById(R.id.addtText);


//        btn_duplicate = (Button)findViewById(R.id.btn_duplicate);
//        btn_duplicate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO : 아이디 중복 확인
//            }
//        });

    }


    //입력정보 확인

    public void onCheck(View v){

        //아이디 입력 확인
        if(idText_.getText().toString().length()==0){
            Toast.makeText(JoinActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
            idText_.requestFocus();
            return;
        }
        //비밀번호 입력확인
        if(pwText_.getText().toString().length()==0){
            Toast.makeText(JoinActivity.this, "password를 입력하세요.", Toast.LENGTH_SHORT).show();
            pwText_.requestFocus();
            return;
        }
        //비밀번호 확인 입력확인
        if(pwConfirm_.getText().toString().length()==0){
            Toast.makeText(JoinActivity.this, "password 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
            pwConfirm_.requestFocus();
            return;
        }
        //비밀번호 일치 확인z
        if(!pwText_.getText().toString().equals(pwConfirm_.getText().toString())){
            Toast.makeText(JoinActivity.this,"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            pwText_.setText("");
            pwConfirm_.setText("");
            pwText_.requestFocus();
            return;
        }



        c_util = new CommunicationUtil(callbackInstance);

        btn_signup = (Button)findViewById(R.id.signUpButton);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 회원가입 구현
                String id = idText_.getText().toString();
                String pw = pwText_.getText().toString();
                String pwCon = pwConfirm_.getText().toString();
//        String name = nameText_.getText().toString();
//        String birth = birthText_.getText().toString();
//        String phone = phoneText_.getText().toString();


                btn_signup.setEnabled(false);
                c_util.join(id, pw);

                Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
