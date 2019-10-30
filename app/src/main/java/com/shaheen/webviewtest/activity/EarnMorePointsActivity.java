package com.shaheen.webviewtest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.databaseRef.TransactionsRef;
import com.shaheen.webviewtest.databaseRef.UsersRef;
import com.shaheen.webviewtest.model.Transaction;
import com.shaheen.webviewtest.model.UserProfile;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.utils.PrefManager;
import com.shaheen.webviewtest.utils.Utils;

public class EarnMorePointsActivity extends AppCompatActivity {

    static TextView TV_points;
    static Context context;
    FirebaseUser user;
    static String userID = null;
    static ImageView BTN_Nav;
    PrefManager prefManager;
    Button BTN_watchVideo;
    private RewardedAd rewardedAd;
    AdView mAdView_banner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnmorepoints);

        init();
    }

    private void init() {


        Utils.isInternetAvailable(EarnMorePointsActivity.this);

        mAdView_banner = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView_banner.loadAd(adRequest);


        rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917"); //change ad unit id



        prefManager = PrefManager.getInstance(EarnMorePointsActivity.this);
        context = this;
        BTN_Nav = findViewById(R.id.nav_btn);
        BTN_watchVideo=findViewById(R.id.btn_watchVideo);
        loadRewardedVideo();
        BTN_watchVideo.setEnabled(false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            getCurrentPointsAndSetInTextView(userID);
        } else {
            mRedirectToLoginPage();
        }

        TV_points = (TextView) findViewById(R.id.tv_pts);
        TV_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animatePointsText();

            }
        });

        BTN_Nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EarnMorePointsActivity.super.onBackPressed();
            }
        });



        BTN_watchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (rewardedAd.isLoaded()) {
                    Activity activityContext = EarnMorePointsActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {

                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {

                            loadRewardedVideo();
                            updatePoint(20);
                            Toast.makeText(EarnMorePointsActivity.this, "You earned "+20+" reward points", Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {

                            loadRewardedVideo();
                            Toast.makeText(EarnMorePointsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);

                } else {
                    loadRewardedVideo();
                    Toast.makeText(EarnMorePointsActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }


            }
        });





    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadRewardedVideo() {

        BTN_watchVideo.setEnabled(false);


        rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {

                BTN_watchVideo.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                BTN_watchVideo.setEnabled(false);
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);


    }




       /* public RewardedAd createAndLoadRewardedAd() {
             rewardedAd = new RewardedAd(this,
                    "ca-app-pub-3940256099942544/5224354917");
            RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                @Override
                public void onRewardedAdLoaded() {
                    // Ad successfully loaded.
                }

                @Override
                public void onRewardedAdFailedToLoad(int errorCode) {
                    // Ad failed to load.
                }
            };
            rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
            return rewardedAd;
        }*/







    private void mRedirectToLoginPage() {

        Intent intent = new Intent(EarnMorePointsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private static void animatePointsText() {
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.anim);
        rotation.setRepeatCount(1);

        TV_points.startAnimation(rotation);
    }

    public static void updatePoint(final int points) {

            UsersRef.getUserByUserId(context, userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int currentPoints = userProfile.getTotalPoints();
                        int newPoints = currentPoints +points;

                        mUpdatePointsInFirebase(userID, newPoints);

                        Transaction transaction = new Transaction();
                        transaction.setBalance(newPoints);
                        transaction.setDate(Utils.DateMonthyear(System.currentTimeMillis()));
                        transaction.setPlusOrMinus(Consts.PLUS);
                        transaction.setPoints(points);
                        transaction.setType(Consts.TRANSACTION_AD_WATCH);
                        transaction.setMsg("You watched a video Ad");

                        mAddTransaction(userID, transaction);


                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    private static void mAddTransaction(String userID, Transaction transaction) {

        TransactionsRef.getInstance(context, userID).push().setValue(transaction);

    }


    public static void getCurrentPointsAndSetInTextView(String userID) {

        Log.d("userID", userID);

        UsersRef.getUserByUserId(context, userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    int currentPoints = userProfile.getTotalPoints();

                    animatePointsText();
                    TV_points.setText(currentPoints + " Pts");


                    //   }

                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void mUpdatePointsInFirebase(String userID, int newPoints) {

        UsersRef.getUserByUserId(context, userID).getRef().child(Consts.F_TOTAL_POINTS).setValue(newPoints);

        getCurrentPointsAndSetInTextView(userID);

    }

}
