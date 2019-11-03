package com.shaheen.webviewtest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.utils.PrefManager;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        prefManager = PrefManager.getInstance(this);


        if (currentUser != null && !prefManager.getIsFirsttime()) {
            mMoveToHomePage();
        }
        else {
            mMoveToLoginPage();
        }

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
