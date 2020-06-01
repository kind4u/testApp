package com.nids.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
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

public class ValidationActivity extends AppCompatActivity {

    NetworkCallBackInterface callbackInstance;
    CommunicationUtil communicationUtil;

    EditText idValText;
    EditText pwValText;
    Button validationButton;

    private String id;
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        idValText = findViewById(R.id.idValText);
        pwValText = findViewById(R.id.pwValText);
        validationButton = findViewById(R.id.validationButton);

        Intent intent = getIntent();
        id = intent.getExtras().get("id").toString();
        idValText.setText("   사용자 ID: "+id);
        idValText.setEnabled(false);

        callbackInstance = new NetworkCallBackInterface() {
            VOUser user;
            @Override
            public void signInResult(boolean result, String message, VOUser userInfo) {
                if(result)
                {
                    user = userInfo;
                    ValidationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            findViewById(R.id.valLoadingPannel).setVisibility(View.GONE);
                            Intent intent=new Intent(ValidationActivity.this,ModifyActivity.class);
                            intent.putExtra("user",user);
                            startActivity(intent);
                        }
                    });
                }
                else
                {
                    if(!message.equals("Connection Error")) {
                        ValidationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                findViewById(R.id.valLoadingPannel).setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        ValidationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                findViewById(R.id.valLoadingPannel).setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "연결 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void modifyResult(boolean result) { }
            @Override
            public void findStation(boolean result, final VOStation station_info) { }
            @Override
            public void dataReqResult(String result, List<VOSensorData> data) { }
            @Override
            public void dataReqResultOutdoor(boolean result, VOOutdoor data) { }
        };

        communicationUtil = new CommunicationUtil(callbackInstance);

        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw = pwValText.getText().toString();
                if(!pw.equals(""))  {
//                    valLodingPannel.setVisibility(VISIBLE);
                    communicationUtil.signIn(id,pw);
                }   else    {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ValidationActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("비밀번호를 입력해 주십시오.");
                    alert.show();
                }
            }
        });
    }
}
