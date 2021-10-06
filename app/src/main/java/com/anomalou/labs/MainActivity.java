package com.anomalou.labs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anomalou.labs.lab1.*;
import com.anomalou.labs.lab2.Lab2_menu;
import com.anomalou.labs.lab3.Lab3_control;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLab1(View view){
        Intent intent = new Intent(this, Lab1_base.class);
        startActivity(intent);
    }

    public void onLab2(View view){
        Intent intent = new Intent(this, Lab2_menu.class);
        startActivity(intent);
        this.finish();
    }

    public void onLab3(View view){
        Intent intent = null;
        switch (view.getId()){
            case R.id.lab3_button:
                intent = new Intent(this, Lab3_control.class);
                break;
        }
        startActivity(intent);
        this.finish();
    }
}