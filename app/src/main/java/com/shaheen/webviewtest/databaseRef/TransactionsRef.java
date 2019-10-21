package com.shaheen.webviewtest.databaseRef;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.shaheen.webviewtest.utils.Consts;


public class TransactionsRef {
    public static DatabaseReference getInstance(Context context, String uid) {
        return UsersRef.getInstance(context).child(uid).child(Consts.TRANSACTIONS).getRef();
    }
}
