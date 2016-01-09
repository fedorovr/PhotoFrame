package com.example.roman.photoframe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.UUID;

public class UserRegisterActivity extends AppCompatActivity {

    final static String DEVICE_ID = UUID.randomUUID().toString();
    final static String DEVICE_NAME = android.os.Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        String queryURL = "https://oauth.yandex.ru/authorize?response_type=token"
                          + "&client_id=e1e3fc7cce22467a9490955fd8e8e215"
                          + "&device_id=" + DEVICE_ID
                          + "&device_name=" + DEVICE_NAME;

        Uri uriUrl = Uri.parse(queryURL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
