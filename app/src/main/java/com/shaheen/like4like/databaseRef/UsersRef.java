package com.shaheen.like4like.databaseRef;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.shaheen.like4like.utils.Consts;


public class UsersRef {
    public static DatabaseReference getInstance(Context context) {
        return MainRef.getInstance(context).child(Consts.USERS).getRef();
    }

    public static DatabaseReference getUserByUserId(Context context,String user_id){
        return UsersRef.getInstance(context).child(user_id).getRef();
    }
}

