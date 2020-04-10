package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        Intent intent = getIntent();
        voUser = (VOUser) intent.getExtras().get("user");

        recyclerView = findViewById(R.id.modify_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<InfoItem> infoArrayList = new ArrayList<>();
        infoArrayList.add(new InfoItem("ID",voUser.getId()));
        infoArrayList.add(new InfoItem("비밀번호",voUser.getPw()));
        infoArrayList.add(new InfoItem("이름",voUser.getName()));
        gender = voUser.getGender();
        if(gender == 0){genderDesc="남성";}    else    { genderDesc="여성"; }
        infoArrayList.add(new InfoItem("성별",genderDesc));


        ModifyAdapter mAdapter = new ModifyAdapter(infoArrayList);
        recyclerView.setAdapter(mAdapter);
    }
}
