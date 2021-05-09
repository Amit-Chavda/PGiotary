package com.example.dhruv.pg_accomodation.helper_classes;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // shared pref mode
    private int PRIVATE_MODE = 0;

    private String callerID;
    private String callerName;
    private String recepientName;

    // Shared preferences file name
    private static final String PREF_NAME = "isLoggedIn";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getCallerName() {
        callerName=pref.getString("callerName","");
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
        editor.putString("callerName",callerName);
    }

    public String getRecepientName() {
        recepientName=pref.getString("recepientName","");
        return recepientName;
    }

    public void setRecepientName(String recepientName) {
        this.recepientName = recepientName;
        editor.putString("recepientName",recepientName);
    }

    public String getCallerID() {
        callerID=pref.getString("callerID","");
        return callerID;
    }

    public void setCallerID(String callerID) {
        this.callerID = callerID;
        editor.putString("callerID",callerID);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(PREF_NAME, isLoggedIn);
        editor.commit();
    }

    public boolean isIsLoggedIn() {
        return pref.getBoolean(PREF_NAME, false);
    }
}