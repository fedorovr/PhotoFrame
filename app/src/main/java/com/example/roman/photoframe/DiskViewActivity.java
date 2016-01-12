package com.example.roman.photoframe;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.util.LinkedList;
import java.util.List;

public class DiskViewActivity extends AppCompatActivity {

    private String currentDir = "/";
    private final static String TAG = "DiskViewActivity";
    private ArrayAdapter<String> diskItemsAdapter;
    private List<ListItem> diskItems = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_view);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        Credentials credentials = new Credentials("", token);

        diskItemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(diskItemsAdapter);

        try {
            Log.e(TAG, "Transport client");
            GetListTask task = new GetListTask();
            task.execute(TransportClient.getInstance(this, credentials));
            Log.e(TAG, "Transport client second");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    class GetListTask extends AsyncTask<TransportClient, Void, List<ListItem>> {
        @Override
        protected List<ListItem> doInBackground(TransportClient... params) {
            Log.e(TAG, "backgroundtask");
            TransportClient client = params[0];
            diskItems.clear();
            try {
                client.getList(currentDir, new CustomListParsingHandler());
            } catch (Exception e) {
                Log.e(TAG, "backgound exception" + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ListItem> listItems) {
            Log.e(TAG, "post_exec");
            for (ListItem item : diskItems) {
                diskItemsAdapter.add(item.getDisplayName());
            }
        }
    }

    class CustomListParsingHandler extends ListParsingHandler {
        @Override
        public boolean handleItem(ListItem item) {
            diskItems.add(item);
            Log.e(TAG, item.toString());
            return false;
        }
    }
}
