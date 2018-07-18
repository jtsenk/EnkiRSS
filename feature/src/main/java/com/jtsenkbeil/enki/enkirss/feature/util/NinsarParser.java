package com.jtsenkbeil.enki.enkirss.feature.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NinsarParser {

    private XmlPullParser xp;
    private String txt;
    private String name;
    private FileInputStream in;
    public Episode ep;

    public NinsarParser(String file) {

        try {

            xp = XmlPullParserFactory.newInstance().newPullParser();
            txt = null;
            name = null;
            ep = null;
            in = new FileInputStream(new File(file));
            //Utils.toastShort("Ninsar Construction Complete!");
            Utils.logD("NinsarParser","Ninsar Construction Complete!");
        } catch (Exception exc) {
            Utils.logD("NinsarError","Constructor Error: " + exc.getMessage());
            //Utils.toastShort("Ninsar Construction Failed!");
        }
    }

    public Episode getEpisodeInfo() {

        ep = new Episode();

        try {

            while (xp.next() != XmlPullParser.END_TAG) {
                if (xp.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                name = xp.getName();
                if (name.equals("title")) {
                    txt = xp.getText();
                    ep.setTitle(txt);
                } else if (name.equals("description")) {
                    txt = xp.getText();
                    ep.setSummary(txt);
                } else if (name.equals("link")) {
                    txt = xp.getText();
                    ep.setLink(txt);
                }
            }
            return ep;

        } catch (Exception exc) {
            Utils.logD("getEpsiodeInfo error","ERROR: " + exc.getMessage());
            return null;
        }

    }

    public String getPodcastName() {
        return null;
    }

    public List parse() throws XmlPullParserException, IOException {
        try {
            //xp = Xml.newPullParser();
            xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xp.setInput(in, null);
            xp.nextTag();
            return readFeed();
        } finally {
            in.close();
        }
    }

    private List readFeed() throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        String name = null;
        xp.require(XmlPullParser.START_TAG, null, "channel");
        while (xp.next() != XmlPullParser.END_TAG) {
            if (xp.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            name = xp.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(getEpisodeInfo());
            } else {
                skip();
            }
        }
        return entries;
    }

    //from https://developer.android.com/training/basics/network-ops/xml
    private void skip() throws XmlPullParserException, IOException {
        if (xp.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (xp.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


} //end NinsarParser class