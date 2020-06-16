package com.nids.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import org.w3c.dom.Text;

import java.util.List;

public class ModifyPwActivity extends AppCompatActivity {

    TextView test;
    TextView modifyPassword;
    TextView modifyPasswordCon;
    Button modifySubmitButton;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pw);

        modifyPassword = findViewById(R.id.modify_pw);
        modifyPasswordCon = findViewById(R.id.modify_pw_con);
        modifySubmitButton = findViewById(R.id.modify_submit_btn);

        test = findViewById(R.id.testview);

        Intent intent = getIntent();
        test.setText(intent.getExtras().get("id").toString());
        userId = intent.getExtras().get("id").toString();

        modifySubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modifyPassword.getText().toString().length() == 0) {
                    Toast.makeText(ModifyPwActivity.this, "password를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 비밀번호 확인 입력확인
                else if (modifyPasswordCon.getText().toString().length() == 0) {
                    Toast.makeText(ModifyPwActivity.this, "password 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 비밀번호 일치 확인
                else if (!modifyPassword.getText().toString().equals(modifyPasswordCon.getText().toString())) {
                    Toast.makeText(ModifyPwActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    c_util.modifyPassword(userId, modifyPassword.getText().toString());
                }
            }
        });
    }

    NetworkCallBackInterface networkCallBackInterface = new NetworkCallBackInterface() {
        @Override
        public void signInResult(boolean result, String message, VOUser userinfo) {
        }

        @Override
        public void modifyResult(boolean result) {
            if (result) {
                ModifyPwActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ModifyPwActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        alert.setMessage("비밀번호 변경이 정상적으로 이루어졌습니다.");
                        alert.show();
                    }
                });
            } else {
                ModifyPwActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alert2 = new AlertDialog.Builder(ModifyPwActivity.this);
                        alert2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert2.setMessage("네트워크 오류가 발생했습니다. 다시 시도해 주세요.");
                        alert2.show();
                    }
                });
            }
        }

        @Override
        public void findStation(boolean result, VOStation station_info) { }
        @Override
        public void dataReqResult(String result, List<VOSensorData> dataList) { }
        @Override
        public void dataReqResultOutdoor(boolean result, VOOutdoor data) { }
    };

    CommunicationUtil c_util = new CommunicationUtil(networkCallBackInterface);
}
