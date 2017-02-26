package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.TimestampFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.storage.methods.StorageMethods;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iRoll on 7/2/17.
 */

public class FirebaseDbMethods {
    private final String TAG = LogHelper.makeLogTag(FirebaseDbMethods.class);
    static boolean uploading = false;
    private RecipeController recipeController;

    public FirebaseDbMethods(RecipeController recipeController) {
        this.recipeController = recipeController;
    }

    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // TODO: 22/2/17 llamar a este método cuando corresponda (antes se llamaba en el timer) 
    public void updateOldRecipesToPersonalStorage(final Context context){
        if(uploading == true){
            Log.d(TAG, "Estaba descargando");
            return;
        }
        uploading = !uploading;
        ReadWriteTools readWriteTools = new ReadWriteTools();
        List<String> recipeItemNameList = readWriteTools.loadOldEditedAndOriginalRecipes(context);
        //Log.d(TAG, "numero recetas: " + recipeItemNameList.sizePicture());
        if(recipeItemNameList != null) {
            updateRecipesToPersonalStorage(context, recipeItemNameList);
        }

    }

    public void updateRecipesToPersonalStorage(final Context context, final List<String> recipeList){

        Tools tools = new Tools();
        if(!tools.getBooleanFromPreferences(context, RecetasCookeoConstants.PROPERTY_CAN_UPLOAD_OWN_RECIPES)){
            Log.d(TAG, "No puede subir recetas por el flag");
            uploading = false;
            return;
        }
        FirebaseUser user = getCurrentUser();
        if(user == null || user.isAnonymous()){
            Log.d(TAG, "No puede subir recetas por el user");
            uploading = false;
            return;
        }

        final ReadWriteTools readWriteTools = new ReadWriteTools();
        if(recipeList.isEmpty()) {
            Log.d(TAG, "No hay recetas que subir");
            uploading = false;
            return;
        }
        final RecipeItemOld recipeOld = readWriteTools.readRecipe(context, recipeList.get(0),
                RecetasCookeoConstants.PATH_TYPE_EDITED);
        if(recipeOld == null){
            Log.d(TAG, "La OWN receta a guardar es null");
            uploadNextRecipe(context, recipeList);
            return;
        }

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("/" + RecetasCookeoConstants.PERSONAL_RECIPES_NODE);


        //String key = ref.child(user.getUid()).push().getKey();
        //Si la receta está en base de datos, era una editada, no una nueva. Me quedo con la key
        String key;

        RecipeDb recipeDb = recipeController.getRecipeByExactName((Application)context.getApplicationContext(),
                recipeOld.getName());
        if(recipeDb != null){
            Log.d(TAG, "La receta " + recipeDb.getName() + "tenía key " + recipeDb.getKey());
            key = recipeDb.getKey();
        }else{
            key = ref.child(user.getUid()).push().getKey();
            Log.d(TAG, "Para la receta " + recipeOld.getName() + "genero key " + key);
        }

        RecipeFirebase recipeFirebase = new RecipeFirebase(recipeOld);
        TimestampFirebase timestampFirebase = new TimestampFirebase();

        Map<String, Object> postDetailedValues = recipeFirebase.toMap();
        Map<String, Object> postTimestamp = timestampFirebase.toMap();

        final Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + key, postDetailedValues);
        childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE + "/" + key, postTimestamp);

        ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    uploadNextRecipe(context, recipeList);
                    return;
                }
                StringBuilder sbPath = new StringBuilder(100);
                sbPath.append(readWriteTools.getEditedStorageDir());
                sbPath.append(recipeList.get(0));
                readWriteTools.deleteFile(sbPath.toString());
                if(recipeOld.getPicture() != null && !recipeOld.getPicture().isEmpty()
                        && !recipeOld.getPicture().equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME)) {
                    StorageMethods storageMethods = new StorageMethods();
                    storageMethods.updatePictureToPersonalStorage(recipeOld);
                }
                //Log.d(TAG, "se ha subido correctamente las receta " +  recipeDb.getName());
                uploadNextRecipe(context, recipeList);
            }
        });
    }

    private void uploadNextRecipe(Context context, List<String> recipeList){
        if(recipeList.isEmpty()){
            return;
        }
        recipeList.remove(0);
        if(!recipeList.isEmpty()){
            updateRecipesToPersonalStorage(context, recipeList);
        }else{
            uploading = false;
        }
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
