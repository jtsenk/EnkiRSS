package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.adapt.EpsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ShowsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.util.NinsarParser;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.util.ArrayList;

public class ShowEpisodesActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<String> epList;
    private NinsarParser np;
    private String file;
    private Ki ki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_episodes);
        lv = findViewById(R.id.episodes_listv);
        epList = new ArrayList<>();

        //parse XML here to get the episdoes for this show
        for (int i=0; i<50; i++) {
            epList.add("Episode " + i);
        }
        EpsListAdapter adapter = new EpsListAdapter(this, epList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                Utils.toastShort(String.valueOf(position));
            }
        });

        //get the file location from the DB eventually
        file = "how-did-this-get-made.xml";
        np = new NinsarParser(file);
        ki = new Ki();
        if (ki != null) {
            Utils.logD("Ki","Ki summoned!");
            Cursor c1 = ki.getTable("tbl_shows");
            if (c1 == null) {
                Utils.logD("Ki","cursor is null...");
            } else {
                Utils.logD("Ki","cursor is not null!");
                while (c1.moveToNext()) {
                    Utils.logD("Ki::Cursor",c1.getString(c1.getColumnIndex("name")) + "    "  + c1.getString(c1.getColumnIndex("xml_link")));
                }
            }
        } else {
            Utils.logD("Ki","Ki was not summoned.  The DB remains shrouded in darkness.");
        }

    }
}
