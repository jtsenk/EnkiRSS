package com.jtsenkbeil.enki.enkirss.feature.util;
//a data object to hold episdoe info from NinsarParser (from XML)
public class Episode {

    private String title;
    private String summary;
    private String link;
    //BELOW IN BYTES, so that every episode doesn't need to do the conversion math every time :JTS
    private long fileSize;
    private String mimeType;

    public Episode() {
        title = null;
        summary = null;
        link = null;
        fileSize = 0;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setFileSize(long size) {
        this.fileSize = size;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
