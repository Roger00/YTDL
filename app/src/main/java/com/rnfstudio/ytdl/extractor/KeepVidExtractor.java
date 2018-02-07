package com.rnfstudio.ytdl.extractor;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fishchang on 2018/2/6.
 */

public class KeepVidExtractor implements Extractor
{
    private static final String TAG = "KeepVidExtractor";
    private static final String KEEP_VID_BASE_URL = "https://keepvid.com/?url=";

    @Override
    public List<Meta> extract(String vidUrl) {
        List<Meta> results = new ArrayList<>();

        String fullUrl = getVideoFullUrl(vidUrl);
        Document doc = null;
        try {
            doc = Jsoup.connect(fullUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Fail to get KeepVid page: " + e.toString());
        }

        // parse thumbnail url and title
        String thumbnailUrl = doc.select(".result-img").first().attr("abs:src");
        String title = doc.select(".item-3").first().text();

        // parse indivisual downloadable links
        Elements rows = doc.select(".result-table tr");
        for (Element row : rows) {
            Elements dlButtons = row.select(".btn");
            if (dlButtons.size() == 0) {
                Log.d(TAG, "no DL button, maybe the header row. " + row.toString());
                continue;
            }

            Meta meta = new Meta(vidUrl, title);
            meta.thumbUrl = thumbnailUrl;

            Elements cells = row.select("td");
            for (int i = 0; i < cells.size(); i++) {
                switch (i) {
                    // TODO: NOT to use positional parsing
                    case 0:
                        meta.quality = cells.get(i).text();
                        break;
                    case 1:
                        meta.format = cells.get(i).text();
                        break;
                    case 2:
                        meta.filesize = cells.get(i).text();
                        break;
                    case 3:
                        meta.url = cells.get(i).select(".btn").first().attr("abs:href");
                    default:
                        break;
                }
            }
            results.add(meta);
        }

        return results;
    }

    private String getVideoFullUrl(String vidUrl) {
        String fullUrl = "";
        try {
            String encodedVid = URLEncoder.encode(vidUrl, "UTF-8");
            fullUrl = KEEP_VID_BASE_URL + "?url=" + encodedVid;
        } catch (UnsupportedEncodingException e) {
        }
        return fullUrl;
    }
}
