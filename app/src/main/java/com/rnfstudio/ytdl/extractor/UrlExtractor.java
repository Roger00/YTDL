package com.rnfstudio.ytdl.extractor;

import java.util.List;

/**
 * Created by roger_huang on 2018/2/8.
 */

public interface UrlExtractor {
    List<String> extract(String vidUrl);
}
