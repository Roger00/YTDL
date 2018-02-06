package com.rnfstudio.ytdl.extractor;

/**
 * Created by fishchang on 2018/2/6.
 */

public class Meta {
    public String name;
    public String vidUrl;
    public String quality;
    public String format;
    public String filesize;
    public String url;
    public String thumbUrl;

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
}
