package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeDetailed;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeTimestamp;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.storage.methods.StorageMethods;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iRoll on 7/2/17.
 */

public class DatabaseMethods {
    private final String TAG = LogHelper.makeLogTag(DatabaseMethods.class);

    public void updateRecipeToPersonalStorage(final Context context, final List<String> recipeList){

        final ReadWriteTools readWriteTools = new ReadWriteTools();
        if(recipeList.isEmpty())    return;
        final RecipeItem recipe = readWriteTools.readRecipe(context, recipeList.get(0),
                RecetasCookeoConstants.PATH_TYPE_EDITED);
        if(recipe == null)  return;

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("/" + RecetasCookeoConstants.PERSONAL_RECIPES_NODE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null || user.isAnonymous()){
            return;
        }

        String key = ref.child(user.getUid()).push().getKey();

        RecipeDetailed recipeDetailed = new RecipeDetailed(recipe);
        RecipeTimestamp recipeTimestamp = new RecipeTimestamp();

        Map<String, Object> postDetailedValues = recipeDetailed.toMap();
        Map<String, Object> postTimestamp = recipeTimestamp.toMap();

        final Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + key, postDetailedValues);
        childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE + "/" + key, postTimestamp);

        ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    return;
                }
                StringBuilder sbPath = new StringBuilder(100);
                sbPath.append(readWriteTools.getEditedStorageDir());
                sbPath.append(recipeList.get(0));
                // TODO: 7/2/17 descomentar lo de borrar receta cuando verifique que el proceso funciona 
                //readWriteTools.deleteFile(sbPath.toString());
                if(recipe.getPicture() != null && !recipe.getPicture().isEmpty()
                        && !recipe.getPicture().equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME)) {
                    StorageMethods storageMethods = new StorageMethods();
                    storageMethods.updatePictureToPersonalStorage(recipe);
                }
                recipeList.remove(0);
                if(!recipeList.isEmpty()){
                    updateRecipeToPersonalStorage(context, recipeList);
                }
            }
        });
    }
}
