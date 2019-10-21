package com.shaheen.webviewtest.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    ListView RV_transactions;
    List<String> list;
    Runnable r;
    FirebaseUser user;
    TransactionsAdapter transactionsAdapter;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        init();
    }

    private void init() {
        RV_transactions = (ListView) findViewById(R.id.list_transactions);
        dialog = new ProgressDialog(this);

        list = new ArrayList<>();
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
        list.clear();
        showProgressDialog();
        TransactionsRef.getInstance(TransactionsActivity.this,uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    list.add(snapshot.getValue().toString());
                }
                int index = RV_transactions.getFirstVisiblePosition(); //This changed
                View v = RV_transactions.getChildAt(list.size());
                int top = (v == null) ? list.size() : v.getBottom(); //this changed

                transactionsAdapter.notifyDataSetChanged();
                hideProgressDialog();

// restore the position of listview
                RV_transactions.setSelectionFromTop(index, top);

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
