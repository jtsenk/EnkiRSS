package com.jtsenkbeil.enki.enkirss.feature.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.activity.ShowEpisodesActivity;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;

import java.io.File;

public class AbzuDownloader {

    private DownloadManager dlm;
    private DownloadManager.Request dlReq;
    private String url;
    private String dest;
    private File f;
    private Uri destination;
    private Ki ki;

    //Preliminary downloading knowledge adapted from:
    //https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
    public AbzuDownloader() {
        try {
            dlm = (DownloadManager) MainActivity.mainContext.getSystemService(Context.DOWNLOAD_SERVICE);
            dlReq = null;
            f = null;
            url = "";
            dest = "";
            destination = null;
            ki = null;
        } catch (Exception exc) {
            Utils.logD("AbzuError","ConstructorError: " + exc.getMessage());
        }
    }

    //downloads an xml file to be parsed, and returns its downloaded filepath
    public String downloadXML(String u) {
        try {
            //hack to make sure the XML broadcast receiver doesn't fire download for every download completed
            ShowEpisodesActivity.isXML = true;
            url = u;
            dlReq = new DownloadManager.Request(Uri.parse(url));
            dest = url.substring(url.lastIndexOf('/') + 1) + ".xml";
            Utils.logD("Abzu","Dest: " + dest);
            f = new File(MainActivity.mainContext.getExternalFilesDir(null) + "/" + dest);
            destination = Uri.fromFile(f);
            Utils.logD("Abzu","destination: " + destination.getPath());
            //dlReq.setDestinationUri(Uri.parse("file:///" + MainActivity.mainContext.getFilesDir().getPath() + "/" + dest));
            //the below will apparently just download to the default Downloads directory
            //dlReq.setDestinationInExternalFilesDir(MainActivity.mainContext, "files", dest);
            dlReq.setDestinationUri(destination);
            //overwrite file
            if (f.exists() ) {
                f.delete();
                if (!f.exists()) {
                    Utils.logD("Abzu","Deleted old file");
                }
            }
            dlReq.setMimeType("text/xml");
            dlReq.allowScanningByMediaScanner();
            //set visibility to visible and notify completion for debugging :JTS
            //dlReq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //set visibility to hidden for actual use :JTS
            dlReq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            dlm.enqueue(dlReq);
            return destination.getPath();
        } catch (Exception exc) {
            Utils.logD("AbzuError","Error: " + exc.getMessage());
            return null;
        }
    }

    public boolean downloadEpisode(String link, String show, String title, long size, String type, String desc) {
        try {
            //hack to make sure the XML broadcast receiver doesn't fire download for every download completed
            ShowEpisodesActivity.isXML = false;
            url = link;
            dlReq = new DownloadManager.Request(Uri.parse(url));
            if (type.equals("audio/mpeg") ) {
                dest = url.substring(url.lastIndexOf('/') + 1);
            } else {
                Utils.logD("AbzuError","Episode type is not audio/mpeg");
                return false;
            }
            Utils.logD("Abzu","Dest: " + dest);
            f = new File(MainActivity.mainContext.getExternalFilesDir(null) + "/" + dest);
            destination = Uri.fromFile(f);
            Utils.logD("Abzu","destination: " + destination.getPath());
            dlReq.setDestinationUri(destination);
            dlReq.setMimeType(type);
            dlReq.allowScanningByMediaScanner();
            dlReq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //add download to DB
            ki = new Ki();
            ki.addDL(destination.getPath(), show, title, size, desc);
            ki.closeDown();
            //add episode to download queue
            dlm.enqueue(dlReq);
            return true;
        } catch (Exception exc) {
            Utils.logD("AbzuError","Error: " + exc.getMessage());
            return false;
        }
    }


}
