package fr.epicture.epicture.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import fr.epicture.epicture.api.flickr.modele.TokenAccess;

public class Preferences {

    private final static String ACCESS_TOKEN = "ACCESS_TOKEN";

    @NonNull
    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setAccessToken(Context context, TokenAccess token) {
        SharedPreferences prefs = getSharedPreferences(context);
        if (token == null) {
            prefs.edit().remove(ACCESS_TOKEN).apply();
        } else {
            prefs.edit().putString(ACCESS_TOKEN, token.fullname + ";"
                    + token.token + ";"
                    + token.tokenSecret + ";"
                    + token.nsid + ";"
                    + token.username).apply();
        }
    }

    @Nullable
    public static TokenAccess getAccessToken(Context context) {
        TokenAccess token = null;
        SharedPreferences prefs = getSharedPreferences(context);
        try {
            String[] datas = prefs.getString(ACCESS_TOKEN, "").split(";");
            if (datas.length == 5) {
                token = new TokenAccess();
                token.fullname = datas[0];
                token.token = datas[1];
                token.tokenSecret = datas[2];
                token.nsid = datas[3];
                token.username = datas[4];
            }
        } catch (Exception e) {
            setAccessToken(context, null);
        }
        return (token);
    }

}
