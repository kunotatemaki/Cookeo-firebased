package com.rukiasoft.androidapps.cocinaconroll;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoMaster;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


/**
 * Created by Ruler on 2015.
 *
 */

public class CocinaConRollApplication  extends MultiDexApplication {
    /**
     * The Analytics singleton. The field is set in onCreate method override when the application
     * class is initially created.
     */
    private GoogleAnalytics analytics;

    /**
     * The default app tracker. The field is from onCreate callback when the application is
     * initially created.
     */
    private Tracker tracker;
    private DaoSession daoSession;


    public static RefWatcher getRefWatcher(Context context) {
        CocinaConRollApplication application = (CocinaConRollApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);
        refWatcher = LeakCanary.install(this);

        //analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        tracker = analytics.newTracker(R.xml.track_app);

        // Provide unhandled exceptions reports. Do that first after creating the tracker
        /*if(BuildConfig.DEBUG) {
            tracker.enableExceptionReporting(false);
        }else{
            tracker.enableExceptionReporting(true);
        }*/
        tracker.enableExceptionReporting(false);

        // Enable Remarketing, Demographics & Interests reports
        // https://developers.google.com/analytics/devguides/collection/android/display-features
        tracker.enableAdvertisingIdCollection(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);
         /*Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                tracker,                                        // Currently used Tracker.
                Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                this);                                         // Context of the application.

// Make myHandler the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);*/

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lepetitcuquichef.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        //Para hacer debug de las querys de la base de datos
        /*if(BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }*/
        Logger
                .init(RecetasCookeoConstants.RUKIA_TAG)                 // default PRETTYLOGGER or use just init()
                //.methodCount(3)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.NONE)        // default LogLevel.FULL
                //.methodOffset(2)                // default 0
                //.logAdapter(new AndroidLogAdapter()) //default AndroidLogAdapter
        ;
    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //if(BuildConfig.DEBUG) {
            MultiDex.install(this);
        //}
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
