package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ViewFragmentStateAdapter;
import com.jtsenkbeil.enki.enkirss.feature.frags.DownloadsFragment;
import com.jtsenkbeil.enki.enkirss.feature.frags.ShowsFragment;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class ShowsActivity extends AppCompatActivity {

    private TabLayout tabL;
    private ViewPager vPager;
    private ArrayList<Pair<String,Fragment>> vList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);
        tabL = findViewById(R.id.shows_pager_tabl);
        vPager = findViewById(R.id.shows_vpager);

        vList.add(new Pair<String, Fragment>("Shows", new ShowsFragment() ) );
        vList.add(new Pair<String, Fragment>("Downloads", new DownloadsFragment() ) );

        ViewFragmentStateAdapter adapter = new ViewFragmentStateAdapter(this.getSupportFragmentManager(), vList);
        vPager.setAdapter(adapter);
        tabL.setupWithViewPager(vPager);

        File df = getFilesDir();
        Utils.toastShort(df.getPath());
        Utils.logD("Files Directory",df.getPath());
    }
}
