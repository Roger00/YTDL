package com.rnfstudio.ytdl.extractor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fishchang on 2018/2/9.
 */

public class ExtractUtils {
    public static Map<String, String> parseQueryStrings(String fmt) {
        Map<String, String> result = new HashMap();

        String[] parts = fmt.split("&");
        for (String part : parts) {
            String[] pairs = part.split("=");
            if (pairs.length < 2) continue;
            result.put(pairs[0], pairs[1]);
        }
        return result;
    }
}
