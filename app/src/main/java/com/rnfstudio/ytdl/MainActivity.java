package com.rnfstudio.ytdl;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.rnfstudio.ytdl.extractor.KeepVidExtractor;
import com.rnfstudio.ytdl.extractor.Meta;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new DLTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    class DLTask extends AsyncTask<String, Integer, List<Meta>> {


        @Override
        protected List<Meta> doInBackground(String... strings) {
            String vidUrl = "https://www.youtube.com/watch?v=6OTp1qp4zvc";
            return new KeepVidExtractor().extract(vidUrl);
        }

        @Override
        protected void onPostExecute(List<Meta> metas) {

            for (Meta meta : metas) {
                Log.d(TAG, meta.toString());
            }
        }
    }
}
