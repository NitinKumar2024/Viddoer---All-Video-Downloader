package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class SqlPythonActivity extends AppCompatActivity {

    private Python py;
    private PyObject pyFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_python);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        py = Python.getInstance();
        pyFunction = py.getModule("sql_python").get("get_mysql_connection");
        sql_python_service("jf");


    }

    private void sql_python_service(final String videoLink) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                PyObject result = pyFunction.call();

                final String hostingLink = result.toString();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        if (!hostingLink.isEmpty()) {

                            TextView textView = findViewById(R.id.textView);
                            textView.setText(hostingLink);


                            //   videoView.setVideoURI(Uri.parse(hostingLink));
                            //      videoView.setMediaController(new MediaController(YoutubeVideo.this));
                            //   videoView.start();
                        } else {
                            Toast.makeText(SqlPythonActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}