package com.shaheen.like4like.activity;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import dmax.dialog.SpotsDialog;

import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shaheen.like4like.R;
import com.shaheen.like4like.utils.Utils;

public class HowItWorksActivity extends AppCompatActivity {

  //  RecyclerView RV_faq;
  //  List<FAQ> list;
    Runnable r;
  //  FaqAdapter faqAdapter;
    AlertDialog dialog;
    ImageView BTN_back;
    AdView mAdView_banner;
    private InterstitialAd mInterstitialAd;
    private boolean adShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        init();
    }

    private void init() {

        Utils.isInternetAvailable(HowItWorksActivity.this);



        mAdView_banner = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView_banner.loadAd(adRequest);



        //change ad unit id
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2416033626083993/4464334324");
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






        //  RV_faq = (RecyclerView) findViewById(R.id.list_faq);
        BTN_back = findViewById(R.id.nav_btn);

        BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowItWorksActivity.super.onBackPressed();
            }
        });


        dialog = new SpotsDialog(this);

       /* list = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        RV_faq.setLayoutManager(mLayoutManager);
        RV_faq.setLayoutManager(mLayoutManager);
        faqAdapter = new FaqAdapter(getApplicationContext(), list);
        RV_faq.setAdapter(faqAdapter);*/


        mGetFAQ();

    }


    private void mGetFAQ() {

       /* showProgressDialog();
        FAQRef.getInstance(HowItWorksActivity.this).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    FAQ faq = snapshot.getValue(FAQ.class);

                    list.add(faq);
                }

                Collections.reverse(list);
                faqAdapter.notifyDataSetChanged();
                hideProgressDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/


      /*  list.add(faq);

        faqAdapter.notifyDataSetChanged();*/


    }


    void showProgressDialog() {
        if (dialog != null) {
            dialog.setTitle("Loading");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }






}
