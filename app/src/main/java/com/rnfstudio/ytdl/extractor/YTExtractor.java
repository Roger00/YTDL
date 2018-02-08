package com.rnfstudio.ytdl.extractor;

import android.util.Log;

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
    private static final String FMT_STREAM_MAP_REGEX = "\"url_encoded_fmt_stream_map\":\"[^\"]*\"";
//    private static final String FMT_STREAM_MAP_REGEX = "\"url_encoded_fmt_stream_map([^\"]*)\"";
    private static final String TEST_INPUT = "\"url_encoded_fmt_stream_map\":\"quality=medium\u0026itag=43\u0026url\", \"123\", \"sdfd\"";

    @Override
    public List<String> extract(String vidUrl) {
        List<String> results = new ArrayList<>();

        try {
            // download YouTube web page
            Document doc = Jsoup.connect(vidUrl).get();
            List<String> streamMaps = getEncodedStreamMaps(doc.toString());

            for (String streamMap : streamMaps) {
                Log.d(TAG, "found map: " + streamMap.substring(0, 50));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Fail to get or parse KeepVid page: " + e.toString());
        }
        return results;
    }

    private List<String> getEncodedStreamMaps(String html) {
        Pattern p = Pattern.compile(FMT_STREAM_MAP_REGEX);
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
