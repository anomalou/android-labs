package com.anomalou.labs.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anomalou.labs.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;

public class Lab4_music extends AppCompatActivity {

    public class MusicTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                while (true){
                    InputStream is;
                    byte[] data;

                    URL url = new URL(strings[0]);
                    String postRequest = strings[1];

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    if(connection != null){
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);

                        connection.setRequestProperty("Content-Length", Integer.toString(postRequest.getBytes().length));

                        OutputStream os = connection.getOutputStream();
                        data = postRequest.getBytes("UTF-8");
                        os.write(data);
                        data = null;

                        connection.connect();

                        int responseCode = connection.getResponseCode();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        if (responseCode == 200) {
                            is = connection.getInputStream();

                            byte[] buffer = new byte[8192];

                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                baos.write(buffer, 0, bytesRead);
                            }

                            data = baos.toByteArray();
                            String answer = new String(data, "UTF-8");
                            publishProgress(answer);
                        }
                    }

                    Thread.sleep(1000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... strings) {
            String jsonString = strings[0];

            try {
                JSONObject json = new JSONObject(jsonString);

                String info = json.getString("info");

                String[] track = info.split(" - ", 2);

                appendMusicTrack(track[0], track[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    Database database;
    MusicTask musicTask;

    private boolean isConnected(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null)
            if(info.isConnected())
                return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_music);

        database = new Database(this, "music", null, 1);
        updateTable();

        if(isConnected()){
            startNetworking();
        }else{
            Toast.makeText(this, "No internet connection. Autonomic mode.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startNetworking(){
        musicTask = new MusicTask();
        musicTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://media.itmo.ru/api_get_current_song.php", "login=4707login&password=4707pass");
    }

    private void appendMusicTrack(String author, String title){
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(database.TABLE, new String[]{database.AUTHOR, database.TITLE},
                null, null, null, null, null);

        cursor.moveToLast();

        String lastAuthor = "";
        String lastTitle = "";

        if(cursor.getCount() != 0) {
            lastAuthor = cursor.getString(0);
            lastTitle = cursor.getString(1);
        }

        if((!lastAuthor.equals(author) && !lastTitle.equals(title)) || cursor.getCount() == 0){
            db = database.getWritableDatabase();

            ContentValues newTrack = new ContentValues();
            newTrack.put(database.AUTHOR, author);
            newTrack.put(database.TITLE, title);
            newTrack.put(database.DATE, LocalTime.now().toString());

            db.insert(database.TABLE, null, newTrack);

            updateTable();
        }
    }

    private void updateTable(){
        TableLayout table = (TableLayout) findViewById(R.id.music_list);

        Cursor cursor = database.getReadableDatabase().query(database.TABLE, new String[]{database.AUTHOR, database.TITLE, database.DATE},
                null, null, null, null, null);

        cursor.moveToFirst();

        table.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for(int i = 0; i < cursor.getCount(); i++){
            TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);

            TextView tv = tr.findViewById(R.id.music_author);
            tv.setText(cursor.getString(0));

            tv = tr.findViewById(R.id.music_title);
            tv.setText(cursor.getString(1));

            tv = tr.findViewById(R.id.music_date);
            tv.setText(cursor.getString(2));

            table.addView(tr);

            cursor.moveToNext();
        }
    }
}