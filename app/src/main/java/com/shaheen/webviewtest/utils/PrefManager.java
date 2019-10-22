package com.shaheen.webviewtest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class PrefManager {
    // ===========================================================
    // Constants
    // ===========================================================


    private static final String TAG_NAME = "chronology.prefs";
    private static final String LISTED_PAGE_ID = "listed_page_id";
    private static final String IS_PAGE_LISTED = "is_page_listed";
    private static final String POINT_PER_LIKE = "points_per_like";


    // ===========================================================
    // Fields
    // ===========================================================

    private static PrefManager instance;

    private SharedPreferences prefs;

    // ===========================================================
    // Constructors
    // ===========================================================
    private PrefManager(Context context) {
        prefs = context.getSharedPreferences(TAG_NAME, Context.MODE_PRIVATE);
    }

    public static PrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrefManager(context);
        }
        return instance;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void putString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @SuppressWarnings("unused")
    private void putLong(String key, long value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    @SuppressWarnings("unused")
    private void putInt(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    @SuppressWarnings("unused")
    private void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @SuppressWarnings("unused")
    public SharedPreferences getSharedPrefs() {
        return prefs;
    }


    public void clearData() {
        prefs.edit().clear().apply();
    }

    public boolean isPageListed(){
        return prefs.getBoolean(IS_PAGE_LISTED, false);
    }

    public void setIsPageListed(boolean status){
        putBoolean(IS_PAGE_LISTED,status);
    }

    public String getLitedPageId(){
        return prefs.getString(LISTED_PAGE_ID, null);
    }

    public void setListedPageId(String id){
        putString(LISTED_PAGE_ID,id);
    }
    public int getPointPerLike(){
        return prefs.getInt(POINT_PER_LIKE, 10);
    }

    public void setPointPerLike(int point){
        putInt(POINT_PER_LIKE,point);
    }

   }