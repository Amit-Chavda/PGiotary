package com.example.dhruv.pg_accomodation;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "isLoggedIn";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(PREF_NAME, isLoggedIn);
        editor.commit();
    }

    public boolean isIsLoggedIn() {
        return pref.getBoolean(PREF_NAME, false);
    }
}