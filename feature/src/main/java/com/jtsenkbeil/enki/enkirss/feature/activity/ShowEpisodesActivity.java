package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.adapt.EpsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ShowsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.dialog.EpisodeDialog;
import com.jtsenkbeil.enki.enkirss.feature.util.AbzuDownloader;
import com.jtsenkbeil.enki.enkirss.feature.util.Episode;
import com.jtsenkbeil.enki.enkirss.feature.util.NinsarParser;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.util.ArrayList;

public class ShowEpisodesActivity extends AppCompatActivity {

    private ListView lv;
    private TextView tv;
    private TextView wtv;

    private ArrayList<Episode> epList;
    private NinsarParser np;
    private String file;
    private String showName;
    private Ki ki;
    private AbzuDownloader abzu;
    private BroadcastReceiver br;
    private Bundle bundle;
    private Episode ep;
    public static boolean isXML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_episodes);
        lv = findViewById(R.id.episodes_listv);
        //make listview invisible until xml is parsed
        lv.setVisibility(View.INVISIBLE);
        epList = new ArrayList<>();

        //get the showName from the Bundle
        bundle = getIntent().getBundleExtra("showB");
        showName = bundle.getString("showName");
        //set showName to the top text view, then make invisible until xml gets parsed
        tv = findViewById(R.id.episodes_textv);
        tv.setText(showName);
        tv.setVisibility(View.INVISIBLE);

        //get the waiting textview ready
        wtv = findViewById(R.id.episodes_waiting_textv);
        wtv.setText("Downloading & Parsing RSS XML file");

        //get a downloader to download the XML RSS file
        abzu = new AbzuDownloader();
        //get XML URL from DB
        ki = new Ki();
        //debug the link URL
        Utils.logD("ShowEpisodesActivity","link url=" + ki.getLinkURL(showName));
        //kinda hacky but this will notify what sort of xml we are downloading :JTS
        isXML = true;
        AddFeedActivity.isNewXML = false;
        //download the xml rss file
        file = abzu.downloadXML( ki.getLinkURL(showName) );

        //wait until the download has finished before parsing (make new BroadcastReceiver)
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Utils.logD("DL Broadcast Reveiver","Starting");

                //make sure this the right situation to start parsing xml again (below)
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()) && isXML)  {
                    np = new NinsarParser(file);
                    //setText will not work here...why not? :JTS
                    //wtv.setText("Parsing XML");
                    //parse XML here to get the episodes for this show
                    try {
                        epList = np.parseEps();
                    } catch (Exception exc) {
                        Utils.logD("ShowEpisdoesActivity","Error calling np.parseEps: " + exc.getMessage());
                        wtv.setText("XML Parsing Failed");
                    }

                    //switch the visibilites
                    wtv.setVisibility(View.INVISIBLE);
                    tv.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.VISIBLE);

                    //set up the episode listview
                    EpsListAdapter adapter = new EpsListAdapter(ShowEpisodesActivity.this, epList);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Utils.toastShort(String.valueOf(position));
                            //put episode info into a Bundle for the dialog :JTS
                            bundle = new Bundle();
                            ep = epList.get(position);
                            bundle.putString("showName",showName);
                            bundle.putString("epTitle", ep.getTitle());
                            bundle.putString("epDesc", ep.getSummary());
                            bundle.putLong("epSize", ep.getFileSize());
                            bundle.putString("epType", ep.getMimeType());
                            bundle.putString("epLink", ep.getLink());

                            EpisodeDialog epd = new EpisodeDialog(ShowEpisodesActivity.this, bundle, new EpisodeDialog.EpisodeDialogEventListener() {
                                @Override
                                public void onDownloadClicked() {
                                    Utils.logD("EPDEListener","EPD Hears DL at ShowsEpisodesActivity");
                                    Intent intent2 = new Intent(ShowEpisodesActivity.this, ShowsActivity.class);
                                    startActivity(intent2);
                                    ShowEpisodesActivity.this.finish();
                                }
                                @Override
                                public void onCancelClicked() {
                                    Utils.logD("EPDEListener","EPD Hears Cancel at ShowsEpisodesActivity");
                                }
                            });;
                            epd.show();
                        }
                    });
                }
            }
        };
        //register the BroadcastReceiver
        MainActivity.mainContext.registerReceiver(br, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //Older Ki DB stuff below
        /*
        ki = new Ki();
        if (ki != null) {
            Utils.logD("Ki","Ki summoned!");
            Cursor c1 = ki.getTable("tbl_shows");
            if (c1 == null) {
                Utils.logD("Ki","cursor is null...");
            } else {
                Utils.logD("Ki","cursor is not null!");
                while (c1.moveToNext()) {
                    Utils.logD("Ki::Cursor",c1.getString(c1.getColumnIndex("id")) + "    " + c1.getString(c1.getColumnIndex("name")) + "    "  + c1.getString(c1.getColumnIndex("xml_link")));
                }
            }
        } else {
            Utils.logD("Ki","Ki was not summoned.  The DB remains shrouded in darkness.");
        }
        */

    }//end onCreate

}//end ShowEpisodesActivity
