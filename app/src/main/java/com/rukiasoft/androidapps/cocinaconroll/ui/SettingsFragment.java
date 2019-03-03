package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.io.Serializable;


public class SettingsFragment extends PreferenceFragmentcom{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.options);

    }
}


