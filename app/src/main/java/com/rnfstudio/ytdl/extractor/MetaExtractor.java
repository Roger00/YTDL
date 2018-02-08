package com.rnfstudio.ytdl.extractor;

import java.util.List;

/**
 * Created by fishchang on 2018/2/6.
 */

public interface MetaExtractor {
    List<Meta> extract(String url);
}
