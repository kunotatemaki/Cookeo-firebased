package com.rukiasoft.androidapps.cocinaconroll.permissions;

import android.util.Log;

import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;

/**
 * Created by iRoll on 28/1/17.
 */

public class ErrorListener  implements PermissionRequestErrorListener {
    private final String TAG = LogHelper.makeLogTag(this.getClass());
    @Override public void onError(DexterError error) {
        Log.e(TAG, "There was an error: " + error.toString());
    }
}
