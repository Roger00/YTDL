package com.rnfstudio.ytdl;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rnfstudio.ytdl.extractor.KeepVidExtractor;
import com.rnfstudio.ytdl.extractor.Meta;
import com.rnfstudio.ytdl.extractor.YTExtractor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final boolean DEBUG = false;
    private String mUrl;
    private ImageView mThumb;
    private GridView mGridview;
    private ProgressBar mProgress;
    private TextView mSummary;
    private ExtractTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mThumb = findViewById(R.id.thumbnail);
        mGridview = findViewById(R.id.gridview);
        mProgress = findViewById(R.id.progressBar);
        mSummary = findViewById(R.id.summary);
        final EditText edit = findViewById(R.id.edit);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edit.getText().toString();
                edit.setText("");
                if (!TextUtils.isEmpty(url)) {
                    asyncExtractUrl(url);
                }
            }
        });

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Meta meta = (Meta) mGridview.getAdapter().getItem(position);
                DLDialogFragment.downloadItem(MainActivity.this, meta);
            }
        });

        String action = getIntent().getAction();
        String type = getIntent().getType();

        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            Log.d(TAG, "start app from share intent");

            String shareUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            asyncExtractUrl(shareUrl);
        } else {
            // TODO: maybe show default download page or prompt some hints
            Log.d(TAG, "start app from launcher");
        }
    }

    private void asyncExtractUrl(final String url) {
        Log.d(TAG, "asyncExtractUrl from: " + url);

        if (mTask != null) {
            // cancel existing jobs when needed
            Log.d(TAG, "Cancel existing task: " + mTask);
            mTask.cancel(true);
            mTask = null;

            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Start new download: " + url);
                    asyncExtractUrl(url);
                }
            });

            // early return
            Log.d(TAG, "Skip download this time");
            return;
        }

        // remember url to download (for permission callback)
        mUrl = url;

        // check download permission
        ArrayList<String> permissions = new ArrayList<>(
                Arrays.asList(Permission.PERMISSIONS_DOWNLOAD));
        if (Permission.checkAndRequest(this, permissions,
                Permission.REQUEST_CODE_DOWNLOAD)) {
            mTask = new ExtractTask();
            mTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mUrl);
        } else {
            Log.d(TAG, "Permission required, return null");
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
                    Log.d(TAG, "Add new download job in onRequestPermissionsResult, url: " + mUrl);
                    asyncExtractUrl(mUrl);
                } else {
                    Toast.makeText(this, R.string.error_no_permission,
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private void setAdapter(List<Meta> metas) {
        mGridview.setAdapter(new GridAdapter(this, metas));
    }

    class ExtractTask extends AsyncTask<String, Integer, List<Meta>> {
        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Meta> doInBackground(String... urls) {
            // only support single url
            String vidUrl = urls[0];

            List<String> downloadUrls = new ArrayList<>();
            if (!isCancelled()) {
                publishProgress(0);
                downloadUrls = new YTExtractor().extract(vidUrl);
            }

            List<Meta> metas = new ArrayList<>();
            if (!isCancelled()) {
                publishProgress(50);
                metas = new KeepVidExtractor().extract(vidUrl);
            }

            publishProgress(100);

            // match download urls by itag
            Map<String, String> iTagMapYT = makeITagMap(downloadUrls);
            for (Meta meta : metas) {
                String itagValue = getITagValue(meta.url);
                if (iTagMapYT.containsKey(itagValue)) {
                    meta.url = iTagMapYT.get(itagValue);
                } else {
                    // TODO: remove not downloadable links
                    continue;
                }
            }
            return metas;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            int resId = R.string.message_wait;
            if (progress < 50) {
                resId = R.string.message_analyze_yt;
            } else if (progress < 100){
                resId = R.string.message_analyze_kv;
            }
            mSummary.setText(getString(resId));
        }

        @Override
        protected void onCancelled(List<Meta> metas) {
            Log.d(TAG, "onCancelled");
            return;
        }

        @Override
        protected void onPostExecute(List<Meta> metas) {
            if (DEBUG) {
                for (Meta meta : metas) {
                    Log.d(TAG, meta.toString());
                }
            }

            mProgress.setVisibility(View.INVISIBLE);
            if (metas.size() > 0) {
                mSummary.setText(metas.get(0).name);
                setAdapter(metas);
                new DownloadImageTask(mThumb).executeOnExecutor(SERIAL_EXECUTOR, metas.get(0).thumbUrl);
            }
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

    /**
     * from:
     * https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
