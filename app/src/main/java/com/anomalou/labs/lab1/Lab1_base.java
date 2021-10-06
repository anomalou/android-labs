package com.anomalou.labs.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.anomalou.labs.*;

public class Lab1_base extends AppCompatActivity {

    EditText paramEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1_base);

        paramEditor = (EditText) findViewById(R.id.param_editor);
    }

    public void onSendClick(View view){
        Intent intent = new Intent(this, Lab1_sub.class);
        String param = paramEditor.getText().toString();
        intent.putExtra("PARAM", param);
        startActivity(intent);
    }
}