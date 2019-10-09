package com.shaheen.webviewtest.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.model.FbPage;

import java.util.List;

public class PagesGridAdapter extends BaseAdapter {
        Context context;
        List<FbPage>fbPageList;
        LayoutInflater inflter;

        public PagesGridAdapter(Context applicationContext, List<FbPage>fbPageList) {
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
            view = inflter.inflate(R.layout.item_pagegrid, null); // inflate the layout
            TextView TV_pageName = (TextView) view.findViewById(R.id.tv_pageid); // get the reference of ImageView
            TextView TV_points =(TextView) view.findViewById(R.id.tv_points);

            TV_pageName.setText(fbPageList.get(i).getPageID());
          TV_points.setText(fbPageList.get(i).getPoints() +" Pts");
            return view;
        }
    }

