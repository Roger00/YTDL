package com.rnfstudio.ytdl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.rnfstudio.ytdl.extractor.Meta;

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
        List<Meta> metas = new ArrayList<>();
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
            }
        });

        return builder.create();
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
