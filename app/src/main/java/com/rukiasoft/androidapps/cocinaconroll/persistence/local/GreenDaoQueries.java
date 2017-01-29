package com.rukiasoft.androidapps.cocinaconroll.persistence.local;

import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.Ingredient;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.IngredientDao;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.RecipeShortDao;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.Step;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.StepDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by iRoll on 27/1/17.
 */

public class GreenDaoQueries {

    private static Query queryRecipesAndPicturesToDownload;
    private static Query queryRecipesToDownload;
    private static Query queryGetRecipeByPictureName;
    private static Query queryGetRecipeByKey;
    private static Query queryGetIngredientByKeyAndPosition;
    private static Query queryGetStepByKeyAndPosition;
    private static DeleteQuery<Ingredient> deleteQueryIngredientsByKey;
    private static DeleteQuery<Step> deleteQueryStepsByKey;

    public static Query getQueryRecipesAndPicturesToDownload(DaoSession session) {
        if(queryRecipesAndPicturesToDownload == null){
            initializeQueryRecipesAndPicturesToDownload(session);
        }
        return queryRecipesAndPicturesToDownload.forCurrentThread();
    }

    public static Query getQueryRecipesToDownload(DaoSession session) {
        if(queryRecipesToDownload == null){
            initializeQueryRecipesToDownload(session);
        }
        return queryRecipesToDownload.forCurrentThread();
    }

    public static Query getQueryGetRecipeByName(DaoSession session) {
        if(queryGetRecipeByPictureName == null){
            initializeQueryGetRecipeByPictureName(session);
        }
        return queryGetRecipeByPictureName.forCurrentThread();
    }

    public static Query getQueryGetRecipeByKey(DaoSession session) {
        if(queryGetRecipeByKey == null){
            initializeQueryGetRecipeByKey(session);
        }
        return queryGetRecipeByKey.forCurrentThread();
    }

    public static Query getQueryGetIngredientByKeyAndPosition(DaoSession session) {
        if(queryGetIngredientByKeyAndPosition == null){
            initializeQueryGetIngredientByKeyAndPosition(session);
        }
        return queryGetIngredientByKeyAndPosition.forCurrentThread();
    }

    public static Query getQueryGetStepByKeyAndPosition(DaoSession session) {
        if(queryGetStepByKeyAndPosition == null){
            initializeQueryGetStepByKeyAndPosition(session);
        }
        return queryGetStepByKeyAndPosition.forCurrentThread();
    }

    public static DeleteQuery<Ingredient> getDeleteQueryIngredientByKey(DaoSession session) {
        if(deleteQueryIngredientsByKey == null){
            initializeDeleteQueryIngredientsByKey(session);
        }
        return deleteQueryIngredientsByKey.forCurrentThread();
    }

    public static DeleteQuery<Step> getDeleteQueryStepByKey(DaoSession session) {
        if(deleteQueryStepsByKey == null){
            initializeDeleteQueryStepsByKey(session);
        }
        return deleteQueryStepsByKey.forCurrentThread();
    }

    private static void initializeQueryRecipesAndPicturesToDownload(DaoSession session){
        RecipeShortDao recipeShortDao = session.getRecipeShortDao();
        recipeShortDao.detachAll();
        QueryBuilder qb = recipeShortDao.queryBuilder();
        queryRecipesAndPicturesToDownload = qb.where(
                qb.or(RecipeShortDao.Properties.DownloadRecipe.eq(1),
                        RecipeShortDao.Properties.DownloadPicture.eq(1))
        ).build();
    }

    private static void initializeQueryRecipesToDownload(DaoSession session){
        RecipeShortDao recipeShortDao = session.getRecipeShortDao();
        recipeShortDao.detachAll();
        QueryBuilder qb = recipeShortDao.queryBuilder();
        queryRecipesToDownload = qb.where(
                RecipeShortDao.Properties.DownloadRecipe.eq(1)
        ).build();
    }

    private static void initializeQueryGetRecipeByPictureName(DaoSession session){
        RecipeShortDao recipeShortDao = session.getRecipeShortDao();
        recipeShortDao.detachAll();
        QueryBuilder qb = recipeShortDao.queryBuilder();
        queryGetRecipeByPictureName = qb.where(
                RecipeShortDao.Properties.Picture.eq("")
        ).build();
    }

    private static void initializeQueryGetRecipeByKey(DaoSession session){
        RecipeShortDao recipeShortDao = session.getRecipeShortDao();
        recipeShortDao.detachAll();
        queryGetRecipeByKey = recipeShortDao.queryBuilder().where(
                RecipeShortDao.Properties.Key.eq("")
        ).build();
    }

    private static void initializeQueryGetIngredientByKeyAndPosition(DaoSession session){
        IngredientDao ingredientDao = session.getIngredientDao();
        ingredientDao.detachAll();
        queryGetIngredientByKeyAndPosition = ingredientDao.queryBuilder().where(
                IngredientDao.Properties.Key.eq(""),
                IngredientDao.Properties.Position.eq(0)
        ).build();
    }

    private static void initializeQueryGetStepByKeyAndPosition(DaoSession session){
        StepDao stepDao = session.getStepDao();
        stepDao.detachAll();
        queryGetStepByKeyAndPosition = stepDao.queryBuilder().where(
                StepDao.Properties.Key.eq(""),
                StepDao.Properties.Position.eq(0)
        ).build();
    }

    private static void initializeDeleteQueryIngredientsByKey(DaoSession session){
        IngredientDao ingredientDao = session.getIngredientDao();
        ingredientDao.detachAll();
        deleteQueryIngredientsByKey = ingredientDao.queryBuilder().where(
                IngredientDao.Properties.Key.eq("")
        ).buildDelete();
    }

    private static void initializeDeleteQueryStepsByKey(DaoSession session){
        StepDao stepDao = session.getStepDao();
        stepDao.detachAll();
        deleteQueryStepsByKey = stepDao.queryBuilder().where(
                StepDao.Properties.Key.eq("")
        ).buildDelete();
    }


}
