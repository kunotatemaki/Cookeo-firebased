package com.rukiasoft.androidapps.cocinaconroll.permissions;


import android.app.Activity;
import android.content.Context;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.util.List;

/**
 * Created by iRoll on 28/1/17.
 */

public class RecetasCookeoMultiplePermissionListener implements MultiplePermissionsListener {

    private final PermissionMethods permissionMethods;
    private Context context;

    public RecetasCookeoMultiplePermissionListener(Activity _activity) {
        permissionMethods = new PermissionMethods(_activity);
        context = _activity.getApplicationContext();
    }

    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
        for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
            if(response.getRequestedPermission().getName().equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Tools tools = new Tools();

                tools.savePreferences(context, RecetasCookeoConstants.PROPERTY_CAN_UPLOAD_OWN_RECIPES, true);
                RecipeController recipeController = new RecipeController();
                FirebaseDbMethods firebaseDbMethods = new FirebaseDbMethods(recipeController);
                firebaseDbMethods.updateOldRecipesToPersonalStorage(context);
            }
        }
    }

    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                             PermissionToken token) {
        permissionMethods.showPermissionRationale(token, R.string.write_external_explanation);
    }
}