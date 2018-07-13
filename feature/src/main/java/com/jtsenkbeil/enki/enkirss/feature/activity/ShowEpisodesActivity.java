package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.adapt.EpsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ShowsListAdapter;

import java.util.ArrayList;

public class ShowEpisodesActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<String> epList;

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
                Toast.makeText(getBaseContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });

    }
}