package com.shaheen.webviewtest.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.databaseRef.TransactionsRef;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.FbPageFragment;
import com.shaheen.webviewtest.MainListFragment;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.databaseRef.PagesRef;
import com.shaheen.webviewtest.databaseRef.UsersRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.model.UserProfile;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    FrameLayout container;
    FragmentTransaction fragmentTransaction;
    MainListFragment mainListFragment;
    static TextView TV_points;
    static Context context;
    FirebaseUser user;
    static String userID = null;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    ImageView BTN_Nav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        // load mainList fragment
        loadFragment(mainListFragment);
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList){
                if(fragment instanceof FbPageFragment){
                   loadFragment(mainListFragment);
                }
                else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void init() {

        context = this;
        mainListFragment = new MainListFragment();
        BTN_Nav=findViewById(R.id.nav_btn);
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

                dl.openDrawer(Gravity.LEFT);
            }
        });


        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();


        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                        Toast.makeText(HomeActivity.this, "My Account",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(HomeActivity.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.mycart:
                        Toast.makeText(HomeActivity.this, "My Cart",Toast.LENGTH_SHORT).show();break;
                    default:
                        return true;
                }


                return true;

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
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

    public static void updatePoint(final FbPage fbPage,int userType) {

        if (userType==Consts.THIS_USER) {

            UsersRef.getUserByUserId(context, userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int currentPoints = userProfile.getTotalPoints();
                        int newPoints = currentPoints + fbPage.getPoints();

                        mUpdatePointsInFirebase(userID, newPoints, fbPage.getPageID(), Consts.THIS_USER);  // the user logged in this device
                        mAddTransaction(userID,"You liked a page : +"+fbPage.getPoints()+" Pts");


                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            UsersRef.getUserByUserId(context, fbPage.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        //   for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int currentPoints = userProfile.getTotalPoints();
                        int newPoints = currentPoints - userProfile.getPointsPerLike();

                        if (newPoints<0 ){
                            newPoints=0;
                        }

                        mUpdatePointsInFirebase(fbPage.getUserID(), newPoints, fbPage.getPageID(), Consts.THAT_USER);

                        mAddTransaction(userID,"A user liked your page : -"+fbPage.getPoints()+" Pts");


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

    private static void mAddTransaction(String userID, String msg) {

        TransactionsRef.getInstance(context,userID).push().setValue(msg);

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


        if (userType==Consts.THAT_USER) {
            PagesRef.getPageByPageId(context,pageID).child(Consts.F_USER_TOTAL_POINTS).setValue(newPoints);
        }

    }
}
