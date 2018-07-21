package com.jtsenkbeil.enki.enkirss.feature.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.util.AbzuDownloader;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.text.DecimalFormat;

public class EpisodeDialog extends Dialog {

    private TextView dHeader;
    private TextView dEpTitle;
    private TextView dEpDesc;
    private TextView dEpSize;
    private TextView dEpType;
    private Button cancelBtn;
    private Button dlBtn;

    private DecimalFormat df;
    private double mbFileSize;
    private AbzuDownloader abzu;

    public interface EpisodeDialogEventListener {
        public void onDownloadClicked();
        public void onCancelClicked();
    }

    private EpisodeDialogEventListener eListener;

    public EpisodeDialog(@NonNull Context context, final Bundle epBundle, final EpisodeDialogEventListener eListener) {
        super(context, R.style.dialog);
        this.eListener = eListener;
        //set the AbzuDownloader to null until we need it (see below)
        abzu = null;
        setContentView(R.layout.dialog_episode);
        dHeader = findViewById(R.id.ep_dialog_header);
        dEpTitle = findViewById(R.id.ep_dialog_ep_title);
        dEpDesc = findViewById(R.id.ep_dialog_ep_desc);
        dEpSize = findViewById(R.id.ep_dialog_ep_filesize);
        cancelBtn = findViewById(R.id.ep_dialog_cancel_btn);
        dlBtn = findViewById(R.id.ep_dialog_dl_btn);
        dEpType = findViewById(R.id.ep_dialog_ep_filetype);

        //get the episode info from the epBundle :JTS
        dHeader.setText(epBundle.getString("showName"));
        dEpTitle.setText(epBundle.getString("epTitle"));
        dEpDesc.setText(epBundle.getString("epDesc"));
        //convert fileSize from B to MB
        mbFileSize = (double)epBundle.getLong("epSize") / 1048576.0;
        df = new DecimalFormat("#.##");
        dEpSize.setText("Size: " + df.format(mbFileSize) + " MB" );
        dEpType.setText(epBundle.getString("epType"));

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                eListener.onCancelClicked();
            }
        });

        //download episode an return to the main shows/downloads screen on download click
        dlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abzu = new AbzuDownloader();
                if (abzu.downloadEpisode(epBundle.getString("epLink"), epBundle.getString("showName"), epBundle.getString("epTitle"), epBundle.getLong("epSize"), epBundle.getString("epType")) ) {
                    Utils.logD("Abzu","Episode Download Successful");
                } else {
                    Utils.logD("AbzuError","Episode Download Failed!");
                }
                cancel();
                eListener.onDownloadClicked();
            }
        });
    }
}
