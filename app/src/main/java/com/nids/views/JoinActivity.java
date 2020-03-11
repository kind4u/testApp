package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nids.kind4u.testapp.R;

public class JoinActivity extends AppCompatActivity {

    EditText idText;
    EditText passwordText;
    Button btn_signup;
    Button btn_duplicate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        btn_signup = findViewById(R.id.signUpButton);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 회원가입 구현
                Toast.makeText(getApplicationContext(),"회원가입 한 척", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
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
