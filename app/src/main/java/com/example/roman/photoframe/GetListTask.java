package com.example.roman.photoframe;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.yandex.disk.client.Credentials;
import com.yandex.disk.client.TransportClient;

/**
 * Created by roman on 10.01.2016.
 */

public class GetListTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {

        Credentials credentials = new Credentials("", token);

        TransportClient transportClient = TransportClient.getInstance(this, credentials);
        transportClient.getList(currentDir, new CustomListParsingHandler());
    }
}
