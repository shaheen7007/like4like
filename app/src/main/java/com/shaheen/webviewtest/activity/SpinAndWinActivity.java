package com.shaheen.webviewtest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.utils.PrefManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinAndWinActivity extends AppCompatActivity {

    Button BTN_spin;
    Button BTN_watchAd;
    PrefManager prefManager;
    LuckyWheelView luckyWheelView;
    FirebaseUser user;
    ImageView BTN_Nav;
    private RewardedAd rewardedAd;
    AdView mAdView_banner;
    private InterstitialAd mInterstitialAd;
    AdView mAdView_banner2;
    Random rnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_and_win);

        rnd = new Random();

        user = FirebaseAuth.getInstance().getCurrentUser();
        BTN_Nav = findViewById(R.id.nav_btn);
        BTN_watchAd = (Button) findViewById(R.id.btn_watchVideo);

        rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917"); //change ad unit id


        loadRewardedVideo();


        //change ad unit id
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAd.setAdListener(new AdListener() {
                                          @Override
                                          public void onAdClosed() {
                                              // Load the next interstitial.
                                              mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                          }
                                      });



        BTN_Nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SpinAndWinActivity.super.onBackPressed();
            }
        });

        mAdView_banner = findViewById(R.id.adView);
        mAdView_banner2 = findViewById(R.id.adView1);
        BTN_spin = findViewById(R.id.btn_spin);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView_banner.loadAd(adRequest);
        mAdView_banner2.loadAd(adRequest1);



        BTN_spin = (Button) findViewById(R.id.spin);

        prefManager = PrefManager.getInstance(this);

        luckyWheelView = findViewById(R.id.luckyWheel);
        luckyWheelView.setTouchEnabled(false);
        final List<LuckyItem> data = new ArrayList<>();

        mGenerateLuckyItems(data);

        luckyWheelView.setData(data);
        //  luckyWheelView.setRound(4);


        mSetWatchAdBtnVisibility();


        BTN_spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BTN_spin.setEnabled(false);



                int[] ex = { 2, 4 };
                int randomNum = getRandomWithExclusion(rnd, 0, 9, ex);

                if (prefManager.getLastSpinTime() == 0) {
                    luckyWheelView.startLuckyWheelWithTargetIndex(randomNum);
                    // luckyWheelView.startLuckyWheelWithTargetIndex(2);
                    prefManager.setLastSpinTime(System.currentTimeMillis());
                } else if (DateUtils.isToday(prefManager.getLastSpinTime())) {
                    BTN_spin.setEnabled(true);
                    Toast.makeText(SpinAndWinActivity.this, "No spins left. Everyday you get 1 free spin", Toast.LENGTH_SHORT).show();
                } else {
                    luckyWheelView.startLuckyWheelWithTargetIndex(randomNum);
                    // luckyWheelView.startLuckyWheelWithTargetIndex(2);
                    prefManager.setLastSpinTime(System.currentTimeMillis());
                }
            }
        });

        BTN_watchAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rewardedAd.isLoaded()) {
                    Activity activityContext = SpinAndWinActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {

                            if (mInterstitialAd.isLoaded()){
                                mInterstitialAd.show();
                            }


                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {

                            loadRewardedVideo();
                            prefManager.setLastSpinTime(0);
                            mSetWatchAdBtnVisibility();

                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {

                            loadRewardedVideo();
                            Toast.makeText(SpinAndWinActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);

                } else {
                    loadRewardedVideo();
                    Toast.makeText(SpinAndWinActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }


            }
        });


        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {

                if (index == 8) {
                    prefManager.setLastSpinTime(0);
                    Toast.makeText(SpinAndWinActivity.this, data.get(index).topText, Toast.LENGTH_SHORT).show();
                }
                else {

                    switch (index){
                        case 0:
                            EarnMorePointsActivity.updatePoint(5,"Daily lucky wheel spin",user.getUid(),SpinAndWinActivity.this,true);
                            break;

                            case 1:
                            EarnMorePointsActivity.updatePoint(10,"Daily lucky wheel spin",user.getUid(),SpinAndWinActivity.this,true);
                            break;

                            case 3:
                            EarnMorePointsActivity.updatePoint(15,"Daily lucky wheel spin",user.getUid(),SpinAndWinActivity.this,true);
                            break;

                            case 5:
                            EarnMorePointsActivity.updatePoint(25,"Daily lucky wheel spin",user.getUid(),SpinAndWinActivity.this,true);
                            break;

                            case 6:
                            EarnMorePointsActivity.updatePoint(30,"Daily lucky wheel spin",user.getUid(),SpinAndWinActivity.this,true);
                            break;

                            case 7:
                            EarnMorePointsActivity.updatePoint(20,"Daily lucky wheel spin",user.getUid(),SpinAndWinActivity.this,true);
                            break;


                    }

                }

                mSetWatchAdBtnVisibility();

                BTN_spin.setEnabled(true);

            }
        });


    }


    private void loadRewardedVideo() {

        BTN_watchAd.setEnabled(false);


        rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {

                BTN_watchAd.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                BTN_watchAd.setEnabled(false);
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);


    }





    private void mSetWatchAdBtnVisibility() {
        if (DateUtils.isToday(prefManager.getLastSpinTime())) {
            BTN_watchAd.setVisibility(View.VISIBLE);
        } else if (prefManager.getLastSpinTime() == 0) {
            BTN_watchAd.setVisibility(View.GONE);
        }
    }

    private void mGenerateLuckyItems(List<LuckyItem> data) {
        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "05 Pts";
        luckyItem1.color = 0xffFFF3E0;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "10 Pts";
        luckyItem2.color = 0xffFFE0B2;
        data.add(luckyItem2);

        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.topText = "750 Pts";
        luckyItem9.color = 0xffFFCC80;
        data.add(luckyItem9);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "15 Pts";
        luckyItem4.color = 0xffFFF3E0;
        data.add(luckyItem4);


        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.topText = "500 Pts";
        luckyItem8.color = 0xffFFE0B2;
        data.add(luckyItem8);



        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "25 Pts";
        luckyItem6.color = 0xffFFCC80;
        data.add(luckyItem6);
        //////////////////

        //////////////////////
        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "30 Pts";
        luckyItem7.color = 0xffFFF3E0;
        data.add(luckyItem7);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "20 Pts";
        luckyItem5.color = 0xffFFE0B2;
        data.add(luckyItem5);


        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "Spin again";
        luckyItem3.color = 0xffFFCC80;
        data.add(luckyItem3);


    }




    public int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }







}
