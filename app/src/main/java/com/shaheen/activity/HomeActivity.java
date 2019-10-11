package com.shaheen.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shaheen.webviewtest.MainListFragment;
import com.shaheen.webviewtest.R;

public class HomeActivity extends AppCompatActivity {

    FrameLayout container;
    FragmentTransaction fragmentTransaction;
    MainListFragment mainListFragment;
    static TextView TV_points;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        // load mainList fragment
        loadFragment(mainListFragment);
    }

    private void init() {

        context=this;
        mainListFragment = new MainListFragment();

        TV_points = (TextView) findViewById(R.id.tv_pts);
        TV_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                animatePointsText();

            }
        });
    }

    private static void animatePointsText() {
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.anim);
        rotation.setRepeatCount(1);

        TV_points.startAnimation(rotation);
    }


    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    public static void updatePoint(int point){
        animatePointsText();
        TV_points.setText(String.valueOf(point));
    }
}
