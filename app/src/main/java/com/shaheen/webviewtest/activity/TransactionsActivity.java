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
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.adapter.TransactionsAdapter;
import com.shaheen.webviewtest.databaseRef.TransactionsRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.model.Transaction;
import com.shaheen.webviewtest.utils.Consts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    RecyclerView RV_transactions;
    List<Transaction> list;
    Runnable r;
    FirebaseUser user;
    TransactionsAdapter transactionsAdapter;
    ProgressDialog dialog;
    ImageView BTN_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        init();
    }

    private void init() {
        RV_transactions = (RecyclerView) findViewById(R.id.list_transactions);
        BTN_back = findViewById(R.id.nav_btn);

        BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionsActivity.super.onBackPressed();
            }
        });


        dialog = new ProgressDialog(this);

        list = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        RV_transactions.setLayoutManager(mLayoutManager);
        RV_transactions.setLayoutManager(mLayoutManager);
        transactionsAdapter = new TransactionsAdapter(getApplicationContext(), list);
        RV_transactions.setAdapter(transactionsAdapter);

        final Handler handler = new Handler();

        user = FirebaseAuth.getInstance().getCurrentUser();


        mGetUserTransactions(user.getUid());






       /* r = new Runnable() {
            public void run() {

                handler.postDelayed(r, 5000);

                list.add(0, String.valueOf(System.currentTimeMillis()));


                int index = RV_transactions.getFirstVisiblePosition(); //This changed
                View v = RV_transactions.getChildAt(list.size());
                int top = (v == null) ? list.size() : v.getBottom(); //this changed

                transactionsAdapter.notifyDataSetChanged();

// restore the position of listview
                RV_transactions.setSelectionFromTop(index, top);

            }
        };

        handler.postDelayed(r, 5000);*/

    }

    private void mGetUserTransactions(String uid) {

        showProgressDialog();
        TransactionsRef.getInstance(TransactionsActivity.this, uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Transaction transaction = snapshot.getValue(Transaction.class);

                    list.add(transaction);
                }
              /*  int index = RV_transactions.getFirstVisiblePosition(); //This changed
                View v = RV_transactions.getChildAt(list.size());
                int top = (v == null) ? list.size() : v.getBottom(); //this changed
*/
                Collections.reverse(list);

                transactionsAdapter.notifyDataSetChanged();
                hideProgressDialog();

// restore the position of listview
                //   RV_transactions.setSelectionFromTop(index, top);

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
