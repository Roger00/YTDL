package com.rnfstudio.ytdl;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

            showSelector(metas);
        }
    }
}
