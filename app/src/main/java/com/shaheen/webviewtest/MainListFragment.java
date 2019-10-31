package com.shaheen.webviewtest;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.activity.HomeActivity;
import com.shaheen.webviewtest.adapter.PagesGridAdapter;
import com.shaheen.webviewtest.databaseRef.PagesRef;
import com.shaheen.webviewtest.databaseRef.UserLikedPagesRef;
import com.shaheen.webviewtest.databaseRef.UsersRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.model.UserProfile;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.utils.HowItWorksDialog;
import com.shaheen.webviewtest.utils.PrefManager;
import com.shaheen.webviewtest.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import co.ceryle.fitgridview.FitGridView;


public class MainListFragment extends Fragment {

    static List<FbPage> fbPageList;
    static FitGridView gridView;
    static PrefManager prefManager;

    static FbPageFragment fbPageFragment;
    static ArrayList<String> userLikedPages;
    static FragmentTransaction ft;
    static FirebaseUser user;
    static ProgressDialog progressBar;
    static Button BTN_listMyPage;
    BottomSheetAdd bottomSheetAdd;
    BottomSheetEdit bottomSheetEdit;
    static Context context;
    static FragmentManager fragmentManager;
    int addOrEdit=Consts.ADD;
    AdView mAdView_banner;
    private static InterstitialAd mInterstitialAd;
    private static SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_main_list, container, false);

        init(view);
        setVisibilityOfButton();
        //getUserLikedPages();
        return view;
    }

    @Override
    public void onResume() {
        if (Utils.isInternetAvailable(getActivity())) {
            getUserLikedPages();
        }
        super.onResume();
    }

    static void getUserLikedPages() {
        progressBar.show();
        userLikedPages = new ArrayList<>();
        UserLikedPagesRef.getInstance(context, user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userLikedPages.add(String.valueOf(snapshot.getKey()));
                }
                getPagesList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void setVisibilityOfButton() {

        UsersRef.getUserByUserId(getActivity(),user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                if (userProfile.getListedPage()==null){
                    addOrEdit=Consts.ADD;
                    BTN_listMyPage.setText("List My Page");
                }
                else {
                    addOrEdit=Consts.EDIT;
                    BTN_listMyPage.setText("Edit Page Listing");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.points_menu, menu);
        ImageView locButton = (ImageView) menu.findItem(R.id.menuRefresh).getActionView();
        if (locButton != null) {
            locButton.setImageResource(R.drawable.ic_refresh_black_24dp);
         //   locButton.setScaleX(IMAGE_SCALE);
           // locButton.setScaleY(IMAGE_SCALE);
            locButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Animation rotation = AnimationUtils.loadAnimation(MainListFragment.this, R.anim.anim);
                    rotation.setRepeatCount(5);

                    view.startAnimation(rotation);
                    //adapter.updateData(QuotesProvider.getQuotesRandom(getActivity()));
                    //adapter.notifyDataSetChanged();
                    //layoutManager.scrollToPosition(0);
                }
            });
        }
        super.onCreateOptionsMenu(menu);
return true;
     }*/

    private void init(View view) {

        //change ad unit id - in layout



        //change ad unit id
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        HomeActivity.showToolbar();

        mAdView_banner = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView_banner.loadAd(adRequest);

        HomeActivity.changeNavButton(Consts.DRAWER);

        context = getActivity();


        prefManager = PrefManager.getInstance(context);

        if (!prefManager.getHowItWorksFlag()){

            mShowHowToLoginDialog();

        }

        fragmentManager = getFragmentManager();
        gridView = view.findViewById(R.id.gridView);
        BTN_listMyPage = view.findViewById(R.id.btn_list_page);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isInternetAvailable(getActivity())) {
                    getUserLikedPages();
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();


        progressBar = new ProgressDialog(getActivity());
        progressBar.setMessage("Loading");
        progressBar.setCancelable(false);


        BTN_listMyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isInternetAvailable(getActivity())) {

                    if (addOrEdit == Consts.EDIT) {
                        bottomSheetEdit = BottomSheetEdit.newInstance();
                        bottomSheetEdit.show(getActivity().getSupportFragmentManager(),
                                "edit page");
                    } else {
                        bottomSheetAdd = BottomSheetAdd.newInstance();
                        bottomSheetAdd.show(getActivity().getSupportFragmentManager(),
                                "add page");
                    }
                }
            }
        });

    }


    private static void showPagesInGridView() {

        fbPageFragment = new FbPageFragment();

        PagesGridAdapter pagesAdapter = new PagesGridAdapter(context, fbPageList);
        gridView.setAdapter(pagesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (Utils.isInternetAvailable(context)) {

                    prefManager.setCountForAd(prefManager.getCountForAd() + 1);
                    if (prefManager.getCountForAd() % 4 == 0) {

                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            prefManager.setCountForAd(prefManager.getCountForAd() - 1);
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }

                    } else {

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("page", fbPageList.get(position));
                        fbPageFragment.setArguments(bundle);
                        ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.container, fbPageFragment, "fbpage");
                        ft.setTransition(
                                FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                }
            }

        });


        progressBar.dismiss();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    private static void getPagesList() {

        fbPageList = new ArrayList<>();


        PagesRef.getInstance(context).orderByChild(Consts.POINTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FbPage fbPage = snapshot.getValue(FbPage.class);

                    if (!userLikedPages.contains(fbPage.getPageID()) && !(fbPage.getUserTotalPoints() < fbPage.getPoints())) {
                        fbPageList.add(fbPage);
                    }
                }

                Collections.reverse(fbPageList);
                showPagesInGridView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });










        /*//dummy data
        FbPage fbPage = new FbPage();
        fbPage.setPageID("btechtrls");
        fbPage.setUserID("1");
        fbPage.setPoints(10);
        fbPageList.add(fbPage);

        fbPage = new FbPage();
        fbPage.setPageID("aanavandiapp");
        fbPage.setUserID("2");
        fbPage.setPoints(15);
        fbPageList.add(fbPage);

        fbPage = new FbPage();
        fbPage.setPageID("vkprasanthTvpm");
        fbPage.setUserID("1");
        fbPage.setPoints(10);
        fbPageList.add(fbPage);


        fbPage = new FbPage();
        fbPage.setPageID("ShaneNigamOfficial");
        fbPage.setUserID("1");
        fbPage.setPoints(10);
        fbPageList.add(fbPage);

        fbPage = new FbPage();
        fbPage.setPageID("SushanthNilambur7");
        fbPage.setUserID("1");
        fbPage.setPoints(10);
        fbPageList.add(fbPage);

        fbPage = new FbPage();
        fbPage.setPageID("reyaursports");
        fbPage.setUserID("1");
        fbPage.setPoints(10);
        fbPageList.add(fbPage);*/


    }



    private void mShowHowToLoginDialog() {

        HowItWorksDialog alert = new HowItWorksDialog();
        alert.showDialog(getActivity());


    }


}


