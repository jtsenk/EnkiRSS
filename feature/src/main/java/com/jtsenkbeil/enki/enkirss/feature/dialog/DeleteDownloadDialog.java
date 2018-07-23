package com.jtsenkbeil.enki.enkirss.feature.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.text.DecimalFormat;

public class DeleteDownloadDialog extends Dialog {

    private TextView dHeader;
    private TextView dEpTitle;
    private TextView dEpDesc;
    private TextView dEpSize;
    private TextView dEpType;
    private Button cancelBtn;
    private Button delBtn;
    private Bundle bundle;

    private double mbFileSize;
    private DecimalFormat df;
    private String filePath;
    private int epID;
    private DeleteDownloadDialogEventListener dListener;

    public interface DeleteDownloadDialogEventListener {
        public void onDeleteClicked();
        public void onCancelClicked();
    }

    public DeleteDownloadDialog(@NonNull Context context, Bundle bundle, final DeleteDownloadDialogEventListener dListener) {
        super(context, R.style.dialog);
        //set the passed implemented listener interface
        this.dListener = dListener;
        //get the passed bundle with episdoe info
        this.bundle = bundle;
        epID = bundle.getInt("epID");
        filePath = bundle.getString("epPath");

        setContentView(R.layout.dialog_delete_download);
        dHeader = findViewById(R.id.del_dl_dialog_header);
        dEpTitle = findViewById(R.id.del_dl_dialog_ep_title);
        dEpDesc = findViewById(R.id.del_dl_ep_desc);
        dEpSize = findViewById(R.id.del_dl_ep_filesize);
        cancelBtn = findViewById(R.id.del_dl_cancel_btn);
        delBtn = findViewById(R.id.del_dl_del_btn);
        dEpType = findViewById(R.id.ep_dialog_ep_filetype);

        dEpTitle.setText(bundle.getString("epTitle"));
        dEpDesc.setText(Html.fromHtml(bundle.getString("epDesc")) );
        mbFileSize = (double)bundle.getLong("epSize") / 1048576.0;
        df = new DecimalFormat("#.##");
        dEpSize.setText("Size: " + df.format(mbFileSize) + " MB" );
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logD("DDLDialog","Cancel clicked");
                cancel();
                dListener.onCancelClicked();
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logD("DDLDialog","Delete clicked");
                Ki ki = new Ki();
                ki.deleteDL(epID, filePath);
                ki.closeDown();
                cancel();
                dListener.onDeleteClicked();
            }
        });

    }
}
