package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.RecipeQueries;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.RecipeShort;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeDetailed;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeTimestamp;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.storage.methods.StorageMethods;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import org.greenrobot.greendao.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iRoll on 7/2/17.
 */

public class DatabaseMethods {
    private final String TAG = LogHelper.makeLogTag(DatabaseMethods.class);

    public void updateOldRecipesToPersonalStorage(final Context context){
        ReadWriteTools readWriteTools = new ReadWriteTools();
        List<String> recipeItemNameList = readWriteTools.loadOldEditedAndOriginalRecipes(context);
        DatabaseMethods databaseMethods = new DatabaseMethods();
        databaseMethods.updateRecipesToPersonalStorage(context, recipeItemNameList);

    }

    public void updateRecipesToPersonalStorage(final Context context, final List<String> recipeList){

        Tools tools = new Tools();
        if(!tools.getBooleanFromPreferences(context, RecetasCookeoConstants.PROPERTY_CAN_UPLOAD_OWN_RECIPES)){
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null || user.isAnonymous()){
            return;
        }

        final ReadWriteTools readWriteTools = new ReadWriteTools();
        if(recipeList.isEmpty())    return;
        final RecipeItem recipe = readWriteTools.readRecipe(context, recipeList.get(0),
                RecetasCookeoConstants.PATH_TYPE_EDITED);
        if(recipe == null)  return;

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("/" + RecetasCookeoConstants.PERSONAL_RECIPES_NODE);


        //String key = ref.child(user.getUid()).push().getKey();
        //Si la receta está en base de datos, era una editada, no una nueva. Me quedo con la key
        String key;
        DaoSession session = ((CocinaConRollApplication)context).getDaoSession();
        Query queryRecipe = RecipeQueries.getQueryGetRecipeByName(session);
        RecipeShort recipeShort = (RecipeShort) queryRecipe.unique();
        if(recipeShort != null){
            key = recipeShort.getKey();
        }else{
            key = ref.child(user.getUid()).push().getKey();
        }

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
                    updateRecipesToPersonalStorage(context, recipeList);
                }
            }
        });
    }
}
