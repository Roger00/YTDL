package com.rnfstudio.ytdl;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rnfstudio.ytdl.extractor.ExtractUtils;
import com.rnfstudio.ytdl.extractor.KeepVidExtractor;
import com.rnfstudio.ytdl.extractor.Meta;
import com.rnfstudio.ytdl.extractor.YTExtractor;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String action = getIntent().getAction();
        String type = getIntent().getType();

        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            Log.d(TAG, "start app from share intent");

            String shareUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            asyncDownloadUrl(shareUrl);
        } else {
            // TODO: maybe show default download page or prompt some hints
            Log.d(TAG, "start app from launcher");

            asyncDownloadUrl("https://www.youtube.com/watch?v=EUHcNeg_e9g");
        }
    }

    private void asyncDownloadUrl(String url) {
        // remember url to download (for permission callback)
        mUrl = url;

        // check download permission
        ArrayList<String> permissions = new ArrayList<>(
                Arrays.asList(Permission.PERMISSIONS_DOWNLOAD));
        if (Permission.checkAndRequest(this, permissions,
                Permission.REQUEST_CODE_DOWNLOAD)) {
            new DLTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mUrl);
        }
    }

    private void showSelector(List<Meta> metas) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = DLDialogFragment.newInstance(metas);
        newFragment.show(ft, "dialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Permission.REQUEST_CODE_DOWNLOAD: {
                ArrayList<String> perms = new ArrayList<>(
                        Arrays.asList(Permission.PERMISSIONS_DOWNLOAD));
                if (Permission.check(this, perms)) {
                    new DLTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mUrl);
                } else {
                    Toast.makeText(this, R.string.error_no_permission,
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    class DLTask extends AsyncTask<String, Integer, List<Meta>> {

        @Override
        protected List<Meta> doInBackground(String... urls) {
            // only support single url
            String vidUrl = urls[0];

            List<String> downloadUrls = new YTExtractor().extract(vidUrl);
            List<Meta> metas = new KeepVidExtractor().extract(vidUrl);

            // match download urls by itag
            Map<String, String> iTagMapYT = makeITagMap(downloadUrls);
            for (Meta meta : metas) {
                String itagValue = getITagValue(meta.url);
                if (iTagMapYT.containsKey(itagValue)) {
                    meta.url = iTagMapYT.get(itagValue);
                } else {
                    continue;
                }
            }
            return metas;
        }

        @Override
        protected void onPostExecute(List<Meta> metas) {
            for (Meta meta : metas) {
                Log.d(TAG, meta.toString());
            }

            if (metas.size() > 0) showSelector(metas);
        }

        private Map<String, String> makeITagMap(List<String> urls) {
            Map<String, String> result = new HashMap<>();
            for (String url : urls) result.put(getITagValue(url), url);
            return result;
        }

        private String getITagValue(String url) {
            Uri uri = Uri.parse(url);
            return uri.getQueryParameter("itag");
        }
    }
}
