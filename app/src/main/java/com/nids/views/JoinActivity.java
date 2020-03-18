package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.text.ParseException;

public class JoinActivity extends AppCompatActivity {

    JoinCallBackInterface joinCallBackInstance;
    CommunicationUtil c_util_join;

    final String siteName = "http://nids-spring-psdg.run.goorm.io/";
    private WebView webView;
    private Handler handler;

    String id;
    String pw;
    String name;
    String zip_code;
    String addr;
    String addr_detail;
    int gender;
    String tmX;
    String tmY;

    EditText edit_id;
    EditText edit_pw;
    EditText edit_name;
    EditText edit_zip_code;
    EditText edit_addr;
    EditText edit_addr_detail;
    RadioGroup genderGroup;
    TextView edit_tmX;
    TextView edit_tmY;

    Button btn_signup;
    Button btn_duplicate;
    Button btn_get_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        edit_id = findViewById(R.id.edit_id);
        edit_pw = findViewById(R.id.edit_pw);
        edit_name = findViewById(R.id.edit_name);
        edit_zip_code = findViewById(R.id.edit_zip_code);
        edit_addr = findViewById(R.id.edit_addr);
        edit_addr_detail = findViewById(R.id.edit_addr_detail);
        genderGroup = findViewById(R.id.genderGroup);
        edit_tmX = findViewById(R.id.edit_tmX);
        edit_tmY = findViewById(R.id.edit_tmY);

        webView = (WebView) findViewById(R.id.addrWebView);

        btn_signup = findViewById(R.id.signUpButton);
        btn_get_addr = findViewById(R.id.btn_get_addr);
        btn_duplicate = findViewById(R.id.btn_duplicate);

        handler = new Handler();
        bindview2();
    }
    private void bindview2()    {

        joinCallBackInstance = new JoinCallBackInterface() {

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

            @Override
            public void positionResult(boolean position_result, final String data) {    // 좌표 값을 불러와서 파싱하는 과정
                if(position_result) {
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
                            JsonArray jsonArray = new JsonArray();
                            jsonArray.add(jsonObject.get("results"));
                            JsonObject resultObj = (JsonObject) jsonArray.get(0);
                            JsonArray jusoArray =  new JsonArray();
                            jusoArray.add(resultObj.get("juso"));
                            JsonObject jusoObj = (JsonObject)(((JsonArray)jusoArray.get(0)).get(0));
                            String tmX = jusoObj.get("entX").toString().replace("\"","");
                            String tmY = jusoObj.get("entY").toString().replace("\"","");
                            edit_tmX.setText(tmX);
                            edit_tmY.setText(tmY);
                        }
                    });
                }   else    {
                    Toast.makeText(getApplicationContext(), "좌표를 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        };
        c_util_join = new CommunicationUtil(joinCallBackInstance);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //회원가입 버튼 입력
                // TODO : 회원가입 구현
                String id_text = edit_id.getText().toString();
                String pw_text = edit_pw.getText().toString();
                String name_text = edit_name.getText().toString();
                String zip_code_text = edit_zip_code.getText().toString();          // 우편번호
                String addr_text = edit_addr.getText().toString();                  // 주소 내용
                String addr_detail_text = edit_addr_detail.getText().toString();    // 상세 주소 내용
                int gender_text = genderGroup.getCheckedRadioButtonId();            // 성별 구분
                String tmX_text = edit_tmX.getText().toString();                    // Tm 기준 x좌표
                String tmY_text = edit_tmY.getText().toString();                    // Tm 기준 y좌표

                if(id_text.equals("") || pw_text.equals("") || name_text.equals("") || zip_code_text.equals("") ||
                        addr_text.equals("") || addr_detail_text.equals("") || gender_text == -1){
                    Toast.makeText(JoinActivity.this, "모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return;
                    // TODO : 아이디 중복 처리를 하지 않았을 때의 예외처리
                }   else    {
                    id = id_text;
                    pw = pw_text;
                    name = name_text;
                    zip_code = zip_code_text;
                    addr = addr_text;
                    addr_detail = addr_detail_text;
                    switch(gender_text) {
                        case R.id.btn_male: {gender=0; break;}
                        case R.id.btn_female:   {gender=1; break;}
                    }
                    tmX = tmX_text;
                    tmY = tmY_text;
                }

                btn_duplicate.setEnabled(false);
                btn_signup.setEnabled(false);
                findViewById(R.id.joinLoadingPannel).setVisibility(View.VISIBLE);
                Log.d("id",id); Log.d("pw",pw);Log.d("name",name);Log.d("zip_code",zip_code);Log.d("addr",addr);Log.d("detail",addr_detail);
                Log.d("gender",Integer.toString(gender));Log.d("tmX",tmX);Log.d("tmY",tmY);
                c_util_join.signUp(id, pw, name, zip_code, addr,addr_detail,gender, tmX, tmY);      //communication util의 signup 함수 호출 (현재 미가동)

            }
        });

        btn_get_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                   // 우편번호 찾기 버튼을 눌렀을 때
                init_webView();
                webView.setVisibility(View.VISIBLE);
                edit_addr.setEnabled(false);
                edit_zip_code.setEnabled(false);
            }
        });
        edit_addr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {               // 우편번호 칸을 눌렀을 때
                if(event.getAction()==MotionEvent.ACTION_DOWN)  {
                    init_webView();
                    webView.setVisibility(View.VISIBLE);
                    edit_addr.setEnabled(false);
                    edit_zip_code.setEnabled(false);
                }
                return false;
            }
        });

        edit_zip_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {            // 주소 칸을 눌렀을 때
                if(event.getAction()==MotionEvent.ACTION_DOWN)  {
                    init_webView();
                    webView.setVisibility(View.VISIBLE);
                    edit_addr.setEnabled(false);
                    edit_zip_code.setEnabled(false);
                }
                return false;
            }
        });
        btn_duplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 아이디 중복 확인
            }
        });
    }

    public void init_webView()  {           //웹뷰 초기화 과정
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "testApp");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(siteName + "JusoMobilePopup");
    }

    public class AndroidBridge  {           // jsp 웹뷰와 통신하기 위한 인터페이스 구성
        @JavascriptInterface                // 구름 서버 jusoMobilePopup.jsp의 56번쨰 줄
        public void setAddress(final String zipNo, final String addr, final String addrDetail, final String amdCd, final String rnMgtSn, final String udrtYn, final String buldMnnm, final String buldSlno) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    // setText 설정
                    edit_zip_code.setText(zipNo);
                    edit_zip_code.setEnabled(false);
                    edit_zip_code.setBackgroundColor(getResources().getColor(R.color.disable));
                    edit_addr.setText(addr);
                    edit_addr.setEnabled(false);
                    edit_addr.setBackgroundColor(getResources().getColor(R.color.disable));
                    edit_addr_detail.setText(addrDetail);
                    c_util_join.findPosition(amdCd, rnMgtSn, udrtYn, buldMnnm, buldSlno);       // 주소의 x좌표, y좌표를 구하기 위한 함수
                    init_webView();
                    webView.setVisibility(View.GONE);

                }
            });
        }
    }
}
