package com.shaheen.webviewtest.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.FbLoginFragment;
import com.shaheen.webviewtest.databaseRef.TransactionsRef;
import com.shaheen.webviewtest.model.Transaction;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.FbPageFragment;
import com.shaheen.webviewtest.MainListFragment;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.databaseRef.PagesRef;
import com.shaheen.webviewtest.databaseRef.UsersRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.model.UserProfile;
import com.shaheen.webviewtest.utils.HowItWorksDialog;
import com.shaheen.webviewtest.utils.PrefManager;
import com.shaheen.webviewtest.utils.Utils;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    FrameLayout container;
    FragmentTransaction fragmentTransaction;
    MainListFragment mainListFragment;
    FbLoginFragment fbLoginFragment;
    static TextView TV_points;
    static Context context;
    FirebaseUser user;
    static String userID = null;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    static ImageView BTN_Nav;
    static PrefManager prefManager;
    static Toolbar toolbar;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        // load mainList fragment

        Utils.isInternetAvailable(HomeActivity.this);

        if (prefManager.getIsFirsttime()) {
            toolbar.setVisibility(View.GONE);
            loadFragment(fbLoginFragment);
        } else {

            toolbar.setVisibility(View.VISIBLE);
            loadFragment(mainListFragment);

        }

    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof FbPageFragment) {
                    loadFragment(mainListFragment);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void init() {


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

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

        toolbar = findViewById(R.id.tb);
        prefManager = PrefManager.getInstance(HomeActivity.this);
        context = this;
        mainListFragment = new MainListFragment();
        fbLoginFragment = new FbLoginFragment();
        BTN_Nav = findViewById(R.id.nav_btn);
        changeNavButton(Consts.DRAWER);
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


                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                if (fragmentList != null) {
                    //TODO: Perform your logic to pass back press here
                    for (Fragment fragment : fragmentList) {
                        if (fragment instanceof FbPageFragment) {
                            loadFragment(mainListFragment);
                        } else {
                            dl.openDrawer(Gravity.LEFT);
                        }
                    }
                }
            }
        });


        dl = (DrawerLayout) findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();


        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               /* if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }*/

                int id = item.getItemId();
                switch (id) {
                    case R.id.account:

                        Intent intent = new Intent(HomeActivity.this, MyAccountActivity.class);
                        if (Utils.isInternetAvailable(HomeActivity.this)) {
                            dl.closeDrawer(Gravity.LEFT);
                            startActivity(intent);
                        }
                        break;
                    case R.id.transactions:
                        dl.closeDrawer(Gravity.LEFT);
                        intent = new Intent(HomeActivity.this, TransactionsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.earn_more:
                        dl.closeDrawer(Gravity.LEFT);
                        intent = new Intent(HomeActivity.this, EarnMorePointsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.faq:
                        dl.closeDrawer(Gravity.LEFT);
                        intent = new Intent(HomeActivity.this, HowItWorksActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        return true;
                }


                return true;

            }
        });

        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(1) // default 1
                .setTitle("Get 50 Points Now!")
                .setMessage("Give 5 stars rating and get 50 reward points")
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        if (which==-1) {
                            EarnMorePointsActivity.updatePoint(50, "You gave 5 stars rating", userID,HomeActivity.this,false);
                        }
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    private void mRedirectToLoginPage() {

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

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

    public static void updatePoint(final FbPage fbPage, int userType) {

        if (userType == Consts.THIS_USER) {

            UsersRef.getUserByUserId(context, userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int currentPoints = userProfile.getTotalPoints();
                        int newPoints = currentPoints + fbPage.getPoints();

                        mUpdatePointsInFirebase(userID, newPoints, fbPage.getPageID(), Consts.THIS_USER);  // the user logged in this device

                        Transaction transaction = new Transaction();
                        transaction.setBalance(newPoints);
                        transaction.setDate(Utils.DateMonthyear(System.currentTimeMillis()));
                        transaction.setPlusOrMinus(Consts.PLUS);
                        transaction.setPoints(fbPage.getPoints());
                        transaction.setType(Consts.TRANSACTION_I_LIKE);
                        transaction.setMsg("You liked a page");

                        mAddTransaction(userID, transaction);


                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            UsersRef.getUserByUserId(context, fbPage.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int currentPoints = userProfile.getTotalPoints();
                        int newPoints = currentPoints - userProfile.getPointsPerLike();

                        if (newPoints < 0) {
                            newPoints = 0;
                        }

                        mUpdatePointsInFirebase(fbPage.getUserID(), newPoints, fbPage.getPageID(), Consts.THAT_USER);

                        Transaction transaction = new Transaction();
                        transaction.setBalance(newPoints);
                        transaction.setDate(Utils.DateMonthyear(System.currentTimeMillis()));
                        transaction.setPlusOrMinus(Consts.MINUS);
                        transaction.setPoints(fbPage.getPoints());
                        transaction.setType(Consts.TRANSACTION_U_LIKE);
                        transaction.setMsg("A user liked your page");

                        mAddTransaction(fbPage.getUserID(), transaction);


                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private static void mAddTransaction(String userID, Transaction transaction) {

        TransactionsRef.getInstance(context, userID).push().setValue(transaction);

    }


    public static void getCurrentPointsAndSetInTextView(String userID) {

        Log.d("userID", userID);

        UsersRef.getUserByUserId(context, userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    int currentPoints = userProfile.getTotalPoints();

                    animatePointsText();
                    TV_points.setText(currentPoints + " Pts");

                    if (currentPoints<userProfile.getPointsPerLike()){
                        TV_points.setTextColor(Color.parseColor("#ff0000"));
                    }
                    else {
                        TV_points.setTextColor(Color.parseColor("#ffffff"));

                    }


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

    private static void mUpdatePointsInFirebase(String userID, int newPoints, String pageID, int userType) {

        UsersRef.getUserByUserId(context, userID).getRef().child(Consts.F_TOTAL_POINTS).setValue(newPoints);


        if (userType == Consts.THAT_USER) {
            PagesRef.getPageByPageId(context, pageID).child(Consts.F_USER_TOTAL_POINTS).setValue(newPoints);
        }
        else {

            if (prefManager.getLitedPageId()!=null) {
                PagesRef.getPageByPageId(context, prefManager.getLitedPageId()).child(Consts.F_USER_TOTAL_POINTS).setValue(newPoints);
            }
        }

    }

    public static void changeNavButton(int c) {

        if (c == Consts.DRAWER) {
            BTN_Nav.setImageResource(R.drawable.ic_menu_white_24dp);
            BTN_Nav.setVisibility(View.VISIBLE);
        } else if (c == Consts.BACK) {
            BTN_Nav.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            BTN_Nav.setVisibility(View.INVISIBLE);
        }


    }

    public static void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }



    private void mShowHowToLoginDialog() {

        HowItWorksDialog alert = new HowItWorksDialog();
        alert.showDialog(HomeActivity.this);


    }


}
