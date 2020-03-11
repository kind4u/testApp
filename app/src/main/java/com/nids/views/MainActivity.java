package com.nids.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nids.kind4u.testapp.R;

public class MainActivity extends AppCompatActivity {

    InsideFragment insideFragment;
    OutsideFragment outsideFragment;
    Button btn_analysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insideFragment = new InsideFragment();
        outsideFragment = new OutsideFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container1,insideFragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.container2,outsideFragment);
        btn_analysis = findViewById(R.id.AnalysisButton);
        btn_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"아직 안 만듬",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
