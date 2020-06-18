package com.nids.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.ModifyAdapter;
import com.nids.util.InfoItem;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.util.ArrayList;
import java.util.List;

public class ModifyActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    VOUser voUser;
    int gender;
    Button submit_button;

    String id;
    private String platform;

    NetworkCallBackInterface networkCallBackInterface = new NetworkCallBackInterface() {
        @Override
        public void signInResult(boolean result, String message, VOUser userinfo) {  }
        @Override
        public void modifyResult(boolean result) {  }
        @Override
        public void modifyUserResult(boolean result){
            if (result) {
                ModifyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ModifyActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                                intent.putExtra("id",id);
                                intent.putExtra("platform",platform);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        });
                        alert.setMessage("회원정보 변경이 정상적으로 이루어졌습니다.");
                        alert.show();
                    }
                });
            } else {
                ModifyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alert2 = new AlertDialog.Builder(ModifyActivity.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);



        Intent intent = getIntent();
        platform = intent.getExtras().get("platform").toString();

        recyclerView = findViewById(R.id.modify_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final ArrayList<InfoItem> infoArrayList = new ArrayList<>();
        switch (platform){
            case "DEFAULT" :
                voUser = (VOUser) intent.getExtras().get("user");
                id =voUser.getId();
                infoArrayList.add(new InfoItem("ID",id));
                infoArrayList.add(new InfoItem("비밀번호",voUser.getPw()));
                infoArrayList.add(new InfoItem("이름",voUser.getName()));
                infoArrayList.add(new InfoItem("성별",voUser.getGender()==0?"남성":"여성"));
                infoArrayList.add(new InfoItem("휴대전화",voUser.getPhone()));
                infoArrayList.add(new InfoItem("생일",voUser.getBd()));
                infoArrayList.add(new InfoItem("연령대",voUser.getAge()));
                infoArrayList.add(new InfoItem("이메일",voUser.getEmail()));
                break;
            case "GOOGLE":
            case "NAVER":
            case "KAKAO":
                voUser = (VOUser) intent.getExtras().get("user");
                id = intent.getExtras().get("id").toString();
                infoArrayList.add(new InfoItem("ID", id));
                infoArrayList.add(new InfoItem("비밀번호",""));
                infoArrayList.add(new InfoItem("이름",voUser.getName()));
                infoArrayList.add(new InfoItem("성별",voUser.getGender()==0?"남성":voUser.getGender()==1?"여성":"미설정"));
                infoArrayList.add(new InfoItem("휴대전화",voUser.getPhone()));
                infoArrayList.add(new InfoItem("생일",voUser.getBd()));
                infoArrayList.add(new InfoItem("연령대",voUser.getAge()));
                infoArrayList.add(new InfoItem("이메일",voUser.getEmail()));
                break;

        }
        ModifyAdapter mAdapter = new ModifyAdapter(this, infoArrayList);
        recyclerView.setAdapter(mAdapter);
        submit_button = findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int genderTemp = infoArrayList.get(3).getDesc().equals("남성")?0:infoArrayList.get(3).getDesc().equals("여성")?1:9;
                c_util.modifyUser(id,
                        infoArrayList.get(2).getDesc(),
                        genderTemp,
                        infoArrayList.get(4).getDesc(),
                        infoArrayList.get(5).getDesc(),
                        infoArrayList.get(6).getDesc(),
                        infoArrayList.get(7).getDesc()
                );
            }
        });

    }
}
