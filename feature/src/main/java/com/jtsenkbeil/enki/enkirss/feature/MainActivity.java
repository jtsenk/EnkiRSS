package com.jtsenkbeil.enki.enkirss.feature;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.activity.ShowsActivity;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

public class MainActivity extends AppCompatActivity {

    private TextView itv;
    private TextView atv;
    private Animation anima;
    private Animation anima2;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itv = findViewById(R.id.intro_tv);
        atv = findViewById(R.id.author_tv);
        anima = AnimationUtils.loadAnimation(this, R.anim.anima_intro);
        itv.startAnimation(anima);
        anima2 = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        atv.startAnimation(anima2);
        anima2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //go to show page when intro animation ends
                intent = new Intent(MainActivity.this, ShowsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

}
