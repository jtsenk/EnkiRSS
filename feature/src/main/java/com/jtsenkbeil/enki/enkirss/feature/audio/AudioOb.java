package com.jtsenkbeil.enki.enkirss.feature.audio;

import com.jtsenkbeil.enki.enkirss.feature.audio.BaseAudioOb;

/**
 * Created by YoungH on 3/15/17.
 */

public class AudioOb extends BaseAudioOb {
    private String name;
    private String size;
    private String duration;
    private String info;
    private String iconUrl;
    private String show;

    public String getShow() {return show;}

    public void setShow(String show) {
        this.show = show;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
