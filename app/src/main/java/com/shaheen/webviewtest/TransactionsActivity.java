package com.shaheen.webviewtest;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.shaheen.webviewtest.adapter.TransactionsAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    ListView RV_transactions;
    List<String> list;
    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        init();
    }

    private void init() {
        RV_transactions = (ListView) findViewById(R.id.list_transactions);

        list = new ArrayList<>();
        final TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getApplicationContext(), list);
        RV_transactions.setAdapter(transactionsAdapter);

        final Handler handler = new Handler();

        r = new Runnable() {
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

        handler.postDelayed(r, 5000);

    }
}
