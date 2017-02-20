package com.rukiasoft.androidapps.cocinaconroll.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.ui.EditRecipeActivity;

/**
 * Created by iRuler on 30/12/15.
 */
public class CommonRecipeOperations {

    private RecipeItemOld recipe;
    private Activity activity;
    //private Context context;

    public CommonRecipeOperations(Activity activity, RecipeItemOld recipeItemOld){
        this.activity = activity;
        this.recipe = recipeItemOld;
    }
    public CommonRecipeOperations(Context context, RecipeItemOld recipeItemOld){
        if(context instanceof Activity) {
            this.activity = (Activity)context;
        }
        this.recipe = recipeItemOld;
    }

    public void editRecipe(){
        if(activity == null)    return;
        Intent intent = new Intent(activity, EditRecipeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RecetasCookeoConstants.KEY_RECIPE, recipe);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, RecetasCookeoConstants.REQUEST_EDIT_RECIPE);
    }


    public void updateRecipe(String deleteOldPicture){
        //// TODO: 21/2/17 todo esto hay que sacarlo al controler
//        if(activity == null)    return;
//        if(recipe.getPicture().equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME))
//            recipe.setPathPicture(RecetasCookeoConstants.DEFAULT_PICTURE_NAME);
//        ReadWriteTools rwTools = new ReadWriteTools();
//        String path = rwTools.saveRecipeOnEditedPath(activity.getApplicationContext(), recipe);
//        recipe.setPathRecipe(path);
//        recipe.setVersion(recipe.getVersion() + 1);
//
//        //update database
//        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
//        dbTools.updatePathsAndVersion(activity.getApplicationContext(), recipe);
//        if(!deleteOldPicture.isEmpty()) {
//            rwTools.deleteImage(deleteOldPicture);
//        }
    }

    public RecipeItemOld loadRecipeDetailsFromRecipeCard(){
        ReadWriteTools rwTools = new ReadWriteTools();
        if(recipe.getIngredients() == null || recipe.getIngredients().size() == 0){
            RecipeItemOld item = rwTools.readRecipeInfo(activity.getApplicationContext(), recipe.getPathRecipe());
            if(item == null)
                return null;
            recipe.setMinutes(item.getMinutes());
            recipe.setPortions(item.getPortions());
            recipe.setAuthor(item.getAuthor());
            recipe.setIngredients(item.getIngredients());
            recipe.setSteps(item.getSteps());
            recipe.setTip(item.getTip());
        }
        return recipe;
    }

}
