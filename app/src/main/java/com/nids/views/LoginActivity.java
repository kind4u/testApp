package com.nids.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;
import java.security.MessageDigest;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import java.util.List;

public class LoginActivity extends AppCompatActivity  {

    OAuthLoginButton btn_naver;

    Button btn_signin;
    Button btn_join;

    EditText edit_id;
    EditText edit_pw;

    String id;
    String pw;

    CommunicationUtil c_util;
    NetworkCallBackInterface callbackInstance;

    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton btn_google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        getAppKeyHash();
        bindView();
    }

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
            public void modifyResult(boolean result) {
            }

            @Override
            public void findStation(boolean result, final VOStation station_info) {
            }

            @Override
            public void dataReqResult(String result, List<VOSensorData> data) {

            }

            @Override
            public void dataReqResultOutdoor(boolean result, VOOutdoor data) {
            }
        };

        c_util = new CommunicationUtil(callbackInstance);

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
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        btn_naver = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);

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

    private void signIn()   {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSingInIntent(...);
        if(requestCode == RC_SIGN_IN)   {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)    {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch(ApiException e) {
            Log.w("Google", "signInResult:failed code = " + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)   {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user)    {
        if(user != null)    {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("id",user.getUid());
            startActivity(intent);
            finish();
        }
    }

    private void getAppKeyHash()    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for(Signature signature: info.signatures)   {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(),0));
                Log.e("Hash key",something);
            }
        }   catch (Exception e) {
            Log.e("name not found", e.toString());
        }

        btn_naver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "kind4u";
                String pw = "admin123";
                c_util.signIn(id,pw);
            }
        });
    }
}