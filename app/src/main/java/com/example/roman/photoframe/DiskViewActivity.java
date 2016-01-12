package com.example.roman.photoframe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.disk.client.Credentials;
import com.yandex.disk.client.DownloadListener;
import com.yandex.disk.client.ListItem;
import com.yandex.disk.client.ListParsingHandler;
import com.yandex.disk.client.TransportClient;
import com.yandex.disk.client.exceptions.CancelledDownloadException;
import com.yandex.disk.client.exceptions.CancelledPropfindException;
import com.yandex.disk.client.exceptions.DownloadNoSpaceAvailableException;
import com.yandex.disk.client.exceptions.FileModifiedException;
import com.yandex.disk.client.exceptions.FileNotModifiedException;
import com.yandex.disk.client.exceptions.PreconditionFailedException;
import com.yandex.disk.client.exceptions.RangeNotSatisfiableException;
import com.yandex.disk.client.exceptions.RemoteFileNotFoundException;
import com.yandex.disk.client.exceptions.ServerWebdavException;
import com.yandex.disk.client.exceptions.UnknownServerWebdavException;
import com.yandex.disk.client.exceptions.WebdavClientInitException;
import com.yandex.disk.client.exceptions.WebdavFileNotFoundException;
import com.yandex.disk.client.exceptions.WebdavForbiddenException;
import com.yandex.disk.client.exceptions.WebdavInvalidUserException;
import com.yandex.disk.client.exceptions.WebdavNotAuthorizedException;
import com.yandex.disk.client.exceptions.WebdavUserNotInitialized;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class DiskViewActivity extends AppCompatActivity {

    private String currentDir = "/";
    private final static String TAG = "DiskViewActivity";
    public final static String PICTURE_NAME = "Picture";
    private ArrayAdapter<ListItem> diskItemsAdapter;
    private List<ListItem> diskItems = new LinkedList<>();
    private ListItem LastTappedItem;
    private byte[] currentPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_view);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        final Credentials credentials = new Credentials("", token);

        diskItemsAdapter = new DiskItemArrayAdapter(this, 0, diskItems);
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

        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LastTappedItem = diskItems.get(position);
                Toast.makeText(context, LastTappedItem.getDisplayName(), Toast.LENGTH_LONG).show();
                try {
                    DownloadPictureTask downloadPictureTask = new DownloadPictureTask();
                    downloadPictureTask.execute(new Container(
                            TransportClient.getInstance(context, credentials), LastTappedItem, context));
                } catch (WebdavClientInitException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class Container {
        public TransportClient transportClient;
        public ListItem tappedItem;
        public Context context;

        public Container(TransportClient transportClient, ListItem tappedItem, Context context) {
            this.transportClient = transportClient;
            this.tappedItem = tappedItem;
            this.context = context;
        }
    }

    class DownloadPictureTask extends AsyncTask<Container, Void, byte[]> {
        private Container container;

        @Override
        protected byte[] doInBackground(Container... params) {
            Log.e(TAG, "Download pic");
            container = params[0];
            TransportClient transportClient = container.transportClient;
            try {
                return transportClient.download(container.tappedItem.getFullPath());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Log.e(TAG, "Downloaded OK");
            if (bytes != null) {
                Log.e(TAG, "Downloaded OK, SHOWING " + bytes.length);
                Intent intent = new Intent(container.context, SlideshowActivity.class);
                intent.putExtra(PICTURE_NAME, bytes);
                startActivity(intent);
            }
        }
    }

    class GetListTask extends AsyncTask<TransportClient, Void, List<ListItem>> {
        @Override
        protected List<ListItem> doInBackground(TransportClient... params) {
            final List<ListItem> currentItems = new LinkedList<>();

            class CustomListParsingHandler extends ListParsingHandler {
                @Override
                public boolean handleItem(ListItem item) {
                    currentItems.add(item);
                    diskItems.add(item);
                    Log.e(TAG, item.toString());
                    return false;
                }
            }

            Log.e(TAG, "backgroundtask");
            TransportClient transportClient = params[0];
            diskItems.clear();
            try {
                transportClient.getList(currentDir, new CustomListParsingHandler());
            } catch (Exception e) {
                Log.e(TAG, "backgound exception" + e.toString());
            }
            return currentItems;
        }

        @Override
        protected void onPostExecute(List<ListItem> listItems) {
            Log.e(TAG, "post_exec");
            for (ListItem item : listItems) {
                diskItemsAdapter.add(item);
            }
        }
    }

    class DiskItemArrayAdapter extends ArrayAdapter<ListItem> {

        private Context context;
        private List<ListItem> objects;

        public DiskItemArrayAdapter(Context context, int resource, List<ListItem> objects) {
            super(context, resource, objects);

            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItem item = objects.get(position);
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            // TODO: View Holder
            View view = inflater.inflate(R.layout.disk_item, null);

            TextView tv = (TextView) view.findViewById(R.id.diskItemText);
            tv.setText(item.getDisplayName());

            return view;
        }
    }
}
