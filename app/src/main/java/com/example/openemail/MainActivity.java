package com.example.openemail;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView email;

    /**
     * По данному коду определяем, отправилось письмо или нет
     */
    private static final int LOG_PERMISSION_CODE = 4000;

    private String fileLOGName;

    private File fileLOG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);

        email.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {

                //Определяем текущее время, для того что б вставить в имя файла
                Date dt = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_HH_mm_ss");

                //Присваиваем имя файла, нужно быть аккуратным, из за некоторых символов, файл может быть не создаться
                fileLOGName = "report_log_" + simpleDateFormat.format(dt) + ".txt";
                //Создаем файл
                fileLOG = new File(getExternalFilesDir("txt"), fileLOGName);

                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    FileOutputStream flogs = new FileOutputStream(fileLOG);
                    flogs.write((
                            "\n" +
                            "\n" +
                            "-----------------------------------------------\n" +
                            "Time: " + Calendar.getInstance().getTime() + "\n" +
                            "OS: " + Build.VERSION.RELEASE + "\n" +
                            "SDK_VERSION: " + Build.VERSION.SDK_INT + "\n" +
                            "Model: " + Build.MODEL + "\n" +
                            "Версия ТНС ЭНЕРГО: " + pInfo.versionName + "\n" + "\n" +
                            "\n" +
                            "Информация об ошибке\n" +
                            "-----------------------------------------------\n" +
                            "Arrays.toString(exception.getStackTrace())").getBytes());
                    flogs.flush();
                    flogs.close();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] emails = new String[]{"zhek90@gmail.com"};

                Uri logUri = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".fileprovider", fileLOG);

                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_EMAIL, emails);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Тема");
                //intent.putExtra(Intent.EXTRA_TEXT, bodyText);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_STREAM, logUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivityForResult(Intent.createChooser(intent, "Chose app"), LOG_PERMISSION_CODE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOG_PERMISSION_CODE) {
            //Место для удаления файла
            deleteFile(fileLOGName);
            if (fileLOG.delete()) {
                Toast.makeText(MainActivity.this, "Файл удалён", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Файл НЕ удалён", Toast.LENGTH_SHORT).show();
            }
        }
    }
}