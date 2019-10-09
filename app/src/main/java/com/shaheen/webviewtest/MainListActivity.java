package com.shaheen.webviewtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.shaheen.webviewtest.adapter.PagesGridAdapter;
import com.shaheen.webviewtest.model.FbPage;

import java.util.ArrayList;
import java.util.List;

import co.ceryle.fitgridview.FitGridView;

public class MainListActivity extends AppCompatActivity {

    List<FbPage> fbPageList;
    FitGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        init();

        getPagesList();
        showPagesInGridView();

    }

    private void init() {

        gridView = (FitGridView) findViewById(R.id.gridView);


    }

    private void showPagesInGridView() {

        PagesGridAdapter pagesAdapter = new PagesGridAdapter(getApplicationContext(), fbPageList);
        gridView.setAdapter(pagesAdapter);
        // implement setOnItemClickListener event on GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity
                Intent intent = new Intent(MainListActivity.this, MainActivity.class);
                intent.putExtra("page", fbPageList.get(position)); // put image data in Intent
                startActivity(intent); // start Intent

                Toast.makeText(MainListActivity.this, fbPageList.get(position).getPageID(), Toast.LENGTH_SHORT).show();
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
