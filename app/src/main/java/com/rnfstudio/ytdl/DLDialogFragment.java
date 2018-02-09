package com.rnfstudio.ytdl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rnfstudio.ytdl.extractor.Meta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fishchang on 2018/2/7.
 */

public class DLDialogFragment extends DialogFragment {

    private static final String TAG = "DLDialogFragment";
    private static final String ARG_METAS = "metas";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // read meta list from bundle
        ArrayList<String> metaStrings = getArguments().getStringArrayList(ARG_METAS);
        final List<Meta> metas = new ArrayList<>();
        for (String metaString : metaStrings) metas.add(Meta.createFromJson(metaString));

        // create name list
        List<String> nameList = new ArrayList<>();
        for (Meta meta : metas) nameList.add(meta.quality);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.select_dialog_item,
                nameList.toArray(new String[0]));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick, which: " + which);
                downloadItem(getActivity(), metas.get(which));
            }
        });

        return builder.create();
    }

    /**
     * from:
     * https://stackoverflow.com/questions/525204/android-download-intent
     * https://www.jianshu.com/p/55eae30d133c
     */
    public static void downloadItem(Context context, Meta meta) {
        String filename = String.format("%s - %s.%s", meta.name, meta.quality, meta.format);
        Uri uri = Uri.parse(meta.url);

        File downloadDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/YTDL/");
        downloadDir.mkdirs();

        File videoFile = new File(downloadDir, filename);
        Uri videoUri = Uri.fromFile(videoFile);

        DownloadManager.Request r = new DownloadManager.Request(uri);
        r.setDestinationUri(videoUri);
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            dm.enqueue(r);
            Log.d(TAG, "Save to Uri: " + videoUri.toString());
            String message = context.getString(R.string.download_message, videoFile.getPath());
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public static DLDialogFragment newInstance(List<Meta> metas) {
        DLDialogFragment f = new DLDialogFragment();

        ArrayList<String> metaStrings = new ArrayList<>();
        for (Meta meta : metas) metaStrings.add(Meta.toJsonStr(meta));

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ARG_METAS, metaStrings);
        f.setArguments(bundle);

        return f;
    }
}
