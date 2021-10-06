package com.anomalou.labs.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.anomalou.labs.*;

import java.util.ArrayList;

public class Lab3_database extends AppCompatActivity {

    ArrayList<Bundle> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3_database);

        students = this.getIntent().getParcelableArrayListExtra("students");
        buildTable();
    }

    private void buildTable(){
        TableLayout table = (TableLayout) findViewById(R.id.db_table);

        for(int i = 0; i < students.size(); i++){
            Bundle student = students.get(i);

            int id = student.getInt("id", -1);

            if(id == -1)
                continue;

            String date = student.getString("date", "error");

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT
            ));

            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1
            );

            TextView idText = new TextView(this);
            idText.setLayoutParams(params);
            idText.setText(String.valueOf(id));
            TextView dateText = new TextView(this);
            dateText.setLayoutParams(params);
            dateText.setText(date);

            TextView fioText = new TextView(this);
            fioText.setLayoutParams(params);
            TextView nameText = new TextView(this);
            nameText.setLayoutParams(params);
            TextView lnameText = new TextView(this);
            lnameText.setLayoutParams(params);
            TextView patronymicText = new TextView(this);
            patronymicText.setLayoutParams(params);

            int db_version = this.getIntent().getIntExtra("db_version", -1);

            if(db_version == -1)
                return;

            table.addView(row);

            row.addView(idText);

            if(db_version == 1){
                String fio = student.getString("fio", "error");
                fioText.setText(fio);

                row.addView(fioText);
            }else{
                String name = student.getString("name", "error");
                String lname = student.getString("lname", "error");
                String patronymic = student.getString("patronymic", "error");

                nameText.setText(name);
                lnameText.setText(lname);
                patronymicText.setText(patronymic);

                row.addView(nameText);
                row.addView(lnameText);
                row.addView(patronymicText);
            }

            row.addView(dateText);
        }
    }
}