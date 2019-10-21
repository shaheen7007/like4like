package com.shaheen.webviewtest.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.model.FbPage;

import java.util.List;

public class TransactionsAdapter extends BaseAdapter {
        Context context;
        List<String>fbPageList;
        LayoutInflater inflter;

        public TransactionsAdapter(Context applicationContext, List<String>fbPageList) {
            this.context = applicationContext;
            this.fbPageList = fbPageList;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return fbPageList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.item_transactions, null); // inflate the layout
            TextView TV_transaction = (TextView) view.findViewById(R.id.tv); // get the reference of ImageView
          //  TextView TV_points =(TextView) view.findViewById(R.id.tv_points);

            TV_transaction.setText(fbPageList.get(i));

            if (fbPageList.get(i).contains("+")){
                TV_transaction.setTextColor(Color.parseColor("#ff3700"));
            }
            else {
                TV_transaction.setTextColor(Color.parseColor("#ff3700"));
            }


          //TV_points.setText(fbPageList.get(i).getPoints() +" Pts");
            return view;
        }






    }

