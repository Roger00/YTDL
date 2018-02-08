package com.rnfstudio.ytdl.extractor;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by roger_huang on 2018/2/8.
 */

public class YTExtractor implements UrlExtractor {

    private static final String TAG = "YTExtractor";
    private static final String YT_CONFIG_REGEX = "ytplayer\\.config = \\{.+?\\};";
//    private static final String YT_CONFIG_REGEX = "ytplayer\\.config = {.+?};";
    private static final String FMT_STREAM_MAP_REGEX = "\"url_encoded_fmt_stream_map\":\"[^\"]*\"";
//    private static final String FMT_STREAM_MAP_REGEX = "\"url_encoded_fmt_stream_map([^\"]*)\"";
    private static final String TEST_INPUT = "\"url_encoded_fmt_stream_map\":\"quality=medium\u0026itag=43\u0026url\", \"123\", \"sdfd\"";

    @Override
    public List<String> extract(String vidUrl) {
        List<String> results = new ArrayList<>();

        try {
            // download YouTube web page
            Document doc = Jsoup.connect(vidUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .followRedirects(true).get();

            Log.d(TAG, "html length: " + doc.html().length());
            Log.d(TAG, "outerHtml length: " + doc.outerHtml().length());
            Log.d(TAG, "toString length: " + doc.toString().length());
            List<String> streamMaps = getEncodedStreamMaps(doc.toString());

            for (String streamMap : streamMaps) {
                Log.d(TAG, "found map: " + streamMap.substring(0, 50));
                Log.d(TAG, "found map length: " + streamMap.length());
                Log.d(TAG, "found map: " + streamMap);
//                new JSONObject(streamMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Fail to get or parse KeepVid page: " + e.toString());
        }
        return results;
    }

    private List<String> getEncodedStreamMaps(String html) {
        Log.d(TAG, "Pattern is: " + YT_CONFIG_REGEX);
        Pattern p = Pattern.compile(YT_CONFIG_REGEX);
        Matcher m = p.matcher(html);

        List<String> matches = new ArrayList<>();
        while(m.find()){
            String match = m.group();
            matches.add(match.replace("url_encoded_fmt_stream_map=", "")
                    .replace(";", ""));
        }
        return matches;
    }
}
