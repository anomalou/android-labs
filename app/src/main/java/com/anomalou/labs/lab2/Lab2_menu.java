package com.anomalou.labs.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anomalou.labs.*;

public class Lab2_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_menu);
    }

    public void onActivity1(View view){
        Intent intent = new Intent(this, Lab2_Activity1.class);
        startActivity(intent);
    }

    public void onActivity2(View view){
        Intent intent = new Intent(this, Lab2_Activity2.class);
        startActivity(intent);
    }

    public void onActivity3(View view){
        Intent intent = new Intent(this, Lab2_Activity3.class);
        startActivity(intent);
    }

    public void onExit(View view){
        this.finish();
    }
}