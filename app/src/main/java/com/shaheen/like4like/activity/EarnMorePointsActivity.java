package com.shaheen.like4like.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.like4like.R;
import com.shaheen.like4like.databaseRef.PagesRef;
import com.shaheen.like4like.databaseRef.TransactionsRef;
import com.shaheen.like4like.databaseRef.UsersRef;
import com.shaheen.like4like.model.Transaction;
import com.shaheen.like4like.model.UserProfile;
import com.shaheen.like4like.utils.Consts;
import com.shaheen.like4like.utils.PrefManager;
import com.shaheen.like4like.utils.Utils;

public class EarnMorePointsActivity extends AppCompatActivity {

    static TextView TV_points;
    static Context context;
    FirebaseUser user;
    static String userID = null;
    static ImageView BTN_Nav;
    static PrefManager prefManager;
    Button BTN_watchVideo;
    Button BTN_spin;
    private RewardedAd rewardedAd;
    AdView mAdView_banner;
    private InterstitialAd mInterstitialAd;
    private boolean adShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnmorepoints);

        init();
    }

    private void init() {


        Utils.isInternetAvailable(EarnMorePointsActivity.this);

        mAdView_banner = findViewById(R.id.adView);
        BTN_spin = findViewById(R.id.btn_spin);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView_banner.loadAd(adRequest);


        rewardedAd = new RewardedAd(this,
                "ca-app-pub-2416033626083993/1438173160");




        //change ad unit id
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2416033626083993/7285938788");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (!adShown) {
                    adShown=true;
                    mInterstitialAd.show();
                }
            }
        });








        prefManager = PrefManager.getInstance(EarnMorePointsActivity.this);
        context = this;
        BTN_Nav = findViewById(R.id.nav_btn);
        BTN_watchVideo=findViewById(R.id.btn_watchVideo);
        loadRewardedVideo();
        //BTN_watchVideo.setEnabled(false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            getCurrentPointsAndSetInTextView(userID, EarnMorePointsActivity.this, true);
        } else {
            mRedirectToLoginPage();
        }

        TV_points = (TextView) findViewById(R.id.tv_pts);
        TV_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animatePointsText(EarnMorePointsActivity.this);

            }
        });

        BTN_Nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EarnMorePointsActivity.super.onBackPressed();
            }
        });



        BTN_spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(EarnMorePointsActivity.this)) {
                    Intent intent = new Intent(EarnMorePointsActivity.this, SpinAndWinActivity.class);
                    startActivity(intent);
                }
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
                            updatePoint(20,"You watched a video Ad",userID,EarnMorePointsActivity.this,true);


                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {

                            loadRewardedVideo();
                            Toast.makeText(EarnMorePointsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);

                } else {
                    loadRewardedVideo();
                    Toast.makeText(EarnMorePointsActivity.this, "No Ads available right now. Please try again later", Toast.LENGTH_LONG).show();
                }


            }
        });





    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadRewardedVideo() {

       // BTN_watchVideo.setEnabled(false);

        if (!rewardedAd.isLoaded()) {

            rewardedAd = new RewardedAd(this,
                    "ca-app-pub-2416033626083993/1438173160");
            RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                @Override
                public void onRewardedAdLoaded() {

                    BTN_watchVideo.setEnabled(true);
                }

                @Override
                public void onRewardedAdFailedToLoad(int errorCode) {
                    //  BTN_watchVideo.setEnabled(false);
                }
            };
            rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        }

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

    private static void animatePointsText(Context context) {
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.anim);
        rotation.setRepeatCount(1);

        TV_points.startAnimation(rotation);
    }

    public static void updatePoint(final int points, final String msg, final String user_ID, final Context c, final boolean animate) {

            UsersRef.getUserByUserId(context, user_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int currentPoints = userProfile.getTotalPoints();
                        int newPoints = currentPoints +points;

                        mUpdatePointsInFirebase(user_ID, newPoints,c,animate);

                        Transaction transaction = new Transaction();
                        transaction.setBalance(newPoints);
                        transaction.setDate(Utils.DateMonthyear(System.currentTimeMillis()));
                        transaction.setPlusOrMinus(Consts.PLUS);
                        transaction.setPoints(points);
                        transaction.setType(Consts.TRANSACTION_AD_WATCH);
                        transaction.setMsg(msg);

                        mAddTransaction(user_ID, transaction);
                        Toast.makeText(c, "You earned "+points+" reward points", Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(c, "Something went wrong", Toast.LENGTH_LONG).show();
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


    public static void getCurrentPointsAndSetInTextView(String userID, final Context c, final boolean animate) {

        Log.d("userID", userID);

        UsersRef.getUserByUserId(c, userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    int currentPoints = userProfile.getTotalPoints();

                    if (animate) {
                        animatePointsText(c);
                        TV_points.setText(currentPoints + " Pts");

                        if (currentPoints<userProfile.getPointsPerLike()){
                            TV_points.setTextColor(Color.parseColor("#ff0000"));
                        }
                        else {
                            TV_points.setTextColor(Color.parseColor("#ffffff"));

                        }
                    }

                    //   }

                } else {
                    Toast.makeText(c, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void mUpdatePointsInFirebase(String userID, int newPoints, Context c, boolean animate) {

        UsersRef.getUserByUserId(c, userID).getRef().child(Consts.F_TOTAL_POINTS).setValue(newPoints);
        prefManager=PrefManager.getInstance(c);
        if (prefManager.getLitedPageId()!=null) {
            PagesRef.getPageByPageId(context, prefManager.getLitedPageId()).child(Consts.F_USER_TOTAL_POINTS).setValue(newPoints);
        }

        getCurrentPointsAndSetInTextView(userID,c,animate);

    }

}
