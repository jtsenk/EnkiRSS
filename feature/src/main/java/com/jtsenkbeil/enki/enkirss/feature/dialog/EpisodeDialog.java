package com.jtsenkbeil.enki.enkirss.feature.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.jtsenkbeil.enki.enkirss.feature.R;

public class EpisodeDialog extends Dialog {

    public interface EpisodeDialogEventListener {
        public void onOKClicked();
    }

    private EpisodeDialogEventListener eListener;

    public EpisodeDialog(@NonNull Context context, final EpisodeDialogEventListener eListener) {
        super(context, R.style.dialog);
        this.eListener = eListener;
        setContentView(R.layout.dialog_episode);
    }
}
