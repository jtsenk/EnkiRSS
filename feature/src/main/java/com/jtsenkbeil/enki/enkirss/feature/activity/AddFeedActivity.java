package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.util.AbzuDownloader;
import com.jtsenkbeil.enki.enkirss.feature.util.NinsarParser;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.io.File;

public class AddFeedActivity extends AppCompatActivity {

    private EditText edTxt;
    private Button procBtn;
    private Button cancelBtn;
    private TextView procTxt;
    private Button debugValBtn;
    private RadioGroup radioG;

    private String newUrl;
    public static boolean isNewXML;
    private AbzuDownloader abzu;
    private Ki ki;
    private String filePath;
    private BroadcastReceiver br;
    private NinsarParser np;
    private int radioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNewXML = false;
        setContentView(R.layout.activity_add_feed);
        edTxt = findViewById(R.id.add_feed_et);
        procBtn = findViewById(R.id.add_feed_proc_btn);
        cancelBtn = findViewById(R.id.add_feed_cancel_btn);
        procTxt = findViewById(R.id.add_feed_proc_tv);
        debugValBtn = findViewById(R.id.add_feed_debug_val_btn);
        radioG = findViewById(R.id.add_feed_radio_g);

        radioG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioID = checkedId;
            }
        });
        //set 'none' as default 'add protocol' option
        radioG.check(R.id.add_feed_radio_none);

        //enable the known-feed test value button in debug mode
        if (Utils.debugMode) {
            Utils.logD("AddFeed","Debug Button Active");
            debugValBtn.setVisibility(View.VISIBLE);
            debugValBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.logD("AddFeed","Debug Button Clicked");
                    edTxt.setText("feeds.feedburner.com/TheHistoryOfRome");
                }
            });
        }

        //set onClick listener to process new feed with supplied URL
        procBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUrl = edTxt.getText().toString();
                //check the radio buttons to see if we add a protocol :JTS
                if (radioID == R.id.add_feed_radio_http) {
                    newUrl = "http://" + newUrl;
                } else if (radioID == R.id.add_feed_radio_https) {
                    newUrl = "https://" + newUrl;
                }
                Utils.logD("AddFeed", "Process Clicked, newUrl= " + newUrl);
                procTxt.setText("Processing new RSS feed at " + newUrl);
                //kinda hacky but this will notify what sort of xml we are downloading :JTS
                ShowEpisodesActivity.isXML = false;
                isNewXML = true;
                //get a downloader to download the XML RSS file
                abzu = new AbzuDownloader();
                filePath = abzu.downloadXML(newUrl);

                //wait until the download has finished before parsing (make new BroadcastReceiver)
                br = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Utils.logD("DL Broadcast Reveiver", "Starting");

                        //make sure this the right situation to start parsing xml again (below)
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()) && isNewXML) {
                            try {

                                np = new NinsarParser(filePath);
                                //parse XML for show info
                                String showTitle = np.parseShowInfo();
                                if (!showTitle.equals("")) {
                                    procTxt.setText("Found RSS feed with title: " + showTitle);
                                    Utils.logD("AddFeed","showTitle returned with value: " + showTitle);
                                    ki = new Ki();
                                    ki.addShow(showTitle, newUrl);
                                    ki.closeDown();
                                    isNewXML = false;
                                    Handler handler = ShowsActivity.getHandler();
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("newFeed",showTitle);
                                    msg.setData(bundle);
                                    msg.what = ShowsActivity.FEED_OK;
                                    intent = new Intent(AddFeedActivity.this, ShowsActivity.class);
                                    AddFeedActivity.this.startActivity(intent);
                                    handler.handleMessage(msg);
                                } else {
                                    procTxt.setText("Could not find Show Title\nIs this a valid RSS feed?");
                                    Utils.logD("AddFeed","showTitle returned blank");
                                }

                            } catch (Exception exc) {
                                Utils.logD("AddFeedError","Error: " + exc.getMessage());
                            }

                        }
                    }
                };
                //register broadcast receiver
                MainActivity.mainContext.registerReceiver(br, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });

        //set onClick to go back to prev activity
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                try {
                    if (br != null){
                        MainActivity.mainContext.unregisterReceiver(br);
                    }
                }catch (Exception exc) {
                    Utils.logD("AddFeed::OnBackBtn","Receiver was not registered");
                }
            }
        });

    }
}