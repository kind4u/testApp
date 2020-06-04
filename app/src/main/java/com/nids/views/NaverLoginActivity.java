//package com.nids.views;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.nhn.android.naverlogin.OAuthLogin;
//import com.nhn.android.naverlogin.OAuthLoginHandler;
//import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
//import com.nids.kind4u.testapp.R;
//import com.nids.util.interfaces.JoinCallBackInterface;
//import com.nids.util.network.CommunicationUtil;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserFactory;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class NaverLoginActivity<header> extends AppCompatActivity {
//    JoinCallBackInterface joinCallBackInstance;
//    CommunicationUtil c_util_join;
//
//    String f_array[] = new String[9];
//
//    private static final String TAG = "OAuthSampleActivity";
//    public static OAuthLogin mOAuthLoginInstance;
//    public Map<String,String> mUserInfoMap;
//
//    /**
//     * client 정보를 넣어준다.
//     */
//    private static String OAUTH_CLIENT_ID = "qlplaikRvERQQnhE4J_h";
//    private static String OAUTH_CLIENT_SECRET = "zHZRmGocla";
//    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인 테스트";
//
//    private static Context mContext;
//
//
//    private OAuthLoginButton mOAuthLoginButton;
//
//    static private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
//        @Override
//        public void run(boolean success) {
//            if (success) {
//                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
//                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
//                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
//                String tokenType = mOAuthLoginInstance.getTokenType(mContext);
//
//            } else {
//                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
//                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
//                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    };
//
//    private class RequestApiTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
//            String at = mOAuthLoginInstance.getAccessToken(mContext);
//            mUserInfoMap = requestNaverUserInfo(mOAuthLoginInstance.requestApi(mContext, at, url));
//            return null;
//        }
//
//        protected void onPostExecute(Void content) {
//            if (mUserInfoMap.get("email") == null) {
//                Toast.makeText(mContext, "로그인 실패하였습니다.  잠시후 다시 시도해 주세요!!", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d(TAG, String.valueOf(mUserInfoMap));
//            }
//
//        }
//    }
//
//    public Map<String,String> requestNaverUserInfo(String data) { // xml 파싱
//
//
//        try {
//            XmlPullParserFactory parserCreator = XmlPullParserFactory
//                    .newInstance();
//            XmlPullParser parser = parserCreator.newPullParser();
//            InputStream input = new ByteArrayInputStream(
//                    data.getBytes("UTF-8"));
//            parser.setInput(input, "UTF-8");
//
//            int parserEvent = parser.getEventType();
//            String tag;
//            boolean inText = false;
//            boolean lastMatTag = false;
//
//            int colIdx = 0;
//
//            while (parserEvent != XmlPullParser.END_DOCUMENT) {
//                switch (parserEvent) {
//                    case XmlPullParser.START_TAG:
//                        tag = parser.getName();
//                        if (tag.compareTo("xml") == 0) {
//                            inText = false;
//                        } else if (tag.compareTo("data") == 0) {
//                            inText = false;
//                        } else if (tag.compareTo("result") == 0) {
//                            inText = false;
//                        } else if (tag.compareTo("resultcode") == 0) {
//                            inText = false;
//                        } else if (tag.compareTo("message") == 0) {
//                            inText = false;
//                        } else if (tag.compareTo("response") == 0) {
//                            inText = false;
//                        } else {
//                            inText = true;
//
//                        }
//                        break;
//                    case XmlPullParser.TEXT:
//                        tag = parser.getName();
//                        if (inText) {
//                            if (parser.getText() == null) {
//                                f_array[colIdx] = "";
//                            } else {
//                                f_array[colIdx] = parser.getText().trim();
//                            }
//
//                            colIdx++;
//                        }
//                        inText = false;
//                        break;
//                    case XmlPullParser.END_TAG:
//                        tag = parser.getName();
//                        inText = false;
//                        break;
//
//                }
//
//                parserEvent = parser.next();
//            }
//        } catch (Exception e) {
//            Log.e("dd", "Error in network call", e);
//        }
//        Map<String, String> resultMap = new HashMap<>();
//        resultMap.put("email", f_array[0]);
//        resultMap.put("nickname", f_array[1]);
//        resultMap.put("enc_id", f_array[2]);
//        resultMap.put("profile_image", f_array[3]);
//        resultMap.put("age", f_array[4]);
//        resultMap.put("gender", f_array[5]);
//        resultMap.put("id", f_array[6]);
//        resultMap.put("name", f_array[7]);
//        resultMap.put("birthday", f_array[8]);
//        return resultMap;
//
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        //login ->naver login
//
//        mContext = this;
//
//        mOAuthLoginInstance = OAuthLogin.getInstance();
//        mOAuthLoginInstance.showDevelopersLog(true);
//        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
//
//        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.btn_naver);
//        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
//        //initData();
//        //initView();
//
//        this.setTitle("OAuthLoginSample Ver." + OAuthLogin.getVersion());
//
//        joinCallBackInstance = new JoinCallBackInterface() {
//
//            @Override
//            public void carResult(boolean insert, String result, String message) { }
//
//            @Override
//            public void deleteCarResult(boolean delete, String result, String message){ }
//
//            @Override
//            public void editCarResult(boolean edit, String result, String message){ }
//
//            @Override
//            public void checkCarResult(String result, boolean exist){ }
//
//            @Override
//            public void signUpResult(boolean insert, String result, String message) { }
//
//            @Override
//            public void existResult(String result, boolean exist) { }
//
//            @Override
//            public void naverSignUpResult(boolean insert, String result, String message) {
//                //TODO: 네이버 연동 로그인
//            }
//
//            @Override
//            public void positionResult(boolean position_result, final String data) { }
//        };
//
//
////        c_util_join = new CommunicationUtil(joinCallBackInstance);
////        c_util_join.naverSignUp(f_array[6], f_array[7], f_array[4]);
//    }
//
//
//}
