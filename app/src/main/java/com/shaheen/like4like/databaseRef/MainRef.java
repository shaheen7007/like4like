package com.shaheen.like4like.databaseRef;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaheen.like4like.utils.Consts;

public class MainRef {
    private static DatabaseReference mDatabase;

    public static DatabaseReference getInstance(Context context) {
        if (mDatabase==null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(Consts.MainRef).getRef();
        }
        return mDatabase;
    }


}
