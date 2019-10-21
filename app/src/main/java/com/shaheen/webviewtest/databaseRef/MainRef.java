package com.shaheen.webviewtest.databaseRef;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaheen.webviewtest.utils.Consts;

public class MainRef {
    private static DatabaseReference mDatabase;

    public static DatabaseReference getInstance(Context context) {
        if (mDatabase==null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(Consts.MainRef).getRef();
        }
        return mDatabase;
    }


}
