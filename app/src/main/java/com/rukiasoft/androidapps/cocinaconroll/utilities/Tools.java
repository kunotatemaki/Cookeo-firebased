package com.rukiasoft.androidapps.cocinaconroll.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.rukiasoft.androidapps.cocinaconroll.R;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Raúl Feliz Alonso on 21/09/2015 for the Udacity Nanodegree.
 */
public class Tools {

    public Tools(){
    }

    /**
     *
     * @param context context of the application
     * @return true if has vibrator, false otherwise
     */
    @SuppressLint("NewApi")
    public Boolean hasVibrator(Context context) {

        String vs = Context.VIBRATOR_SERVICE;
        Vibrator mVibrator = (Vibrator) context.getSystemService(vs);
        return mVibrator.hasVibrator();

    }

    /**
     * get the application name
     */
    public String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public void setScreenOnIfSettingsAllowed(Activity activity, Boolean state){
        if(state && getBooleanFromPreferences(activity, "option_screen_on"))
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public Boolean getBooleanFromPreferences(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(name, false);
    }


    public Integer getIntegerFromPreferences(Context context, String name) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(name, Integer.MIN_VALUE);

    }

    public Long getLongFromPreferences(Context context, String name) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(name, Long.MIN_VALUE);

    }

    public void savePreferences(Context context, String name, String value) {

        //SharedPreferences preferences = context.getSharedPreferences("sacarino",Context.MODE_PRIVATE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString(name, value);
        ed.apply();

    }

    public void savePreferences(Context context, String name, int value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt(name, value);
        ed.apply();

    }

    public void savePreferences(Context context, String name, long value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putLong(name, value);
        ed.apply();

    }

    public void savePreferences(Context context, String name, Double value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putFloat(name, value.floatValue());
        ed.apply();

    }

    public void savePreferences(Context context, String name, Boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean(name, value);
        ed.apply();

    }

    public String getCurrentDate(Context context) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(RecetasCookeoConstants.FORMAT_DATE_TIME,
                context.getResources().getConfiguration().locale);
        return df.format(c.getTime());
    }

    public void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getStringFromPreferences(Context context, String name) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(name, "");

    }

    public boolean isKeyboardShown(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        return imm.isAcceptingText();
    }

    public String getNormalizedString(String input){
        if(input == null){
            return "";
        }
        String normalized;
        normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = normalized.replaceAll("[^\\p{ASCII}]", "");
        input = input.trim();
        return input.toLowerCase();
    }

    public static int getIconFromType(String type){
        int icon;
        switch (type) {
            case RecetasCookeoConstants.TYPE_DESSERTS:
                icon = R.drawable.ic_dessert_18;
                break;
            case RecetasCookeoConstants.TYPE_STARTERS:
                icon = R.drawable.ic_starters_18;
                break;
            case RecetasCookeoConstants.TYPE_MAIN:
                icon = R.drawable.ic_main_18;
                break;
            default:
                icon = R.drawable.ic_all_18;
                break;
        }
        return icon;
    }

}
