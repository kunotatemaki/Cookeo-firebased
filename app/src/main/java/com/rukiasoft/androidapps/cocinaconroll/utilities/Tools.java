package com.rukiasoft.androidapps.cocinaconroll.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rukiasoft.androidapps.cocinaconroll.wifi.WifiHandler;


import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Raúl Feliz Alonso on 21/09/2015 for the Udacity Nanodegree.
 */
public class Tools {

    public Tools(){
    }

    public Long getTimeframe(){
        try {
            Long miliseconds = RecetasCookeoConstants.TIMEFRAME_MILI_SECONDS_DAY * RecetasCookeoConstants.TIMEFRAME_NEW_RECIPE_DAYS;
            return System.currentTimeMillis() - miliseconds;
        }catch(Exception e){
            e.printStackTrace();
            return Long.MAX_VALUE;
        }
    }

    /**
     *
     * @param context context of the application
     * @return true if has vibrator, false otherwise
     */
    @SuppressLint("NewApi")
    public Boolean hasVibrator(Context context) {

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            return true;
        else{
            String vs = Context.VIBRATOR_SERVICE;
            Vibrator mVibrator = (Vibrator) context.getSystemService(vs);
            return mVibrator.hasVibrator();
        }
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


    public boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
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


    public boolean hasPermissionForDownloading(Context context) {
       Boolean downloadWithWifi = getBooleanFromPreferences(context, "option_update_wifi");
            return !(downloadWithWifi && !WifiHandler.IsWifiConnected(context));
    }

    public String getStringFromPreferences(Context context, String name) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(name, "");

    }

    public String getJsonString(Object object) {
        // Before converting to GSON check value of id
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(object);
    }

    public int getAppVersion(Application application) {
        try {
            PackageInfo packageInfo = application.getPackageManager()
                    .getPackageInfo(application.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO: 14/1/17 aquí mandaba excepción con ACRA
            return 0;
        }
    }



    public boolean isKeyboardShown(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        return imm.isAcceptingText();
    }

    public String getNormalizedString(String input){
        String normalized;
        normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = normalized.replaceAll("[^\\p{ASCII}]", "");
        input = input.trim();
        return input.toLowerCase();
    }


}
