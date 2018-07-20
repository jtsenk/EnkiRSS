package com.jtsenkbeil.enki.enkirss.feature.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;

import java.io.File;

public class AbzuDownloader {

    private DownloadManager dlm;
    private DownloadManager.Request dlReq;
    private String url;
    private String dest;
    private File f;

    //Largely adapted from:
    //https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
    public AbzuDownloader() {
        try {
            dlm = (DownloadManager) MainActivity.mainContext.getSystemService(Context.DOWNLOAD_SERVICE);
            dlReq = null;
            f = null;
            url = "";
            dest = "";
        } catch (Exception exc) {
            Utils.logD("AbzuError","ConstructorError: " + exc.getMessage());
        }
    }

    public String downloadXML(String u) {
        try {
            url = u;
            dlReq = new DownloadManager.Request(Uri.parse(url));
            dest = url.substring(url.lastIndexOf('/') + 1) + ".xml";
            Utils.logD("Abzu","Dest: " + dest);
            f = new File(MainActivity.mainContext.getExternalFilesDir(null) + "/" + dest);
            Uri destination = Uri.fromFile(f);
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
            dlReq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            dlm.enqueue(dlReq);
            return destination.getPath();
        } catch (Exception exc) {
            Utils.logD("AbzuError","Error: " + exc.getMessage());
            return null;
        }
    }

    public boolean downloadEpisdoe(String u) {
        try {
            url = u;
            dlReq = new DownloadManager.Request(Uri.parse(url));
            dest = url.substring(url.lastIndexOf('/') + 1);
            //show progress
            dlReq.allowScanningByMediaScanner();
            dlReq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            dlReq.setDestinationInExternalPublicDir(MainActivity.mainContext.getFilesDir().getPath(), dest);
            dlm.enqueue(dlReq);
            return true;
        } catch (Exception exc) {
            Utils.logD("AbzuError","Error: " + exc.getMessage());
            return false;
        }
    }


}
