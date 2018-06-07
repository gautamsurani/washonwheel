package com.washonwheel.android.Util;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager
{
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared pref file name
    private static final String PREF_NAME = "Login";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String Cust_email = "Cust_email";
    private static final String Cust_id = "Cust_id";

    // Email address
    private static final String Cust_Number ="Cust_Number";
    private static final String Cust_Name ="Cust_Name";
    private static final String Cust_age ="Cust_age";
    private static final String Cust_profile ="Cust_profile";
    private static final String Cust_prof ="Cust_prof";
    private static final String Cust_company ="Cust_compnay";
    private static final String Cust_gender ="Cust_gender";
    private static final String Cust_city ="Cust_city";
    private static final String Cust_coutry_flag = "Cust_flag";
    private static final String Cust_coutry_code = "Cust_code";

  // Constructor
    public PrefManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String id,String name,String email,String number,String profile,String gender,String age,String prof,String company,String city,String code ) {
        // Storing login value as TRUE

        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(Cust_email, email);
        editor.putString(Cust_Name, name);
        editor.putString(Cust_id, id);
        editor.putString(Cust_Number, number);
        editor.putString(Cust_profile,profile);
        editor.putString(Cust_age,age);
        editor.putString(Cust_prof, prof);
        editor.putString(Cust_company,company);
        editor.putString(Cust_city,city);
        editor.putString(Cust_gender,gender);
        editor.putString(Cust_coutry_code,code);
        // commit changes
        editor.commit();

    }


    public String getEmail() {
        return pref.getString(Cust_email, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
    public String getName()
    {
        return pref.getString(Cust_Name, null);
    }

    public String getCustId() {
        return pref.getString(Cust_id, null);
    }
    public String getCustNumber() {
        return pref.getString(Cust_Number, null);
    }
    public String getCustProfile() {
        return pref.getString(Cust_profile, null);
    }
    public String getCustAge() {
        return pref.getString(Cust_age, null);
    }
    public String getCustGender() {
        return pref.getString(Cust_gender, null);
    }
    public String getCustProf() {
        return pref.getString(Cust_prof, null);
    }
    public String getCustCompany() {
        return pref.getString(Cust_company, null);
    }
    public String getCustCity() {
        return pref.getString(Cust_city, null);
    }
    public String getCust_coutry_code() {
        return pref.getString(Cust_coutry_code, null);
    }


    public void logout()
    {
        editor.clear();
        editor.commit();
    }
}
