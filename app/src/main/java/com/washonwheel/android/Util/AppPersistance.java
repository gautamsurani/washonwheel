package com.washonwheel.android.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.washonwheel.android.R;

import java.util.Map;

public class AppPersistance<T> {
    public enum keys {
        USER_NAME, USER_ID, USER_NUMBER, USER_EMAIL, USER_ADDRESS, CITY, LANDMARK, USERIMAGE, PINCODE, DOB, MRG_ANVSRY,
        ModelType, BookService
    }

    private static AppPersistance mAppPersistance;
    private SharedPreferences sharedPreferences;

    public static AppPersistance start(Context context) {
        if (mAppPersistance == null) {
            mAppPersistance = new AppPersistance(context);
        }
        return mAppPersistance;
    }

    private AppPersistance(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.prefrence_file_name), Context.MODE_PRIVATE);
    }

    public Object get(Enum key) {
        Map<String, ?> all = sharedPreferences.getAll();
        return all.get(key.toString());
    }

    void save(Enum key, Object val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (val instanceof Integer) {
            editor.putInt(key.toString(), (Integer) val);
        } else if (val instanceof String) {
            editor.putString(key.toString(), String.valueOf(val));
        } else if (val instanceof Float) {
            editor.putFloat(key.toString(), (Float) val);
        } else if (val instanceof Long) {
            editor.putLong(key.toString(), (Long) val);
        } else if (val instanceof Boolean) {
            editor.putBoolean(key.toString(), (Boolean) val);
        }
        editor.apply();
    }

    void remove(Enum key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key.toString());
        editor.apply();
    }
}
