package com.anomalou.labs.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.anomalou.labs.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Lab3_control extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Database db;
    private int db_version;

    private EditText fioText;
    private EditText nameText;
    private EditText lnameText;
    private EditText patronymicText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3_control);
        db = null;
        db_version = 1;
        Spinner versions = (Spinner) findViewById(R.id.db_version_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.db_version, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        versions.setAdapter(adapter);
        versions.setOnItemSelectedListener(this);

        fioText = (EditText) findViewById(R.id.editFIO);
        nameText = (EditText) findViewById(R.id.editName);
        lnameText = (EditText) findViewById(R.id.editLName);
        patronymicText = (EditText) findViewById(R.id.editPatronymic);

        fioText.setEnabled(true);
        nameText.setEnabled(false);
        lnameText.setEnabled(false);
        patronymicText.setEnabled(false);
    }

    public void onCreateDB(View view){
        db = new Database(this, "students", null, db_version);
    }

    public void onFillTestData(View view){
        if(db == null){
            Toast.makeText(this, R.string.db_empty, Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase database = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.TIME_COLUMN, LocalTime.now().toString());

        if(db_version == 1)
            values.put(db.FIO_COLUMN, "Бородин А.А.");
        else{
            values.put(db.NAME_COLUMN, "Александр");
            values.put(db.LNAME_COLUMN, "Бородин");
            values.put(db.PATRONYMIC, "Алексеевич");
        }
        database.insert(db.STUDENT_TABLE, null, values);
        if(db_version == 1)
            values.put(db.FIO_COLUMN, "Быков А.С.");
        else{
            values.put(db.NAME_COLUMN, "Андрей");
            values.put(db.LNAME_COLUMN, "Быков");
            values.put(db.PATRONYMIC, "Сергеевич");
        }
        database.insert(db.STUDENT_TABLE, null, values);
        if(db_version == 1)
            values.put(db.FIO_COLUMN, "Голахов С.Ю.");
        else{
            values.put(db.NAME_COLUMN, "Сергей");
            values.put(db.LNAME_COLUMN, "Голахов");
            values.put(db.PATRONYMIC, "Юрьевич");
        }
        database.insert(db.STUDENT_TABLE, null, values);
    }

    public void onClear(View view){
        if(db == null) {
            Toast.makeText(this, R.string.db_empty, Toast.LENGTH_LONG).show();
            return;
        }

        db.getWritableDatabase().delete(db.STUDENT_TABLE, null, null);
    }

    public void onAdd(View view){
        if(db == null) {
            Toast.makeText(this, R.string.db_empty, Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues value = new ContentValues();

        if(db_version == 1){
            String fio = fioText.getText().toString();

            value.put(db.FIO_COLUMN, fio);
        }else{
            String name = nameText.getText().toString();
            String lname = lnameText.getText().toString();
            String patronymic = patronymicText.getText().toString();

            value.put(db.NAME_COLUMN, name);
            value.put(db.LNAME_COLUMN, lname);
            value.put(db.PATRONYMIC, patronymic);
        }

        value.put(db.TIME_COLUMN, LocalTime.now().toString());

        db.getWritableDatabase().insert(db.STUDENT_TABLE, null, value);
    }

    public void onRename(View view){
        if(db == null) {
            Toast.makeText(this, R.string.db_empty, Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase database = db.getWritableDatabase();

        ContentValues value = new ContentValues();

        String date = LocalTime.now().toString();
        value.put(db.TIME_COLUMN, date);

        String id = ((EditText) findViewById(R.id.editID)).getText().toString();

        if(db_version == 1){
            String fio = fioText.getText().toString();

            value.put(db.FIO_COLUMN, fio);
        }else{
            String name = nameText.getText().toString();
            String lname = lnameText.getText().toString();
            String patronymic = patronymicText.getText().toString();

            value.put(db.NAME_COLUMN, name);
            value.put(db.LNAME_COLUMN, lname);
            value.put(db.PATRONYMIC, patronymic);
        }

        database.update(db.STUDENT_TABLE, value, db.KEY + "=?", new String[]{id});
    }

    public void onDatabase(View view){
        if(db == null) {
            Toast.makeText(this, R.string.db_empty, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, Lab3_database.class);

        Cursor cursor;
        ArrayList<Bundle> bundles = new ArrayList<>();

        if(db_version == 1)
            cursor = db.getReadableDatabase().query(
                    db.STUDENT_TABLE, new String[]{db.KEY, db.FIO_COLUMN, db.TIME_COLUMN},
                    null, null, null, null, db.KEY);
        else
            cursor = db.getReadableDatabase().query(
                    db.STUDENT_TABLE, new String[]{db.KEY, db.NAME_COLUMN, db.LNAME_COLUMN, db.PATRONYMIC, db.TIME_COLUMN},
                    null, null, null, null, db.KEY);

        cursor.moveToFirst();



        for(int i = 0; i < cursor.getCount(); i++){
            Bundle student = new Bundle();
            int id = cursor.getInt(0);
            String date;

            if(db_version == 1){
                String fio = cursor.getString(1);
                date = cursor.getString(2);
                student.putString("fio", fio);
            }else{
                String name = cursor.getString(1);
                String lname = cursor.getString(2);
                String patronymic = cursor.getString(3);
                date = cursor.getString(4);
                student.putString("name", name);
                student.putString("lname", lname);
                student.putString("patronymic", patronymic);
            }

            student.putInt("id", id);
            student.putString("date", date);
            bundles.add(student);
            cursor.moveToNext();
        }

        intent.putExtra("students", bundles);
        intent.putExtra("db_version", db_version);

        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        db_version = i + 1;

        if(db_version == 1){
            fioText.setEnabled(true);
            nameText.setEnabled(false);
            lnameText.setEnabled(false);
            patronymicText.setEnabled(false);
        }else{
            fioText.setEnabled(false);
            nameText.setEnabled(true);
            lnameText.setEnabled(true);
            patronymicText.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}