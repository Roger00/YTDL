package com.rnfstudio.ytdl.extractor;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fishchang on 2018/2/6.
 */

public class Meta {
    public static final String TAG = "Meta";

    public String name;
    public String vidUrl;
    public String quality;
    public String format;
    public String filesize;
    public String url;
    public String thumbUrl;

    public class JSON {
        public static final String NAME = "name";
        public static final String VID_URL = "vid_url";
        public static final String QUALITY = "quality";
        public static final String FORMAT = "format";
        public static final String FILE_SIZE = "file_size";
        public static final String URL = "url";
        public static final String THUMB_URL = "thumb_url";
    }

    public Meta(String vidUrl, String name) {
        this.vidUrl = vidUrl;
        this.name = name;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Meta(" + name + ")\n");
        sb.append("Original video url(" + vidUrl + ")\n");
        sb.append("Quality(" + quality + ")\n");
        sb.append("Format(" + format + ")\n");
        sb.append("File size(" + filesize + ")\n");
        sb.append("DL url(" + url + ")\n");
        sb.append("Thumb url(" + thumbUrl + ")\n");
        return sb.toString();
    }

    public static String toJsonStr(Meta meta) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(JSON.NAME, meta.name);
            obj.put(JSON.VID_URL, meta.vidUrl);
            obj.put(JSON.QUALITY, meta.quality);
            obj.put(JSON.FORMAT, meta.format);
            obj.put(JSON.FILE_SIZE, meta.filesize);
            obj.put(JSON.URL, meta.url);
            obj.put(JSON.THUMB_URL, meta.thumbUrl);

        } catch (JSONException e) {
            Log.d(TAG, "Fail to save meta to json: " + e.toString());
        }
        return obj.toString();
    }

    public static Meta createFromJson(String json) {
        JSONObject obj;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            Log.d(TAG, "Fail to restore json: " + e.toString());
            return null;
        }

        Meta meta = new Meta(obj.optString(JSON.VID_URL, ""),
                obj.optString(JSON.NAME, ""));
        meta.quality = obj.optString(JSON.QUALITY, "");
        meta.format = obj.optString(JSON.FORMAT, "");
        meta.filesize = obj.optString(JSON.FILE_SIZE, "");
        meta.url = obj.optString(JSON.URL, "");
        meta.thumbUrl = obj.optString(JSON.THUMB_URL, "");
        return meta;
    }
}
