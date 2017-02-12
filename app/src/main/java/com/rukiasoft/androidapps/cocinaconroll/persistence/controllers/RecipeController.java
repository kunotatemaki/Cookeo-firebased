package com.rukiasoft.androidapps.cocinaconroll.persistence.controllers;


import android.app.Application;

import com.google.firebase.database.DataSnapshot;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.RecipeQueries;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeDetailed;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.Recipe;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDao;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by iRoll on 9/2/17.
 */

public class RecipeController {

    private String TAG = LogHelper.makeLogTag(this.getClass());

    public RecipeController(){

    }


    
    public void insertOrReplaceRecipe(Application application, Recipe recipe){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Recipe");
        RecipeDao recipeDao = session.getRecipeDao();
        recipeDao.detachAll();
        recipeDao.insertOrReplace(recipe);

    }

    public Recipe getRecipeByKey(Application application, String key){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Recipe");
        Query query = RecipeQueries.getQueryGetRecipeByKey(session);
        query.forCurrentThread().setParameter(0, key);
        return (Recipe) query.unique();
    }

    public List<Recipe> getListBothRecipeAndPicturesToDownload(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Recipe");
        return RecipeQueries.getQueryBothRecipesAndPicturesToDownload(session).list();
    }

    public List<Recipe> getListOnlyRecipeToDownload(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Recipe");
        return RecipeQueries.getQueryOnlyRecipesToDownload(session).list();
    }

    public void updateDownloadRecipeFlag(Application application, String name, boolean state) {
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Recipe");
        Query query = RecipeQueries.getQueryGetRecipeByName(session);
        query.setParameter(0, name);
        Recipe recipeFromDatabase = (Recipe) query.unique();
        if(recipeFromDatabase != null) {
            recipeFromDatabase.setDownloadPicture(false);
            recipeFromDatabase.update();
        }
    }

    public Recipe insertRecipeFromFirebase(Application application, DataSnapshot dataSnapshot, RecipeDetailed recipeFromFirebase) {
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Recipe");
        Integer flag = FirebaseDbMethods.getRecipeFlagFromNodeName(dataSnapshot.getRef().getParent().getParent().getKey());
        Recipe recipe = new Recipe(recipeFromFirebase, dataSnapshot.getKey(), flag);
        RecipeDao recipeDao = session.getRecipeDao();
        recipeDao.detachAll();
        recipeDao.insertOrReplace(recipe);
        //grabo los ingredientes
        IngredientController ingredientController = new IngredientController();
        ingredientController.saveIngredientsToDatabase(application, recipeFromFirebase.getIngredients(), dataSnapshot.getKey());
        //grabo los pasos
        StepController stepController = new StepController();
        stepController.saveStepsToDatabase(application, recipeFromFirebase.getSteps(), dataSnapshot.getKey());
        //Log.d(TAG, "insertado " + recipe.getName());
        return recipe;
    }


    public Recipe getRecipeByName(Application application, String name) {
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Recipe");
        Query queryRecipe = RecipeQueries.getQueryGetRecipeByName(session);
        queryRecipe.setParameter(0, name);
        List<Recipe> recipeList = queryRecipe.list();
        Recipe recipe = null;
        if(recipeList != null && recipeList.size()>0){
            recipe = recipeList.get(0);
        }
        return recipe;

    }
}
