package com.jtsenkbeil.enki.enkirss.feature.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

public class SettingsActivity extends AppCompatActivity {

    private Button testBtn;
    private Button delBtn;
    private Button dropBtn;
    private Button backBtn;
    private CheckBox debugCheck;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        debugCheck = findViewById(R.id.settings_debug_check_box);
        if (Utils.debugMode) {
            Log.d("debugCheck","true");
            debugCheck.setChecked(true);
        } else {
            Log.d("debugCheck","false");
            debugCheck.setChecked(false);
        }
        debugCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set debug mode (logging) if box is active
                if (isChecked) {
                    Utils.debugMode = true;
                    Log.d("Settings","Debug Mode Activated");
                } else {
                    Utils.debugMode = false;
                    Log.d("Settings","Debug Mode Deactivated");
                }
            }
        });

        testBtn = findViewById(R.id.settings_add_testv_btn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ki ki = new Ki();
                ki.debugAddTestVals();
                Utils.logD("SettingsActivity","Add Test Vals to DB");
                ki.closeDown();
            }
        });

        delBtn = findViewById(R.id.settings_delv_test_btn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ki ki = new Ki();
                ki.debugClearTestVals();
                Utils.logD("SettingsActivity","Clear Show Table");
                ki.closeDown();
            }
        });

        dropBtn = findViewById(R.id.settings_dropt_test_btn);
        dropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ki ki = new Ki();
                ki.debugDropDB();
                Utils.logD("SettingsActivity","Drop/Reset DB");
                ki.closeDown();
            }
        });

        backBtn = findViewById(R.id.settings_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SettingsActivity.this, ShowsActivity.class);
                startActivity(intent);
                SettingsActivity.this.finish();
            }
        });

    }
}
