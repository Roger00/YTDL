package com.rnfstudio.ytdl.extractor;

import android.util.Log;

import com.rnfstudio.ytdl.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fishchang on 2018/2/6.
 */

public class KeepVidExtractor implements MetaExtractor
{
    private static final String TAG = "KeepVidExtractor";
    private static final boolean DEBUG = MainActivity.DEBUG;
    private static final String KEEP_VID_BASE_URL = "https://keepvid.com/?url=";

    @Override
    public List<Meta> extract(String vidUrl) {
        Log.d(TAG, "Start extract from: " + vidUrl);
        List<Meta> results = new ArrayList<>();

        try {
            String fullUrl = getVideoFullUrl(vidUrl);
            Document doc = Jsoup.connect(fullUrl).get();

            // parse thumbnail url and title
            String title = doc.select(".item-3").first().text();
            String thumbnailUrl = doc.select(".result-img").first()
                    .attr("abs:src");

            // parse individual downloadable links
            Elements rows = doc.select(".result-table tr");
            for (Element row : rows) {
                Elements dlButtons = row.select(".btn");
                if (dlButtons.size() == 0) {
                    if (DEBUG) Log.d(TAG, "no DL button, maybe the header row. " + row.toString());
                    continue;
                }
                Meta meta = parseResultRow(row, vidUrl, title, thumbnailUrl);
                results.add(meta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Fail to get or parse KeepVid page: " + e.toString());
        }
        return results;
    }

    private String getVideoFullUrl(String vidUrl) {
        String fullUrl = "";
        try {
            String encodedVid = URLEncoder.encode(vidUrl, "UTF-8");
            fullUrl = KEEP_VID_BASE_URL + "?url=" + encodedVid;
        } catch (UnsupportedEncodingException ignored) {
        }
        return fullUrl;
    }

    private Meta parseResultRow(Element row, String vidUrl, String title, String thumbnailUrl) {
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
                    meta.url = cells.get(i).select(".btn").first()
                            .attr("abs:href");
                default:
                    break;
            }
        }
        return meta;
    }
}
