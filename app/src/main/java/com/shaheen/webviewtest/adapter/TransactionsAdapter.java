package com.shaheen.webviewtest.adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.model.Transaction;
import com.shaheen.webviewtest.utils.Consts;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {
    Context context;
    List<Transaction> transactionList;
    LayoutInflater inflter;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView TV_date,TV_msg,TV_pts,TV_balance;
        ImageView IMG_pts;

        public MyViewHolder(View view) {
            super(view);
            TV_date = (TextView) view.findViewById(R.id.date);
            TV_msg = (TextView) view.findViewById(R.id.msg);
            TV_pts = (TextView) view.findViewById(R.id.tv_pts);
            TV_balance = (TextView) view.findViewById(R.id.balance);
            IMG_pts=view.findViewById(R.id.img_pts);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transactions, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (transactionList.get(position).getPlusOrMinus()== Consts.PLUS) {
            holder.IMG_pts.setBackgroundResource(R.drawable.ic_arrow_upward_black_24dp);
        } else if (transactionList.get(position).getPlusOrMinus()== Consts.MINUS){
            holder.IMG_pts.setBackgroundResource(R.drawable.ic_arrow_downward_black_24dp);
        }
        holder.TV_balance.setText("Balance: "+transactionList.get(position).getBalance()+" Pts");
        holder.TV_pts.setText(transactionList.get(position).getPoints()+" Pts");
        holder.TV_date.setText(transactionList.get(position).getDate());
        holder.TV_msg.setText(transactionList.get(position).getMsg());


    }


    @Override
    public int getItemCount() {
        return transactionList.size();
    }


    public TransactionsAdapter(Context applicationContext, List<Transaction> fbPageList) {
        this.context = applicationContext;
        this.transactionList = fbPageList;
    }
}

