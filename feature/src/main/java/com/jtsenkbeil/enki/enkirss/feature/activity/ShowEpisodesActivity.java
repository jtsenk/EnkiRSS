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
import android.widget.Toast;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.adapt.EpsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ShowsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.util.AbzuDownloader;
import com.jtsenkbeil.enki.enkirss.feature.util.Episode;
import com.jtsenkbeil.enki.enkirss.feature.util.NinsarParser;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.util.ArrayList;

public class ShowEpisodesActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<Episode> epList;
    private NinsarParser np;
    private String file;
    private Ki ki;
    private AbzuDownloader abzu;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_episodes);
        lv = findViewById(R.id.episodes_listv);
        epList = new ArrayList<>();

        //download the XML RSS file
        abzu = new AbzuDownloader();
        //get XML URL from DB eventually
        file = abzu.downloadXML("https://rss.earwolf.com/how-did-this-get-made");

        //wait until the download has finished before parsing
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Utils.logD("DL Broadcast Reveiver","Starting");
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction() ) ) {
                    np = new NinsarParser(file);
                    //parse XML here to get the episdoes for this show
                    try {
                        epList = np.parseEps();
                    } catch (Exception exc) {
                        Utils.logD("ShowEpisdoesActivity","Error calling np.parseEps: " + exc.getMessage());
                    }

                    EpsListAdapter adapter = new EpsListAdapter(ShowEpisodesActivity.this, epList);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getBaseContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                            Utils.toastShort(String.valueOf(position));
                        }
                    });
                }
            }
        };
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
