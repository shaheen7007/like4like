package com.shaheen.webviewtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.adapter.PagesGridAdapter;
import com.shaheen.webviewtest.databaseRef.PagesRef;
import com.shaheen.webviewtest.databaseRef.UserLikedPagesRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.utils.PrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    BottomSheet bottomSheet;
    static Context context;
    static FragmentManager fragmentManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_main_list, container, false);

        init(view);
        //getUserLikedPages();


        return view;
    }

    @Override
    public void onResume() {
        getUserLikedPages();
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

        context = getActivity();
        prefManager = PrefManager.getInstance(context);
        fragmentManager = getFragmentManager();
        gridView = view.findViewById(R.id.gridView);
        BTN_listMyPage = view.findViewById(R.id.btn_list_page);
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = new ProgressDialog(getActivity());
        progressBar.setMessage("Loading");
        progressBar.setCancelable(false);


        BTN_listMyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet = BottomSheet.newInstance();
                bottomSheet.show(getActivity().getSupportFragmentManager(),
                        "list page");
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
                // set an Intent to Another Activity
                /*Intent intent = new Intent(getActivity(), FbPageFragment.class);
                intent.putExtra("page", fbPageList.get(position));
                startActivity(intent);

                Toast.makeText(getActivity(), fbPageList.get(position).getPageID(), Toast.LENGTH_SHORT).show();
*/
                Bundle bundle = new Bundle();
                bundle.putParcelable("page", fbPageList.get(position));
                fbPageFragment.setArguments(bundle);
                ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, fbPageFragment, "fbpage");
                ft.setTransition(
                        FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        });


        progressBar.dismiss();

    }

    private static void getPagesList() {


        if (prefManager.isPageListed()) {
            BTN_listMyPage.setVisibility(View.GONE);
        } else {
            BTN_listMyPage.setVisibility(View.VISIBLE);
        }


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
}
