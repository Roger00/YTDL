package com.rnfstudio.ytdl.extractor;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by roger_huang on 2018/2/8.
 */

public class YTExtractor implements UrlExtractor {

    private static final String TAG = "YTExtractor";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0";
    private static final String YT_CONFIG_REGEX = "ytplayer\\.config = \\{.+?\\};";
    private static final String YT_CONFIG_PREFIX = "ytplayer.config =";
    private static final String YT_CONFIG_SUFFIX = ";";
    private static final String KEY_ARGS = "args";
    private static final String KEY_FMT_STREAM_MAP = "url_encoded_fmt_stream_map";
    private static final String KEY_URL = "url";
    private static final String SPLITTER_FMT_LIST = ",";

    @Override
    public List<String> extract(String vidUrl) {
        List<String> results = new ArrayList<>();

        try {
            // download YouTube web page
            Document doc = Jsoup.connect(vidUrl)
                    .userAgent(USER_AGENT)
                    .followRedirects(true).get();
            Log.d(TAG, "html length: " + doc.html().length());

            // parse player config json string
            String configJson = getFirstPlayerConfig(doc.toString());
            Log.d(TAG, "configJson length: " + configJson.length());

            // parse encoded parameters
            JSONObject config = new JSONObject(configJson);
            String fmtListStr = config.getJSONObject(KEY_ARGS).getString(KEY_FMT_STREAM_MAP);

            // parse & decode url
            String[] fmtList = fmtListStr.split(SPLITTER_FMT_LIST);
            for (String fmt : fmtList) {
                Map<String, String> argMap = ExtractUtils.parseQueryStrings(fmt);
                String url = URLDecoder.decode(argMap.get(KEY_URL), "UTF-8");
                Log.d(TAG, "url: " + url);
                results.add(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Fail to get or parse YT page: " + e.toString());
        }
        return results;
    }

    private String getFirstPlayerConfig(String html) {
        Pattern p = Pattern.compile(YT_CONFIG_REGEX);
        Matcher m = p.matcher(html);

        while(m.find()){
            String match = m.group();
            String configJsonStr = match.replace(YT_CONFIG_PREFIX, "")
                    .replace(YT_CONFIG_SUFFIX, "");
            return configJsonStr;
        }
        return "";
    }
}
