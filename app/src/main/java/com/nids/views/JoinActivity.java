package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.network.CommunicationUtil;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {

	JoinCallBackInterface joinCallBackInstance;
	CommunicationUtil c_util_join;

	final String siteName = "http://nids-spring-psdg.run.goorm.io/";

    String id;
    String pw;
    String pwCon;
    String name;
    int gender;
    String platform;
    String bd;
    String email;
	String hp;

	EditText edit_id;
	EditText edit_pw;
	EditText edit_pw_con;
	EditText edit_name;
	RadioGroup genderGroup;
	EditText edit_email;
	EditText edit_bd;
	EditText edit_hp;

	Button btn_signup;
	Button btn_duplicate;
	Button btn_get_addr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		edit_id = findViewById(R.id.edit_id);
		edit_pw = findViewById(R.id.edit_pw);
		edit_pw_con = findViewById(R.id.edit_pw_con);
		edit_name = findViewById(R.id.edit_name);
		genderGroup = findViewById(R.id.genderGroup);
		edit_bd = findViewById(R.id.edit_bd);
		edit_email = findViewById(R.id.edit_email);
		edit_hp = findViewById(R.id.edit_hp);

		btn_signup = findViewById(R.id.btn_signup);
		btn_duplicate = findViewById(R.id.btn_duplicate);

		bindView();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void bindView() {

		joinCallBackInstance = new JoinCallBackInterface() {
			@Override
			public void getUserResult(boolean result, String message, VOUser userinfo){  }

			@Override
			public void carResult(boolean insert, String result, String message) { }

			@Override
			public void deleteCarResult(boolean delete, String result, String message){ }

			@Override
			public void editCarResult(boolean edit, String result, String message){ }

			@Override
			public void checkCarResult(String result, boolean exist){ }

			@Override
			public void signUpResult(boolean insert, String result, String message) {
				if (insert) {
					JoinActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
							btn_signup.setEnabled(true);
							Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(JoinActivity.this, CarActivity.class);
							intent.putExtra("id",id);
							intent.putExtra("page", "main");
							startActivity(intent);
						}
					});
				} else {
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
			public void existResult(String result, boolean exist) {
				if (!exist) {
					JoinActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							btn_duplicate.setEnabled(false);
							Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();

						}
					});
				} else {
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
			public void positionResult(boolean position_result, final String data) { // 좌표 값을 불러와서 파싱하는 과정
//				if (position_result) {
//					JoinActivity.this.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							JsonParser jsonParser = new JsonParser();
//							JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
//							JsonArray jsonArray = new JsonArray();
//							jsonArray.add(jsonObject.get("results"));
//							JsonObject resultObj = (JsonObject) jsonArray.get(0);
//							JsonArray jusoArray = new JsonArray();
//							jusoArray.add(resultObj.get("juso"));
//							JsonObject jusoObj = (JsonObject) (((JsonArray) jusoArray.get(0)).get(0));
//
//
//							findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
//						}
//					});
//				} else {
//					Toast.makeText(getApplicationContext(), "좌표를 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
//					findViewById(R.id.joinLoadingPannel).setVisibility(View.GONE);
//				}
			}
		};

		c_util_join = new CommunicationUtil(joinCallBackInstance);

//		btn_get_addr.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) { // 우편번호 찾기 버튼을 눌렀을 때
//				init_webView();
//				webView.setVisibility(View.VISIBLE);
//				edit_addr.setEnabled(false);
//				edit_zip_code.setEnabled(false);
//			}
//		});
//		edit_addr.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) { // 우편번호 칸을 눌렀을 때
//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					init_webView();
//					webView.setVisibility(View.VISIBLE);
//					edit_addr.setEnabled(false);
//					edit_zip_code.setEnabled(false);
//				}
//				return false;
//			}
//		});
//
//		edit_zip_code.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) { // 주소 칸을 눌렀을 때
//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					init_webView();
//					webView.setVisibility(View.VISIBLE);
//					edit_addr.setEnabled(false);
//					edit_zip_code.setEnabled(false);
//				}
//				return false;
//			}
//		});

		btn_duplicate.setOnClickListener(new View.OnClickListener() { // 아이디 중복 체크 버튼
			@Override
			public void onClick(View v) {
				id = edit_id.getText().toString();

				if (id.length() == 0) {
					Toast.makeText(JoinActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_id.requestFocus();
				} else {
					btn_duplicate.setEnabled(false);
					c_util_join.checkExist(id);
				}
			}
		});

		btn_signup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 아이디 입력 확인
				if (edit_id.getText().toString().length() == 0) {
					Toast.makeText(JoinActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_id.requestFocus();
					return;
				}
				// 비밀번호 입력확인
				if (edit_pw.getText().toString().length() == 0) {
					Toast.makeText(JoinActivity.this, "password를 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_pw.requestFocus();
					return;
				}
				// 비밀번호 확인 입력확인
				if (edit_pw_con.getText().toString().length() == 0) {
					Toast.makeText(JoinActivity.this, "password 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_pw_con.requestFocus();
					return;
				}
				// 비밀번호 일치 확인
				if (!edit_pw.getText().toString().equals(edit_pw_con.getText().toString())) {
					Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
					edit_pw.setText("");
					edit_pw_con.setText("");
					edit_pw.requestFocus();
					return;
				}
				// 이름 입력확인
				if (edit_name.getText().toString().length() == 0) {
					Toast.makeText(JoinActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_name.requestFocus();
					return;
				}
				// 휴대폰 번호 입력확인
				if (edit_hp.getText().toString().length() == 0) {
					Toast.makeText(JoinActivity.this, "휴대폰 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_hp.requestFocus();
					return;
				}else{
					// 휴대폰 번호 형식 체크
					if(!Pattern.matches("^01(?:[0-1]|[6-9])-(\\d{3,4})-(\\d{4})$", edit_hp.getText().toString())){
						Toast.makeText(JoinActivity.this, "휴대폰 번호 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
					}
				}
				// 이메일 입력확인
				if (edit_email.getText().toString().length() == 0) {
					Toast.makeText(JoinActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_email.requestFocus();
					return;
				}else{
					// 이메일 형식 체크
					if(!Pattern.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.+[a-zA-Z0-9]{2,6}+$",edit_email.getText().toString())){
						Toast.makeText(JoinActivity.this,"이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
						edit_email.requestFocus();
						return;
					}
				}
				// 생일 입력확인
				if (edit_bd.getText().toString().length() == 0) {
					Toast.makeText(JoinActivity.this, "생일을 입력하세요.", Toast.LENGTH_SHORT).show();
					edit_bd.requestFocus();
					return;
				}else{
					// 생일 형식 체크
					if(!Pattern.matches("^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])$",edit_bd.getText().toString())){
						Toast.makeText(JoinActivity.this,"생일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
						edit_bd.requestFocus();
						return;
					}
				}

				// 성별 입력확인
				if (genderGroup.getCheckedRadioButtonId() == -1) {
					Toast.makeText(JoinActivity.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
					genderGroup.requestFocus();
					return;
				}

				id = edit_id.getText().toString();
				pw = edit_pw.getText().toString();
				pwCon = edit_pw_con.getText().toString();
				name = edit_name.getText().toString();
				int gender_text = genderGroup.getCheckedRadioButtonId();
				switch (gender_text) {
				case R.id.btn_male: {
					gender = 0;
					break;
				}
				case R.id.btn_female: {
					gender = 1;
					break;
				}
				}
				platform ="DEFAULT";
				bd = edit_bd.getText().toString();
				email = edit_email.getText().toString();
				hp = edit_hp.getText().toString();

//				btn_get_addr.setEnabled(false);
				btn_signup.setEnabled(false);
				findViewById(R.id.joinLoadingPannel).setVisibility(View.VISIBLE);

				Log.d("id", id);
				Log.d("pw", pw);
				Log.d("name", name);
				Log.d("gender", Integer.toString(gender));
				Log.d("platform", platform);
				Log.d("bd", bd);
				Log.d("email", email);
				Log.d("hp", hp);

				c_util_join.signUp(id, pw, name, gender, platform, bd, email, hp); // communication util의
																									// signup 함수 호출

			}
		});
	}

//	public void init_webView() { // 웹뷰 초기화 과정
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//		webView.getSettings().setDomStorageEnabled(true);
//		webView.addJavascriptInterface(new AndroidBridge(), "testApp");
//		webView.setWebChromeClient(new WebChromeClient());
//		webView.loadUrl(siteName + "JusoMobilePopup");
//	}
//
//	public class AndroidBridge { // jsp 웹뷰와 통신하기 위한 인터페이스 구성
//		@JavascriptInterface // 구름 서버 jusoMobilePopup.jsp의 56번쨰 줄
//		public void setAddress(final String zipNo, final String addr1, final String addr2, final String addrDetail,
//				final String amdCd, final String rnMgtSn, final String udrtYn, final String buldMnnm,
//				final String buldSlno) {
//			webView.post(new Runnable() {
//				@Override
//				public void run() {
//
//					findViewById(R.id.joinLoadingPannel).setVisibility(View.VISIBLE);
//					// setText 설정
//					edit_zip_code.setText(zipNo);
//					edit_zip_code.setEnabled(false);
//					edit_zip_code.setBackgroundColor(getResources().getColor(R.color.disable));
//					edit_addr.setText(addr1 + " " + addr2);
//					edit_addr.setEnabled(false);
//					edit_addr.setBackgroundColor(getResources().getColor(R.color.disable));
//					edit_addr_detail.setText(addrDetail);
//					c_util_join.findPosition(amdCd, rnMgtSn, udrtYn, buldMnnm, buldSlno); // 주소의 x좌표, y좌표를 구하기 위한 함수
//					init_webView();
//					webView.setVisibility(View.GONE);
//
//				}
//			});
//		}
//	}
}