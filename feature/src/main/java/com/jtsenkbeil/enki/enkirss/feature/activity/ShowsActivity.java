package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ViewFragmentStateAdapter;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.frags.DownloadsFragment;
import com.jtsenkbeil.enki.enkirss.feature.frags.ShowsFragment;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class ShowsActivity extends AppCompatActivity {

    private TabLayout tabL;
    private ViewPager vPager;
    private ArrayList<Pair<String,Fragment>> vList = new ArrayList<>();
    private Button testBtn;
    private Button delBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        testBtn = findViewById(R.id.shows_activity_add_test_btn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ki ki = new Ki();
                ki.debugAddTestVals();
                Utils.logD("ShowsActivity","Add Test Vals to DB");
                ki.closeDown();
            }
        });

        delBtn = findViewById(R.id.shows_activity_del_test_btn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ki ki = new Ki();
                ki.debugClearTestVals();
                Utils.logD("ShowsActivity","Clear Show Table");
                ki.closeDown();
            }
        });

        tabL = findViewById(R.id.shows_pager_tabl);
        vPager = findViewById(R.id.shows_vpager);

        vList.add(new Pair<String, Fragment>("Shows", new ShowsFragment() ) );
        vList.add(new Pair<String, Fragment>("Downloads", new DownloadsFragment() ) );

        ViewFragmentStateAdapter adapter = new ViewFragmentStateAdapter(this.getSupportFragmentManager(), vList);
        vPager.setAdapter(adapter);
        tabL.setupWithViewPager(vPager);

        //debug code to figure out where the files are saved/accessed :JTS
        //File df = getFilesDir();
        //Utils.toastShort(df.getPath());
        //Utils.logD("Files Directory",df.getPath());
    }
}
