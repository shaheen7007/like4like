package com.shaheen.webviewtest.databaseRef;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.shaheen.webviewtest.Consts;


public class UserLikedPagesRef {
    public static DatabaseReference getInstance(Context context, String uid) {
        return UsersRef.getInstance(context).child(uid).child(Consts.USER_LIKED_PAGES).getRef();
    }
}
