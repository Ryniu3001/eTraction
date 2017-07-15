package pl.poznan.put.etraction.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import pl.poznan.put.etraction.R;

/**
 * Created by Marcin on 15.07.2017.
 */

public class PrefUtility {

    public static void changeNickName(Context context, String nickname){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getResources().getString(R.string.pref_nickname_key), nickname);
        editor.commit();
    }

    public static String getNickName(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String name = prefs.getString(context.getString(R.string.pref_nickname_key), "");
        return name;
    }
}
