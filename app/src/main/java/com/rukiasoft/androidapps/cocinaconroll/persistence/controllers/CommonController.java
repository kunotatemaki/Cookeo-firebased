package com.rukiasoft.androidapps.cocinaconroll.persistence.controllers;

import android.app.Application;

import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;

/**
 * Created by iRoll on 11/2/17.
 */

public class CommonController {

    public static DaoSession getDaosessionFromApplication(Application application, String tableName){
        if(application instanceof CocinaConRollApplication){
            return ((CocinaConRollApplication)application).getDaoSession();
        }else{
            throw new RuntimeException("Error accessing to internal database -> table " + tableName);
        }
    }

}
