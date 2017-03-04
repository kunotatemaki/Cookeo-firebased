package com.rukiasoft.androidapps.cocinaconroll.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;

import com.karumi.dexter.PermissionToken;
import com.rukiasoft.androidapps.cocinaconroll.R;

/**
 * Created by iRoll on 28/1/17.
 */

public class PermissionMethods {

    private Activity activity;
    public PermissionMethods(Activity activity) {
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token, @StringRes int description) {
        new AlertDialog.Builder(activity).setTitle(R.string.permissions_title)
                .setMessage(description)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                /*.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.continuePermissionRequest();
                    }
                })*/
                .show();
    }

    /*public void showPermissionDenied(@StringRes int text) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);

        if(rootView != null){
            Snackbar.make(rootView, text, Snackbar.LENGTH_LONG)
                    .show();
        }
    }*/
}
