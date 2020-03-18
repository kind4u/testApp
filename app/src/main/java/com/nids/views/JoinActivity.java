package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android .text.Editable;
import android .text.TextWatcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import org.json.JSONArray;

public class JoinActivity extends AppCompatActivity {

    final String siteName = "http://nids-spring-psdg.run.goorm.io/";
    private WebView webView;
    private Handler handler;

    private EditText idText_;
    private EditText pwText_;
    private EditText pwConfirm_;
    private EditText nameText_;
    private  EditText zipCodeText_;
    private EditText adText_;
    private EditText addtText_;
    RadioGroup genderGroup;
    TextView edit_tmX;
    TextView edit_tmY;

    String id;
    String pw;
    String pwCon
    String name;
    String zip_code;
    String addr;
    String addr_detail;
    int gender;
    String tmX;
    String tmY;

    Button btn_signup;
    Button btn_duplicate;
    Button btn_get_addr;

    CommunicationUtil c_util_join;
    JoinCallBackInterface joincallbackInstance;

    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idText_ = (EditText) findViewById(R.id.idText);
        pwText_ = (EditText) findViewById(R.id.pwText);
        pwConfirm_ = (EditText) findViewById(R.id.pwConfirm);
        nameText_ = (EditText)findViewById(R.id.nameText);
        zipCodeText_ = (EditText)findViewById(R.id.zipCodeText);
        adText_ = (EditText)findViewById(R.id.adText);
        addtText_ = (EditText)findViewById(R.id.addtText);
        genderGroup = findViewById(R.id.genderGroup);
        edit_tmX = findViewById(R.id.edit_tmX);
        edit_tmY = findViewById(R.id.edit_tmY);

        webView = (WebView) findViewById(R.id.addrWebView);

        btn_signup =(Button)findViewById(R.id.signUpButton);
        btn_duplicate = (Button)findViewById(R.id.btn_duplicate);
        btn_get_addr = findViewById(R.id.btn_get_addr);

        handler = new Handler();
        bindView1();
    }

    private void bindView1() {

        joincallbackInstance = new JoinCallBackInterface() {

            @Override
            public void joinResult(String result, boolean insert) {
                if (insert) {
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //findViewById((R.id.joinLoadPannel1)).setVisibility(View.GONE);
                            btn_signup.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(JoinActivity.this, CarActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                    });
                } else {
                    //findViewById((R.id.joinLoadPannel1)).setVisibility(View.GONE);
                    btn_signup.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public  void joinResult(String result, boolean insert, String message){
                if (insert) {
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                            btn_signup.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(JoinActivity.this, CarActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                    });
                } else {
                    // TODO : 예외처리 고려
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
                            btn_signup.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void existResult(String result, boolean exist){
                if(!exist) {
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_duplicate.setEnabled(false);
                            Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else {
                    JoinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_duplicate.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void positionResult(boolean position_result, final String data){ //좌표 값 불러와서 파싱
                if(position_result){
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
                } else{
                    Toast.makeText(getApplicationContext(), "좌표를 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        };
            c_util_join =new CommunicationUtil(joincallbackInstance);

            btn_duplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 아이디 중복 확인
                String id = idText_.getText().toString();

                if (idText_.getText().toString().length() == 0) {
                    Toast.makeText(JoinActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                    idText_.requestFocus();
                    return;
                }else {

                    btn_duplicate.setEnabled(false);
                    c_util_join.checkExist(id);
                }
            }
        });

            btn_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                // TODO : 회원가입 구현

                //아이디 입력 확인
                if (idText_.getText().toString().length() == 0) {
                    Toast.makeText(JoinActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                    idText_.requestFocus();
                    return;
                }
                //비밀번호 입력확인
                if (pwText_.getText().toString().length() == 0) {
                    Toast.makeText(JoinActivity.this, "password를 입력하세요.", Toast.LENGTH_SHORT).show();
                    pwText_.requestFocus();
                    return;
                }
                //비밀번호 확인 입력확인
                if (pwConfirm_.getText().toString().length() == 0) {
                    Toast.makeText(JoinActivity.this, "password 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                    pwConfirm_.requestFocus();
                    return;
                }
                //비밀번호 일치 확인z
                 if (!pwText_.getText().toString().equals(pwConfirm_.getText().toString())) {
                       Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                       pwText_.setText("");
                       pwConfirm_.setText("");
                       pwText_.requestFocus();
                       return;
                 }
                //이름 입력확인
                 if (nameText_.getText().toString().length() == 0) {
                     Toast.makeText(JoinActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                     nameText_.requestFocus();
                     return;
                 }
                 //zipcode 입력확인
                 if (zipCodeText_.getText().toString().length() == 0) {
                     Toast.makeText(JoinActivity.this, "우편번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                     zipCodeText_.requestFocus();
                     return;
                  }
                    //주소 입력확인
                    if (adText_.getText().toString().length() == 0) {
                        Toast.makeText(JoinActivity.this, "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
                        adText_.requestFocus();
                        return;
                    }
                    //주소 detail 입력확인
                    if (addtText_.getText().toString().length() == 0) {
                        Toast.makeText(JoinActivity.this, "주소 상세정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                        addtText_.requestFocus();
                        return;
                    }
                    //성별 입력확인
                    if (genderGroup.getCheckedRadioButtonId() == -1){
                        Toast.makeText(JoinActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                        genderGroup.requestFocus();
                        return;
                    }


                    id = idText_.getText().toString();
                    pw = pwText_.getText().toString();
                    pwCon = pwConfirm_.getText().toString();
                    name = nameText_.getText().toString();
                    zip_code = zipCodeText_.getText().toString();
                    addr =adText_.getText().toString();
                    addr_detail =addtText_.getText().toString();
                    int gender_text = genderGroup.getCheckedRadioButtonId();
                    switch (gender_text){
                        case R.id.btn_male: {gender=0; break;}
                        case R.id.btn_female: {gender=1; break;}
                    }
                    tmX = edit_tmX.getText().toString();
                    tmY = edit_tmY.getText().toString();

                btn_signup.setEnabled(false);
                c_util_join.join(id, pw, name, zip_code, addr,addr_detail,gender, tmX, tmY);

            }
            });

            btn_get_addr.setOnClickListener(new View.OnClickListener() { //우편번호 찾기 버튼 눌렀을 때
                @Override
                public void onClick(View v) {
                    init_webView();
                    webView.setVisibility(View.VISIBLE);
                    adText_.setEnabled(false);
                    zipCodeText_.setEnabled(false);
                }
            });
            adText_.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN)  {
                        init_webView();
                        webView.setVisibility(View.VISIBLE);
                        adText_.setEnabled(false);
                        zipCodeText_.setEnabled(false);
                    }
                    return false;
                }
            });

            zipCodeText_.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {            // 주소 칸을 눌렀을 때
                    if(event.getAction()==MotionEvent.ACTION_DOWN)  {
                        init_webView();
                        webView.setVisibility(View.VISIBLE);
                        adText_.setEnabled(false);
                        zipCodeText_.setEnabled(false);
                    }
                    return false;
                }

            });
    }

    public void init_webView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "testApp");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(siteName + "JusoMobilePopup");
    }

    public class AndroidBridge {
        @JavascriptInterface                // 구름 서버 jusoMobilePopup.jsp의 56번쨰 줄
        public void setAddress(final String zipNo, final String addr, final String addrDetail, final String amdCd, final String rnMgtSn, final String udrtYn, final String buldMnnm, final String buldSlno) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    // setText 설정
                    zipCodeText_.setText(zipNo);
                    zipCodeText_.setEnabled(false);
                    zipCodeText_.setBackgroundColor(getResources().getColor(R.color.disable));
                    adText_.setText(addr);
                    adText_.setEnabled(false);
                    adText_.setBackgroundColor(getResources().getColor(R.color.disable));
                    addtText_.setText(addrDetail);
                    c_util_join.findPosition(amdCd, rnMgtSn, udrtYn, buldMnnm, buldSlno);       // 주소의 x좌표, y좌표를 구하기 위한 함수
                    init_webView();
                    webView.setVisibility(View.GONE);

                }
            });
        }
    }
}
