package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.Authentication;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.TimestampFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.storage.methods.StorageMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iRoll on 7/2/17.
 */

public class FirebaseDbMethods {
    private static boolean uploadingOld = false;
    private static boolean uploadingRegular = false;
    private RecipeController recipeController;

    public FirebaseDbMethods(RecipeController recipeController) {
        this.recipeController = recipeController;
    }



    // TODO: 22/2/17 llamar a este método cuando corresponda (antes se llamaba en el timer) 
    public void updateOldRecipesToPersonalStorage(Context context){
        if(uploadingOld){
            Logger.d("Estaba subiendo old recipes");
            return;
        }
        uploadingOld = true;
        ReadWriteTools readWriteTools = new ReadWriteTools();
        List<String> recipeItemNameList = readWriteTools.loadOldEditedAndOriginalRecipes();
        if(recipeItemNameList != null) {
            updateOldRecipesToPersonalStorage(context, recipeItemNameList);
        }
    }

    private void updateOldRecipesToPersonalStorage(final Context context, final List<String> recipeList){

        FirebaseUser user = Authentication.getCurrentUser();
        if(user == null || user.isAnonymous()){
            Logger.d("No puede subir recetas por el user");
            uploadingOld = false;
            return;
        }

        final ReadWriteTools readWriteTools = new ReadWriteTools();
        if(recipeList.isEmpty()) {
            Logger.d("No hay recetas que subir");
            uploadingOld = false;
            return;
        }

        //ref
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("/" + RecetasCookeoConstants.PERSONAL_RECIPES_NODE);
        //child para subir
        final Map<String, Object> childUpdates = new HashMap<>();

        for (String recipeFileName : recipeList) {
            RecipeItemOld recipeOld = readWriteTools.readRecipe(context, recipeFileName,
                    RecetasCookeoConstants.PATH_TYPE_EDITED);
            if (recipeOld == null) {
                Logger.d("La receta a guardar es null: " + recipeFileName);
                continue;
            }

            //Si la receta está en base de datos, era una editada, no una nueva. Me quedo con la key
            String key;

            RecipeDb recipeDb = recipeController.getRecipeByExactName((Application) context.getApplicationContext(),
                    recipeOld.getName());
            if (recipeDb != null) {
                Logger.d("La receta " + recipeDb.getName() + "tenía key " + recipeDb.getKey());
                key = recipeDb.getKey();
            } else {
                key = ref.child(user.getUid()).push().getKey();
                Logger.d("Para la receta " + recipeOld.getName() + "genero key " + key);
            }

            RecipeFirebase recipeFirebase = new RecipeFirebase(recipeOld);
            TimestampFirebase timestampFirebase = new TimestampFirebase();
            Map<String, Object> postDetailedValues = recipeFirebase.toMap();
            Map<String, Object> postTimestamp = timestampFirebase.toMap();

            childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + key, postDetailedValues);
            childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE + "/" + key, postTimestamp);
        }

        ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    return;
                }
                //Si ha ido bien, borro las recetas (saco primero las fotos)
                List<String> pictureNames = new ArrayList<>();
                for(String name : recipeList) {
                    RecipeItemOld recipeOld = readWriteTools.readRecipe(context, name,
                            RecetasCookeoConstants.PATH_TYPE_EDITED);
                    if (recipeOld != null && recipeOld.getPicture() != null && !recipeOld.getPicture().isEmpty()
                            && !recipeOld.getPicture().equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME)) {
                        pictureNames.add(recipeOld.getPicture());
                    }
                    String sbPath = readWriteTools.getEditedStorageDir() +
                            name;
                    readWriteTools.deleteFile(sbPath);
                }
                StorageMethods storageMethods = new StorageMethods();
                storageMethods.uploadOldPicturesToPersonalStorage(pictureNames);
            }
        });
    }

    private void updateRecipesToPersonalStorage(final Context context, final List<RecipeDb> recipeList){

        if(uploadingRegular){
            Logger.d("Estaba subiendo regular recipes");
            return;
        }

        FirebaseUser user = Authentication.getCurrentUser();
        if(user == null || user.isAnonymous()){
            Logger.d("No puede subir recetas por el user");
            uploadingRegular = false;
            return;
        }

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("/" + RecetasCookeoConstants.PERSONAL_RECIPES_NODE);
        final Map<String, Object> childUpdates = new HashMap<>();

        for (RecipeDb recipe : recipeList) {
            TimestampFirebase timestampFirebase = new TimestampFirebase();
            String key = recipe.getKey();
            RecipeFirebase recipeFirebase = new RecipeFirebase(recipe);
            Map<String, Object> postDetailedValues = recipeFirebase.toMap();
            Map<String, Object> postTimestamp = timestampFirebase.toMap();

            childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + key, postDetailedValues);
            childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE + "/" + key, postTimestamp);
        }

        ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError != null) {
                    Logger.d("Data could not be saved: " + databaseError.getMessage());
                    return;
                }

                for (RecipeDb recipe : recipeList) {
                    recipe.setUpdateRecipe(RecetasCookeoConstants.FLAG_NOT_UPDATE_RECIPE);
                    recipeController.insertOrReplaceRecipe((Application)context.getApplicationContext(), recipe);
                }
                StorageMethods storageMethods = new StorageMethods();
                storageMethods.uploadPendingPicturesToPersonalStorage(context);
            }
        });
    }

    public static Integer getRecipeFlagFromNodeName(String node){
        Integer flag;
        switch(node){
            case RecetasCookeoConstants.ALLOWED_RECIPES_NODE:
                flag = RecetasCookeoConstants.FLAG_ALLOWED_RECIPE;
                break;
            case RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE:
                flag = RecetasCookeoConstants.FLAG_FORBIDDEN_RECIPE;
                break;
            default:
                flag = RecetasCookeoConstants.FLAG_PERSONAL_RECIPE;
                break;
        }
        return flag;
    }

    public static String getNodeNameFromRecipeFlag(Integer flag){
        String node;
        switch(flag){
            case RecetasCookeoConstants.FLAG_ALLOWED_RECIPE:
                node = RecetasCookeoConstants.ALLOWED_RECIPES_NODE;
                break;
            case RecetasCookeoConstants.FLAG_FORBIDDEN_RECIPE:
                node = RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE;
                break;
            case RecetasCookeoConstants.FLAG_PERSONAL_RECIPE:
                node = RecetasCookeoConstants.PERSONAL_RECIPES_NODE;
                break;
            default:
                node = null;
                break;
        }
        return node;
    }
}
