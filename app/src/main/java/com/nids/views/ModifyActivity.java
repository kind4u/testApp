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

    String genderDesc;
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
                gender = voUser.getGender();
                if(gender == 0){genderDesc="남성";}    else    { genderDesc="여성"; }
                infoArrayList.add(new InfoItem("성별",genderDesc));
                break;
            case "GOOGLE":
            case "NAVER":
            case "KAKAO":
                id = intent.getExtras().get("id").toString();
                infoArrayList.add(new InfoItem("ID", id));
                infoArrayList.add(new InfoItem("비밀번호",""));
                infoArrayList.add(new InfoItem("이름",""));
                infoArrayList.add(new InfoItem("성별",""));
                break;

        }
        ModifyAdapter mAdapter = new ModifyAdapter(infoArrayList);
        recyclerView.setAdapter(mAdapter);
        submit_button = findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyActivity.this,MainActivity.class);
                intent.putExtra("id",id);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
