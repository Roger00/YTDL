package com.rnfstudio.ytdl;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.rnfstudio.ytdl.extractor.Meta;

import java.util.List;

/**
 * Created by fishchang on 2018/2/9.
 */

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Meta> mData;

    public GridAdapter(Context context, List<Meta> metas) {
        mContext = context;
        mData = metas;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(85, 85));
            textView.setPadding(8, 8, 8, 8);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(mData.get(position).quality);
        Log.d("xxx", "Position: " + position);
        Log.d("xxx", "quality: " + mData.get(position).quality);
        textView.setBackgroundColor(mContext.getColor(R.color.colorAccent));

        return textView;
    }
}
