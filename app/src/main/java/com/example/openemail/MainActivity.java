package com.example.openemail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File root = Environment.getExternalStorageDirectory();
                File file = new File(root, "exception-report.txt");

                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    FileOutputStream flogs = new FileOutputStream(file);
                    flogs.write((
                            "Time: " + Calendar.getInstance().getTime() + "\n" +
                                    "OS: " + Build.VERSION.RELEASE + "\n" +
                                    "SDK_VERSION: " + Build.VERSION.SDK_INT + "\n" +
                                    "Model: " + Build.MODEL + "\n" +
                                    "Версия приложения: " + pInfo.versionName + "\n" + "\n" +
                                    "Arrays.toString(exception.getStackTrace())" //Текст ошибки
                    ).getBytes());
                    flogs.close();
                } catch (FileNotFoundException | PackageManager.NameNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (!file.exists() || !file.canRead()){
                    Toast.makeText(MainActivity.this, "Произошла ошибка прикрепления файла", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] emails = new String[]{"zhek90@gmail.com"};

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, emails);
                intent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivity(Intent.createChooser(intent, "Chose apps"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}