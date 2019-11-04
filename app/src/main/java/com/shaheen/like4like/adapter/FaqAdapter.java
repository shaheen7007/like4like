package com.shaheen.like4like.adapter;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaheen.like4like.R;
import com.shaheen.like4like.model.FAQ;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {
    Context context;
    List<FAQ> faqList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView TV_question, TV_answer;


        public MyViewHolder(View view) {
            super(view);
            TV_question = (TextView) view.findViewById(R.id.q);
            TV_answer = (TextView) view.findViewById(R.id.a);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.TV_question.setText(faqList.get(position).getQ());
        holder.TV_answer.setText(faqList.get(position).getA());

    }


    @Override
    public int getItemCount() {
        return faqList.size();
    }


    public FaqAdapter(Context applicationContext, List<FAQ> fbPageList) {
        this.context = applicationContext;
        this.faqList = fbPageList;
    }
}

