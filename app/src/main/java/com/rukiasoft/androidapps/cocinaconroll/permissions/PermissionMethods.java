package com.rukiasoft.androidapps.cocinaconroll.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.karumi.dexter.PermissionToken;
import com.rukiasoft.androidapps.cocinaconroll.R;

/**
 * Created by iRoll on 28/1/17.
 */

class PermissionMethods {

    private Activity activity;
    PermissionMethods(Activity _activity) {
        this.activity = _activity;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    void showPermissionRationale(final PermissionToken token, @StringRes int description) {
        new AlertDialog.Builder(activity).setTitle(R.string.permissions_title)
                .setMessage(description)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .show();
    }
}
