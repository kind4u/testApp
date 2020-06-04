package com.nids.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;


import java.util.List;

enum Platform    {
    DEFAULT, GOOGLE, NAVER, KAKAO
}

public class LoginActivity extends AppCompatActivity {

    OAuthLoginButton btn_naver;
    String testId;
    String testName;
    String testAge;
    String testBd;
    String testGender;
    String testEmail;
    Button btn_signin;
    Button btn_join;

    EditText edit_id;
    EditText edit_pw;

    String id;
    String pw;

    CommunicationUtil c_util;
    CommunicationUtil c_util_join;
    NetworkCallBackInterface callbackInstance;
    JoinCallBackInterface joinCallBackInstance;
    private SessionCallback sessionCallBack;
  
    private MeV2Response meV2Response = null;
    private FirebaseUser user = null;
    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton btn_google;

    //---------여기부터---------------
    Map<String, String> resultMap = new HashMap<>();
    String f_array[] = new String[9];

    private static final String TAG = "OAuthSampleActivity";
    public Map<String,String> mUserInfoMap;

    /**
     * client 정보를 넣어준다.
     */
    private static String OAUTH_CLIENT_ID = "qlplaikRvERQQnhE4J_h";
    private static String OAUTH_CLIENT_SECRET = "zHZRmGocla";
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인 테스트";

    private static Context mContext;
    private OAuthLoginButton mOAuthLoginButton;
    public static OAuthLogin mOAuthLoginInstance;

    private Platform platform = Platform.DEFAULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

//-----------여기부터---------------
        mContext = this;

        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.btn_naver);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
      //  mOAuthLoginInstance.startOauthLoginActivity(LoginActivity, mOAuthLoginHandler);


       // this.setTitle("OAuthLoginSample Ver." + OAuthLogin.getVersion());
//==========여기까지========-----------

        bindView();
    }

    //-----------여기부터---------------
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void run(boolean success) {
            if (success) {
                //사용자 정보 가져오기
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);

                new RequestApiTask().execute();
                platform = Platform.NAVER;
                c_util_join.checkExist(testId);

            } else {
                //로그인 인증 실패
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

    };

    public class RequestApiTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            mUserInfoMap = requestNaverUserInfo(mOAuthLoginInstance.requestApi(mContext, at, url));
            return null;
        }

        protected void onPostExecute(Void content) {
            if (mUserInfoMap.get("email") == null) {
                Toast.makeText(mContext, "로그인 실패하였습니다.  잠시후 다시 시도해 주세요!!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, String.valueOf(mUserInfoMap));
            }

            //임의변수설정 수정 필요
            testId = mUserInfoMap.get("email");
            testName = mUserInfoMap.get("profile_image");
            testAge = mUserInfoMap.get("nickname");
            testBd = mUserInfoMap.get("age");
            testGender= mUserInfoMap.get("enc_id");
            //testEmail =mUserInfoMap.get("email");
        }

    }

    public Map<String,String> requestNaverUserInfo(String data) { // xml 파싱
        try {
            XmlPullParserFactory parserCreator = XmlPullParserFactory
                    .newInstance();
            XmlPullParser parser = parserCreator.newPullParser();
            InputStream input = new ByteArrayInputStream(
                    data.getBytes("UTF-8"));
            parser.setInput(input, "UTF-8");

            int parserEvent = parser.getEventType();
            String tag;
            boolean inText = false;
            boolean lastMatTag = false;

            int colIdx = 0;

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.compareTo("xml") == 0) {
                            inText = false;
                        } else if (tag.compareTo("data") == 0) {
                            inText = false;
                        } else if (tag.compareTo("result") == 0) {
                            inText = false;
                        } else if (tag.compareTo("resultcode") == 0) {
                            inText = false;
                        } else if (tag.compareTo("message") == 0) {
                            inText = false;
                        } else if (tag.compareTo("response") == 0) {
                            inText = false;
                        } else {
                            inText = true;

                        }
                        break;
                    case XmlPullParser.TEXT:
                        tag = parser.getName();
                        if (inText) {
                            if (parser.getText() == null) {
                                f_array[colIdx] = "";
                            } else {
                                f_array[colIdx] = parser.getText().trim();
                            }
                            colIdx++;
                        }
                        inText = false;
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        inText = false;
                        break;

                }

                parserEvent = parser.next();
            }
        } catch (Exception e) {
            Log.e("dd", "Error in network call", e);
        }
        //Map<String, String> resultMap = new HashMap<>();
        resultMap.put("email",f_array[0]);
        resultMap.put("nickname", f_array[1]);
        resultMap.put("enc_id", f_array[2]);
        resultMap.put("profile_image", f_array[3]);
        resultMap.put("age", f_array[4]);
        resultMap.put("gender", f_array[5]);
        resultMap.put("id", f_array[6]);
        resultMap.put("name", f_array[7]);
        resultMap.put("birthday", f_array[8]);

        return resultMap;

    }
    //-----------여기까지----------

    private void bindView() {
        callbackInstance = new NetworkCallBackInterface() {
            VOUser user;

            @Override
            public void signInResult(boolean result, String message, VOUser userInfo) {
                if (result) {
                    user = userInfo;
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.loginLoadingPannel).setVisibility(View.GONE);
                            btn_signin.setEnabled(true);
                            btn_join.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id", user.getId());
                            startActivity(intent);
                        }
                    });
                } else {
                    if (!message.equals("Connection Error")) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.loginLoadingPannel).setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                btn_signin.setEnabled(true);
                                btn_join.setEnabled(true);
                            }
                        });
                    } else {
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
            public void modifyResult(boolean result) { }

            @Override
            public void findStation(boolean result, final VOStation station_info) { }
            @Override
            public void dataReqResult(String result, List<VOSensorData> data) { }
            @Override
            public void dataReqResultOutdoor(boolean result, VOOutdoor data) { }
        };

        c_util = new CommunicationUtil(callbackInstance);

        //여기부터
        joinCallBackInstance = new JoinCallBackInterface() {

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
                if(insert)  {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                            intent2.putExtra("id",user.getUid());
                            startActivity(intent2);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void naverSignUpResult(boolean insert, String result, String message) { }
                //TODO: 네이버 연동 로그인

            @Override
            public void existResult(String result, boolean exist) {
                if(exist)   {       //db에 등록되어 있음. id 얻어서 MainActivity
                    switch(platform)    {
                        case GOOGLE:
                            Intent intent_google = new Intent(getApplicationContext(), MainActivity.class);
                            intent_google.putExtra("id", user.getUid());
                            startActivity(intent_google);
                            finish();
                            break;
                      case NAVER:
                            Intent intent_naver = new Intent(LoginActivity.this, MainActivity.class);
                            intent_naver.putExtra("id", testId); //네이버 연동 id 받아와서 넣기!
                            startActivity(intent_naver);
                            break;
                        case KAKAO:
                            Intent intent_kakao = new Intent(getApplicationContext(), MainActivity.class);
                            intent_kakao.putExtra("id", meV2Response.getId());
                            startActivity(intent_kakao);
                            finish();
                            break;
                    }
                }
                else    {     //db에 등록되어있지 않아서 db에 회원정보 등록필요함
                    switch (platform)   {
                        case GOOGLE:
                            c_util_join.signUp(user.getUid(), null, null, null, null, null, 9, null, null);
                            break;
                      case NAVER:
                            c_util_join.naverSignUp(testId, testName, testAge);  //네이버 연동 id, name age 받아와서 넣기!
                            break;
                        case KAKAO:
                            c_util_join.signUp(String.valueOf(meV2Response.getId()),null, null, null, null, null, 9, null, null);
                            break;
                    }
                }

            }

            @Override
            public void positionResult(boolean position_result, final String data) { }
        };

        c_util_join = new CommunicationUtil(joinCallBackInstance);
      

        sessionCallBack = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallBack);
        Session.getCurrentSession().checkAndImplicitOpen();


        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_join = (Button) findViewById(R.id.btn_join);
        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_pw = (EditText) findViewById(R.id.edit_pw);
        btn_google = findViewById(R.id.btn_google);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btn_naver = (OAuthLoginButton) findViewById(R.id.btn_naver);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id_text = edit_id.getText().toString();
                String pw_text = edit_pw.getText().toString();
                if (!id_text.equals("")) {
                    id = id_text;
                } else {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pw_text.equals("")) {
                    pw = pw_text;
                } else {
                    Toast.makeText(LoginActivity.this, "패스워드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

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

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSingInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }   else if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data))    {
            super.onActivityResult(requestCode,resultCode,data);
            return;
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w("Google", "signInResult:failed code = " + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            platform = Platform.GOOGLE;
            c_util_join.checkExist(user.getUid());
        }   else    {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("구글 로그인에 실패했습니다. 다시 시도해주세요.");
                    alert.show();
                }
            });
        }
    }

    private class SessionCallback implements ISessionCallback   {

        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();
                    if(result == ApiErrorCode.CLIENT_ERROR_CODE)    {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }   else    {
                        Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다."+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), "세션이 닫혔습니다. 다시 시도해 주세요."+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    platform = Platform.KAKAO;
                    meV2Response = result;
                    c_util_join.checkExist(String.valueOf(meV2Response.getId()));
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요.: "+exception.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}