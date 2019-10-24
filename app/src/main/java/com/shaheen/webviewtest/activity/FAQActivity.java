package com.shaheen.webviewtest.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.adapter.FaqAdapter;
import com.shaheen.webviewtest.adapter.TransactionsAdapter;
import com.shaheen.webviewtest.databaseRef.FAQRef;
import com.shaheen.webviewtest.databaseRef.TransactionsRef;
import com.shaheen.webviewtest.model.FAQ;
import com.shaheen.webviewtest.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    RecyclerView RV_faq;
    List<FAQ> list;
    Runnable r;
    FirebaseUser user;
    FaqAdapter faqAdapter;
    ProgressDialog dialog;
    ImageView BTN_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        init();
    }

    private void init() {
        RV_faq = (RecyclerView) findViewById(R.id.list_faq);
        BTN_back = findViewById(R.id.nav_btn);

        BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FAQActivity.super.onBackPressed();
            }
        });


        dialog = new ProgressDialog(this);

        list = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        RV_faq.setLayoutManager(mLayoutManager);
        RV_faq.setLayoutManager(mLayoutManager);
        faqAdapter = new FaqAdapter(getApplicationContext(), list);
        RV_faq.setAdapter(faqAdapter);

        final Handler handler = new Handler();

        user = FirebaseAuth.getInstance().getCurrentUser();


        mGetFAQ(user.getUid());


    }


    private void mGetFAQ(String uid) {

        showProgressDialog();
        FAQRef.getInstance(FAQActivity.this, uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
