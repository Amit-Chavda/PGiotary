package com.example.dhruv.pg_accomodation;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtility {

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public static boolean isValidateCity(String city){
        if(city.isEmpty()){
            return false;
        }
        return true;
    }
    public static boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");
        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
    }

    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isValidUsername(String name) {
        String regex = "^[A-Za-z]\\w{3,29}$";

        Pattern p = Pattern.compile(regex);
        if (name == null) {
            return false;
        }

        Matcher m = p.matcher(name);
        return m.matches();
    }
}
