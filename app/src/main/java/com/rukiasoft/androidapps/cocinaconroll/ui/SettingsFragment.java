package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.io.Serializable;


@SuppressLint("NewApi")
public class SettingsFragment extends PreferenceFragment implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Tools mTools = new Tools();
        if (mTools.hasVibrator(getActivity().getApplicationContext()))
            addPreferencesFromResource(R.xml.options);
        else
            addPreferencesFromResource(R.xml.options_not_vibrate);
    }
}


