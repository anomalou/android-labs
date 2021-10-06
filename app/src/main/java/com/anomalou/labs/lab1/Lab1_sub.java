package com.anomalou.labs.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.anomalou.labs.*;

public class Lab1_sub extends AppCompatActivity {

    TextView param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1_sub);

        param = (TextView) findViewById(R.id.param);
        String value = (String) getIntent().getSerializableExtra("PARAM");
        param.setText(value);
    }
}