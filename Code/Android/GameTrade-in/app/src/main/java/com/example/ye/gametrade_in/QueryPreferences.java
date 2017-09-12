package com.example.ye.gametrade_in;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ye on 2017/9/10.
 */

public class QueryPreferences {

    private static final String PREF_AUTHORIZED_QUERY = "authorized";

    private static final String PREF_USERID_QUERY = "userId";

    private static final String PREF_USERNAME_QUERY = "username";

    private static final String PREF_PASSWORD_QUERY = "password";

    public static String getStoredUserIdQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USERID_QUERY, null);
    }

    public static String getStoredAuthorizedQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_AUTHORIZED_QUERY, null);
    }

    public static void setStoredQuery(Context context, String userId, String authorizedHeader){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_AUTHORIZED_QUERY, authorizedHeader)
                .putString(PREF_USERID_QUERY, userId)
                .apply();
    }

}
