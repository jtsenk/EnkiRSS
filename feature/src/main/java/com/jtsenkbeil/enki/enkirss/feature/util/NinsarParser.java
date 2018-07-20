package com.jtsenkbeil.enki.enkirss.feature.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NinsarParser {

    private XmlPullParser xp;
    private String txt;
    private String name;
    private InputStream in;
    public Episode ep;
    private ArrayList<Episode> entries;
    private File f;

    public NinsarParser(String filename) {

        try {

            xp = XmlPullParserFactory.newInstance().newPullParser();
            txt = null;
            name = null;
            ep = null;
            f = new File(filename);
            in = new FileInputStream(f);
            //log the filesize
            Utils.logD("NinsarParser",String.valueOf(f.length()) );

            //Debug downloaded XML file
            //Display the entire contents
            /*
            InputStreamReader isr = new InputStreamReader(in,"UTF8");
            String ls = "";
            BufferedReader br = new BufferedReader(isr);
            while ( (ls = br.readLine() ) != null) {
                Utils.logD("FIS",ls);
            }
            */

            //by reading the length of the file (below), the error below chages to END_DOCUMENT, so we can assume it's the BOM Java error? :JTS
            //byte[] b = new byte[(int)f.length()/4];
            //in.read(b);
            //Utils.logD("FIS", Arrays.toString(b));

            Utils.logD("NinsarParser","Ninsar Construction Complete!");
        } catch (Exception exc) {
            Utils.logD("NinsarError","Constructor Error: " + exc.getMessage());
        }
    }

    public Episode getEpisodeInfo() {
        Utils.logD("getEpisdoeInfo","Starting Episode #" + entries.size() + " Collection");
        ep = new Episode();

        try {
            while ( (xp.next() != XmlPullParser.END_TAG) || !(xp.getName().equals("item"))) {
                if (xp.getEventType() == XmlPullParser.START_TAG) {
                    name = xp.getName();
                    if (name != null) {
                        if (name.equals("title")) {
                            //txt = xp.getText();
                            //ep.setTitle(txt);
                            //Utils.logD("Ep", "title: " + txt);
                            if (xp.next() == XmlPullParser.TEXT) {
                                txt = xp.getText();
                                ep.setTitle(txt);
                                Utils.logD("Ep", "title: " + txt);
                                xp.nextTag();
                            }
                        } else if (name.equals("description")) {
                            //txt = xp.getText();
                            //ep.setSummary(txt);
                            //Utils.logD("Ep", "description: " + txt);
                            if (xp.next() == XmlPullParser.TEXT) {
                                txt = xp.getText();
                                ep.setSummary(txt);
                                Utils.logD("Ep", "summary: " + txt);
                                xp.nextTag();
                            }
                        } else if (name.equals("link")) {
                            //txt = xp.getText();
                            //ep.setLink(txt);
                            //Utils.logD("Ep", "link: " + txt);
                            if (xp.next() == XmlPullParser.TEXT) {
                                txt = xp.getText();
                                ep.setLink(txt);
                                Utils.logD("Ep", "link: " + txt);
                                xp.nextTag();
                            }
                        }
                    }
                }
            }
            return ep;
        } catch (Exception exc) {
            Utils.logD("getEpsiodeInfo","ERROR: " + exc.getMessage());
            return null;
        }
    }//end getEpisdodeInfo

    public String getPodcastName() {
        return null;
    }

    public ArrayList<Episode> parseEps() throws XmlPullParserException, IOException {
        try {
            xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xp.setInput(in, "UTF8");
            xp.nextTag();
            Utils.logD("parseEps","getName() = " + xp.getName());
            return readFeedEps();
        } catch (Exception exc) {
            Utils.logD("parseEps","Error: " + exc.getMessage());
            exc.printStackTrace();
            return null;
        } finally {
            in.close();
        }
    }

    private ArrayList<Episode> readFeedEps() throws XmlPullParserException, IOException {
        entries = new ArrayList();
        String tag = "";
        while (xp.next() != XmlPullParser.END_DOCUMENT) {
            tag = xp.getName();
            if (tag != null) {
                //only getEpisodeInfo if this is the start_tag of an item
                if (tag.equals("item") && xp.getEventType()==XmlPullParser.START_TAG) {
                    entries.add(getEpisodeInfo());
                }
            } else {
                //tag is null, must be text, etc., so skip this one
                //Utils.logD("readFeedEps","tag is null, skipping iteration");
            }
            Utils.logD("readFeedEps","tag: " + tag);
        }
        return entries;
    }

    //original-ish from AndroidDev - not really using that skip method anymore, because why? :JTS
    private ArrayList<Episode> readFeed() throws XmlPullParserException, IOException {
        entries = new ArrayList();
        String tag = "";
        while (xp.next() != XmlPullParser.END_DOCUMENT) {
            if (tag == null) {
                Utils.logD("readFeed","tag is null, skipping iteration");
            } else {
                tag = xp.getName();
            }
            // Starts by looking for the entry tag
            if (tag.equals("item")) {
                entries.add(getEpisodeInfo());
            } else {
                //skip();
            }
        }
        return entries;
    }

    //from https://developer.android.com/training/basics/network-ops/xml
    //don't really know what I'd use this for, but thought I'd keep it around :JTS
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