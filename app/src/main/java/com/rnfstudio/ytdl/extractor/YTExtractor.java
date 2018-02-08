package com.rnfstudio.ytdl.extractor;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by roger_huang on 2018/2/8.
 */

public class YTExtractor implements UrlExtractor {

    private static final String TAG = "YTExtractor";
    private static final String YT_CONFIG_REGEX = "ytplayer\\.config = \\{.+?\\};";

    @Override
    public List<String> extract(String vidUrl) {
        List<String> results = new ArrayList<>();

        try {
            // download YouTube web page
            Document doc = Jsoup.connect(vidUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .followRedirects(true).get();
            Log.d(TAG, "html length: " + doc.html().length());

            // parse player config json string
            String configJson = getFirstPlayerConfig(doc.toString());
            Log.d(TAG, "configJson length: " + configJson.length());

            // parse encoded parameters
            JSONObject config = new JSONObject(configJson);
            String fmtListStr = config.getJSONObject("args").getString("url_encoded_fmt_stream_map");

            // parse & decode url
            String[] fmtList = fmtListStr.split(",");
            for (String fmt : fmtList) {
                Map<String, String> argMap = parseEncodedStreamMap(fmtListStr);
                String url = URLDecoder.decode(argMap.get("url"), "UTF-8");
                String signature = argMap.get("signature");
                Log.d(TAG, "url" + url);
                Log.d(TAG, "signature" + signature);
                results.add(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Fail to get or parse KeepVid page: " + e.toString());
        }
        return results;
    }

    private String getFirstPlayerConfig(String html) {
        Log.d(TAG, "Pattern is: " + YT_CONFIG_REGEX);
        Pattern p = Pattern.compile(YT_CONFIG_REGEX);
        Matcher m = p.matcher(html);

        while(m.find()){
            String match = m.group();
            String configJsonStr = match.replace("ytplayer.config =", "")
                    .replace(";", "");
            return configJsonStr;
        }
        return "";
    }

    private Map<String, String> parseEncodedStreamMap(String fmt) {
        Map<String, String> result = new HashMap();

        String[] parts = fmt.split("&");
        for (String part : parts) {
            String[] pairs = part.split("=");
            if (pairs.length < 2) continue;
            result.put(pairs[0], pairs[1]);
//            if ("url".equals(key)) {
//                String decodedUrl = URLDecoder.decode(value, "UTF-8");
//                Log.d(TAG, "value: " + decodedUrl);
//
//                results.add(decodedUrl);
//            }
        }
        return result;
    }
}
