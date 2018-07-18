package com.jtsenkbeil.enki.enkirss.feature.util;

public class Episode {

    private String title;
    private String summary;
    private String link;

    public Episode() {
        title = null;
        summary = null;
        link = null;
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

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
