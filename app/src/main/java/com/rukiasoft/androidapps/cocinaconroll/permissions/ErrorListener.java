package com.rukiasoft.androidapps.cocinaconroll.permissions;

import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.orhanobut.logger.Logger;

/**
 * Created by iRoll on 28/1/17.
 */

public class ErrorListener  implements PermissionRequestErrorListener {
    @Override public void onError(DexterError error) {
        Logger.e("There was an error: " + error.toString());
    }
}
