package com.shaheen.like4like.activity;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.like4like.BottomSheetAdd;
import com.shaheen.like4like.BottomSheetEdit;
import com.shaheen.like4like.R;
import com.shaheen.like4like.databaseRef.TransactionsRef;
import com.shaheen.like4like.databaseRef.UsersRef;
import com.shaheen.like4like.model.Transaction;
import com.shaheen.like4like.model.UserProfile;
import com.shaheen.like4like.utils.Consts;
import com.shaheen.like4like.utils.Utils;

public class MyAccountActivity extends AppCompatActivity {

    AlertDialog dialog;
    FirebaseUser user;
    TextView TV_name, TV_points, TV_rank, TV_i_like, TV_u_like;
    Button BTN_edit;
    ImageView BTN_back;
    int i_like = 0;
    int u_like = 0;
    int rank = 0;
    long count=0;
    BottomSheetEdit bottomSheetEdit;
    BottomSheetAdd bottomSheetAdd;
    int addOrEdit=Consts.ADD;

    private InterstitialAd mInterstitialAd;
    private boolean adShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        init();

        setVisibilityOfButton();

        getUserDetails();


        BTN_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isInternetAvailable(MyAccountActivity.this)) {

                    if (addOrEdit == Consts.EDIT) {
                        bottomSheetEdit = BottomSheetEdit.newInstance();
                        bottomSheetEdit.show(getSupportFragmentManager(),
                                "edit page");
                    } else {
                        bottomSheetAdd = BottomSheetAdd.newInstance();
                        bottomSheetAdd.show(getSupportFragmentManager(),
                                "add page");
                    }
                }
            }
        });

    }

    private void setVisibilityOfButton() {

        UsersRef.getUserByUserId(MyAccountActivity.this,user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                if (userProfile.getListedPage()==null){
                    addOrEdit=Consts.ADD;
                    BTN_edit.setText("List My Page");
                }
                else {
                    addOrEdit=Consts.EDIT;
                    BTN_edit.setText("Edit Page Listing");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void getUserDetails() {

        showProgressDialog();


        UsersRef.getUserByUserId(this, user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);


                    setValuesToTextViews(userProfile);


                } else {
                    Toast.makeText(MyAccountActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setValuesToTextViews(final UserProfile userProfile) {

        TV_name.setText(userProfile.getUserName());
        TV_points.setText(String.valueOf(userProfile.getTotalPoints()));



        //get no of likes
        TransactionsRef.getInstance(this, user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                i_like = 0;
                u_like = 0;


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Transaction transaction = snapshot.getValue(Transaction.class);

                    if (transaction.getType() == Consts.TRANSACTION_I_LIKE) {
                        i_like++;
                    } else if (transaction.getType() == Consts.TRANSACTION_U_LIKE) {
                        u_like++;
                    }
                }

                TV_i_like.setText(String.valueOf(u_like));
                TV_u_like.setText(String.valueOf(i_like));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //get rank
        UsersRef.getInstance(this).orderByChild(Consts.F_TOTAL_POINTS).limitToLast(1000).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count=dataSnapshot.getChildrenCount();
                rank=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UserProfile userProfile1 = snapshot.getValue(UserProfile.class);
                    rank++;
                    if (userProfile1.getUserID() != null && userProfile1.getUserID().equals(user.getUid())) {
                        break;
                    }
                }


                if (rank==0 || count>=999){
                    TV_rank.setText("1000+");
                }
                else {
                    rank=(int) count-rank+1;
                    TV_rank.setText(String.valueOf(rank));
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void init() {

        Utils.isInternetAvailable(MyAccountActivity.this);


        dialog = new SpotsDialog(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        TV_name = findViewById(R.id.name);
        TV_points = findViewById(R.id.points);
        TV_rank = findViewById(R.id.rank);
        TV_i_like = findViewById(R.id.i_like);
        TV_u_like = findViewById(R.id.u_like);
        BTN_edit = findViewById(R.id.edit);


        BTN_back = findViewById(R.id.nav_btn);

        BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAccountActivity.super.onBackPressed();
            }
        });


        //change ad unit id
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2416033626083993/7163962931");
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
