package com.example.roman.photoframe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CustomURLSchemeHandlerActivity extends AppCompatActivity {

    private static final String TAG = "CustomURLScheme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_urlscheme_handler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Log.e(TAG, "Handling custom url");

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String[] parameters = uri.getFragment().split("\\&");
            if (parameters[0].charAt(0) == 'a') {    // "access_token"
                String[] parts = parameters[0].split("\\=");
                String userToken = parts[1];

                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", userToken);
                editor.putString("deviceID", UserRegisterActivity.DEVICE_ID);
                editor.commit();

                Intent viewDisk = new Intent(this, DiskViewActivity.class);
                startActivity(viewDisk);
            }
            else {
                // error handling here
                Toast.makeText(this, "Some error", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
