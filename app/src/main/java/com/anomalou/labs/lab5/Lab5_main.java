package com.anomalou.labs.lab5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anomalou.labs.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class Lab5_main extends AppCompatActivity {

    class AsyncDownload extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected void onPreExecute() {
            downloadProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return false;

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                output = new FileOutputStream(downloadsDir + File.separator + params[1] + ".pdf");

                byte data[] = new byte[4096];
                long totalDownloaded = 0;
                int downloaded = 0;
                while((downloaded = input.read(data)) != -1){
                    totalDownloaded += downloaded;
                    if(fileLength > 0)
                        publishProgress((int)(totalDownloaded / fileLength * 100));
                    output.write(data, 0, downloaded);
                }

            }catch (Exception ex) {
                ex.printStackTrace();
            }finally {
                try{
                    if(input != null)
                        input.close();
                    if(output != null)
                        output.close();
                    if(connection != null)
                        connection.disconnect();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            downloadProgress.setProgress(values[0], true);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            downloadProgress.setVisibility(View.GONE);
            if(success)
                Toast.makeText(context, "Download compleated!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Something goes wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private Context context;
    private static String downloadsDir;
    private static final String journalsUrl = "https://ntv.ifmo.ru/file/journal/";

    private ProgressBar downloadProgress;
    private EditText idTextBox;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab5_main);

        context = this;
        downloadProgress = (ProgressBar) findViewById(R.id.downloadProgress);
        idTextBox = (EditText) findViewById(R.id.id_textbox);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        File downloads = new File(getExternalFilesDir(null) + "/DownloadedPdfs");
        downloadsDir = downloads.getPath();
        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if(!downloads.exists())
                downloads.mkdirs();
        }

        preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);

        if(!preferences.getBoolean("dont_show", false))
            idTextBox.post(new Runnable() {
                @Override
                public void run() {
                    createPopUp();
                }
            });
    }

    private boolean isConnected(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null)
            return info.isConnected();
        return false;
    }

    private File getJournal(){
        String journalId = idTextBox.getText().toString();
        String path = downloadsDir + File.separator + journalId + ".pdf";
        return new File(path);
    }

    private void createPopUp(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.tipwindow, null);

        PopupWindow popup = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        popupView.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });
        popupView.findViewById(R.id.dont_show).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("dont_show", ((CheckBox)view).isChecked());
                editor.apply();
            }
        });
        popup.showAtLocation(idTextBox, Gravity.CENTER, 0, 0);
    }

    public void onDownload(View view){
        if(isConnected()){
            String journalId = idTextBox.getText().toString();
            AsyncDownload asyncDownload = new AsyncDownload();
            asyncDownload.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, journalsUrl + journalId + ".pdf", journalId);
        }
    }

    public void onView(View view){
        File journal = getJournal();
        if(!(journal.exists())){
            Toast.makeText(this, "Journal not exist!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent viewPdf = new Intent(Intent.ACTION_VIEW);
        viewPdf.setDataAndType(
                FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", journal),
                "application/pdf");
        viewPdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(viewPdf.resolveActivity(this.getPackageManager()) != null)
            startActivity(viewPdf);
        else
            Toast.makeText(this, "Install PDF reader!", Toast.LENGTH_SHORT).show();
    }

    public void onDelete(View view){
        File journal = getJournal();
        if(journal.exists()) {
            Toast.makeText(this, "Journal deleted!", Toast.LENGTH_SHORT).show();
            journal.delete();
        }else
            Toast.makeText(this, "Journal not exist!", Toast.LENGTH_SHORT).show();
    }

    public void onRestoreTip(View view){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}