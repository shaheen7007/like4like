package com.shaheen.webviewtest.databaseRef;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.shaheen.webviewtest.utils.Consts;


public class FAQRef {
    public static DatabaseReference getInstance(Context context) {
        return MainRef.getInstance(context).child(Consts.FAQ).getRef();
    }
}
