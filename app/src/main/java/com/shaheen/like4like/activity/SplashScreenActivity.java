package com.shaheen.like4like.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shaheen.like4like.R;
import com.shaheen.like4like.utils.PrefManager;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(SplashScreenActivity.this, "ca-app-pub-2416033626083993~8380159490");


                prefManager = PrefManager.getInstance(SplashScreenActivity.this);


                if (!prefManager.getIsLoggedIn()) {
                    mAuth = FirebaseAuth.getInstance();

                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null && !prefManager.getIsFirsttime()) {
                        prefManager.setIsLoggedIn(true);
                        mMoveToHomePage();
                    } else {
                        prefManager.setIsLoggedIn(false);
                        mMoveToLoginPage();
                    }
                }
                else{

                    mMoveToHomePage();

                }




            }
        }, 500);

    }

    private void mMoveToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void mMoveToHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
