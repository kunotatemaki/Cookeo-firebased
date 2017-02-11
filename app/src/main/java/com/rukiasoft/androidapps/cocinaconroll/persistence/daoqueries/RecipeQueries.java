package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDao;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by iRoll on 27/1/17.O
 */

public class RecipeQueries {

    private static Query queryBothRecipesAndPicturesToDownload;
    private static Query queryOnlyRecipesToDownload;
    private static Query queryGetRecipeByPictureName;
    private static Query queryGetRecipeByKey;
    private static Query queryGetRecipeByName;

    public static Query getQueryBothRecipesAndPicturesToDownload(DaoSession session) {
        if(queryBothRecipesAndPicturesToDownload == null){
            initializeQueryBothRecipesAndPicturesToDownload(session);
        }
        return queryBothRecipesAndPicturesToDownload.forCurrentThread();
    }

    public static Query getQueryOnlyRecipesToDownload(DaoSession session) {
        if(queryOnlyRecipesToDownload == null){
            initializeQueryOnlyRecipesToDownload(session);
        }
        return queryOnlyRecipesToDownload.forCurrentThread();
    }

    public static Query getQueryGetRecipeByPictureName(DaoSession session) {
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

    public static Query getQueryGetRecipeByName(DaoSession session) {
        if(queryGetRecipeByName == null){
            initializeQueryGetRecipeByName(session);
        }
        return queryGetRecipeByName.forCurrentThread();
    }

    private static void initializeQueryBothRecipesAndPicturesToDownload(DaoSession session){
        RecipeDao recipeDao = session.getRecipeDao();
        recipeDao.detachAll();
        QueryBuilder qb = recipeDao.queryBuilder();
        queryBothRecipesAndPicturesToDownload = qb.where(
                qb.or(RecipeDao.Properties.DownloadRecipe.eq(1),
                        RecipeDao.Properties.DownloadPicture.eq(1))
        ).build();
    }

    private static void initializeQueryOnlyRecipesToDownload(DaoSession session){
        RecipeDao recipeDao = session.getRecipeDao();
        recipeDao.detachAll();
        QueryBuilder qb = recipeDao.queryBuilder();
        queryOnlyRecipesToDownload = qb.where(
                RecipeDao.Properties.DownloadRecipe.eq(1)
        ).build();
    }

    private static void initializeQueryGetRecipeByPictureName(DaoSession session){
        RecipeDao recipeDao = session.getRecipeDao();
        recipeDao.detachAll();
        QueryBuilder qb = recipeDao.queryBuilder();
        queryGetRecipeByPictureName = qb.where(
                RecipeDao.Properties.Picture.eq("")
        ).build();
    }

    private static void initializeQueryGetRecipeByKey(DaoSession session){
        RecipeDao recipeDao = session.getRecipeDao();
        recipeDao.detachAll();
        queryGetRecipeByKey = recipeDao.queryBuilder().where(
                RecipeDao.Properties.Key.eq("")
        ).build();
    }

    private static void initializeQueryGetRecipeByName(DaoSession session){
        RecipeDao recipeDao = session.getRecipeDao();
        recipeDao.detachAll();
        queryGetRecipeByName = recipeDao.queryBuilder().where(
                RecipeDao.Properties.Name.eq("")
        ).build();
    }

}
