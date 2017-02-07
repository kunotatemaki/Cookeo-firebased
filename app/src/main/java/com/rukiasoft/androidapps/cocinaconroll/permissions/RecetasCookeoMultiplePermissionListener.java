package com.rukiasoft.androidapps.cocinaconroll.permissions;


import android.content.Context;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.DatabaseMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeTimestamp;
import com.rukiasoft.androidapps.cocinaconroll.ui.RecipeListActivity;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;

import java.util.List;

/**
 * Created by iRoll on 28/1/17.
 */

public class RecetasCookeoMultiplePermissionListener implements MultiplePermissionsListener {

    private final String TAG = LogHelper.makeLogTag(RecetasCookeoMultiplePermissionListener.class);
    private final PermissionMethods permissionMethods;
    private Context context;

    public RecetasCookeoMultiplePermissionListener(RecipeListActivity activity) {
        permissionMethods = new PermissionMethods(activity);
        context = activity.getApplicationContext();
    }

    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
        for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
            if(response.getRequestedPermission().getName().equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                // TODO: 28/1/17 Aquí es donde leo las recetas antiguas
                ReadWriteTools readWriteTools = new ReadWriteTools();
                List<RecipeItem> recipeItemList = readWriteTools.loadOldEditedAndOriginalRecipes(context);
                DatabaseMethods databaseMethods = new DatabaseMethods();
                for(RecipeItem recipeItem : recipeItemList) {
                    databaseMethods.updateRecipeToPersonalStorage(recipeItem);
                }
            }
        }

        /*for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
            if(response.getRequestedPermission().getName().equals(android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    response.getRequestedPermission().getName().equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                permissionMethods.showPermissionDenied(R.string.write_external_denied);
            }

        }*/
    }

    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                             PermissionToken token) {
        permissionMethods.showPermissionRationale(token, R.string.write_external_explanation);
    }
}