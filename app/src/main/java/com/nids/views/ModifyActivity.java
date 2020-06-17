package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.ModifyAdapter;
import com.nids.util.InfoItem;

import java.util.ArrayList;

public class ModifyActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    VOUser voUser;
    int gender;
    Button submit_button;

    String id;
    private String platform;

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

        ArrayList<InfoItem> infoArrayList = new ArrayList<>();
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
                Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("platform",platform);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
