package com.shaheen.webviewtest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.shaheen.webviewtest.adapter.PagesGridAdapter;
import com.shaheen.webviewtest.model.FbPage;

import java.util.ArrayList;
import java.util.List;

import co.ceryle.fitgridview.FitGridView;


public class MainListFragment extends Fragment {

    List<FbPage> fbPageList;
    FitGridView gridView;

    FbPageFragment fbPageFragment;
    FragmentTransaction ft;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.activity_main_list, container, false);

        init(view);
        getPagesList();
        showPagesInGridView();









        return view;
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

        gridView = view.findViewById(R.id.gridView);



    }



    private void showPagesInGridView() {

        fbPageFragment = new FbPageFragment();

        PagesGridAdapter pagesAdapter = new PagesGridAdapter(getActivity(), fbPageList);
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
                Bundle bundle=new Bundle();
                bundle.putParcelable("page", fbPageList.get(position));
                fbPageFragment.setArguments(bundle);
                ft = getFragmentManager().beginTransaction();
                ft.add(R.id.container, fbPageFragment, "fbpage");
                ft.setTransition(
                        FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("mainlist");
                ft.commit();
            }

        });



    }

    private void getPagesList() {

        fbPageList = new ArrayList<>();

        //dummy data
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
        fbPageList.add(fbPage);


    }
}
