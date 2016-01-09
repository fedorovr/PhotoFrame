package com.example.roman.photoframe;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yandex.disk.client.Credentials;
import com.yandex.disk.client.ListItem;
import com.yandex.disk.client.ListParsingHandler;
import com.yandex.disk.client.TransportClient;
import com.yandex.disk.client.exceptions.CancelledPropfindException;
import com.yandex.disk.client.exceptions.PreconditionFailedException;
import com.yandex.disk.client.exceptions.ServerWebdavException;
import com.yandex.disk.client.exceptions.UnknownServerWebdavException;
import com.yandex.disk.client.exceptions.WebdavClientInitException;
import com.yandex.disk.client.exceptions.WebdavFileNotFoundException;
import com.yandex.disk.client.exceptions.WebdavForbiddenException;
import com.yandex.disk.client.exceptions.WebdavInvalidUserException;
import com.yandex.disk.client.exceptions.WebdavNotAuthorizedException;
import com.yandex.disk.client.exceptions.WebdavUserNotInitialized;

import java.io.IOException;
import java.util.List;

public class DiskViewActivity extends AppCompatActivity {

    private String currentDir = "/";
    private final static String TAG = "DiskViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_view);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        String deviceID = sharedPreferences.getString("deviceID", null);

        Credentials credentials = new Credentials("", token);

        //List<ListItem> items = new ListLoader(this, credentials, currentDir).loadInBackground();
//
        final ArrayAdapter<ListItem> diskItemsAdapter =
                new ArrayAdapter<ListItem>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(diskItemsAdapter);

        class CustomListParsingHandler extends ListParsingHandler {
            @Override
            public boolean handleItem(ListItem item) {
                diskItemsAdapter.add(item);
                Log.e(TAG, item.toString());
                return false;
            }
        }

        TransportClient transportClient = null;
        try {
            Log.e(TAG, "Transport client");
            transportClient = TransportClient.getInstance(this, credentials);
            //transportClient.getList(currentDir, new CustomListParsingHandler());
            Log.e(TAG, "Transport client second");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
}
