package com.shaheen.like4like.databaseRef;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.shaheen.like4like.utils.Consts;


public class PagesRef {
    public static DatabaseReference getInstance(Context context) {
        return MainRef.getInstance(context).child(Consts.PAGES);
    }

    public static DatabaseReference getPageByPageId(Context context,String page_id){
           return PagesRef.getInstance(context).child(page_id).getRef();
    }
}

