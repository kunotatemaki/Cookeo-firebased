package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import android.database.Cursor;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDbDao;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import org.greenrobot.greendao.query.CursorQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by iRoll on 27/1/17.O
 */

public class RecipeQueries {

    private static Query queryBothRecipesAndPicturesToDownload;
    private static Query queryOnlyRecipesToDownload;
    private static Query queryRecipeByPictureName;
    private static Query queryRecipeByKey;
    private static Query queryRecipeById;
    private static Query queryRecipesByName;
    private static Query queryRecipesByType;
    private static Query queryFavouriteRecipes;
    private static Query queryVegetarianRecipes;
    private static Query queryOwnRecipes;
    private static Query queryLatestRecipes;
    private static CursorQuery cursorAllRecipes;
    private static Query queryAllRecipes;

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

    public static Query getQueryRecipeByPictureName(DaoSession session) {
        if(queryRecipeByPictureName == null){
            initializeQueryRecipeByPictureName(session);
        }
        return queryRecipeByPictureName.forCurrentThread();
    }

    public static Query getQueryRecipeByKey(DaoSession session) {
        if(queryRecipeByKey == null){
            initializeQueryRecipeByKey(session);
        }
        return queryRecipeByKey.forCurrentThread();
    }

    public static Query getQueryRecipeById(DaoSession session) {
        if(queryRecipeById == null){
            initializeQueryRecipeById(session);
        }
        return queryRecipeById.forCurrentThread();
    }

    public static Cursor getCursorAllRecipes(DaoSession session) {
        if(cursorAllRecipes == null){
            initializeCursorAllRecipes(session);
        }
        return cursorAllRecipes.forCurrentThread().query();
    }

    public static Query getQueryAllRecipes(DaoSession session) {
        if(queryAllRecipes == null){
            initializeQueryAllRecipes(session);
        }
        return queryAllRecipes.forCurrentThread();
    }

    public static Query getQueryRecipesByName(DaoSession session) {
        if(queryRecipesByName == null){
            initializeQueryRecipesByName(session);
        }
        return queryRecipesByName.forCurrentThread();
    }

    public static Query getQueryRecipesByType(DaoSession session) {
        if(queryRecipesByType == null){
            initializeQueryRecipesByType(session);
        }
        return queryRecipesByType.forCurrentThread();
    }

    public static Query getQueryVegetarianRecipes(DaoSession session) {
        if(queryVegetarianRecipes == null){
            initializeQueryVegetarianRecipes(session);
        }
        return queryVegetarianRecipes.forCurrentThread();
    }

    public static Query getQueryFavouriteRecipes(DaoSession session) {
        if(queryFavouriteRecipes == null){
            initializeQueryFavouriteRecipes(session);
        }
        return queryFavouriteRecipes.forCurrentThread();
    }

    public static Query getQueryOwnRecipes(DaoSession session) {
        if(queryOwnRecipes == null){
            initializeQueryOwnRecipes(session);
        }
        return queryOwnRecipes.forCurrentThread();
    }

    public static Query getQueryLatestRecipes(DaoSession session) {
        if(queryLatestRecipes== null){
            initializeQueryLatestRecipes(session);
        }
        return queryLatestRecipes.forCurrentThread();
    }

    private static void initializeQueryBothRecipesAndPicturesToDownload(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        QueryBuilder qb = recipeDbDao.queryBuilder();
        queryBothRecipesAndPicturesToDownload = qb.where(
                qb.or(RecipeDbDao.Properties.DownloadRecipe.eq(1),
                        RecipeDbDao.Properties.DownloadPicture.eq(1))
        ).build();
    }

    private static void initializeQueryOnlyRecipesToDownload(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        QueryBuilder qb = recipeDbDao.queryBuilder();
        queryOnlyRecipesToDownload = qb.where(
                RecipeDbDao.Properties.DownloadRecipe.eq(1)
        ).build();
    }

    private static void initializeQueryRecipeByPictureName(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        QueryBuilder qb = recipeDbDao.queryBuilder();
        queryRecipeByPictureName = qb.where(
                RecipeDbDao.Properties.Picture.eq(null)
        ).build();
    }

    private static void initializeQueryRecipeByKey(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryRecipeByKey = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Key.eq(null)
        ).build();
    }

    private static void initializeQueryRecipeById(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryRecipeById = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Id.eq(null)
        ).build();
    }

    private static void initializeQueryRecipesByName(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryRecipesByName = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.NormalizedName.like(null)
        ).build();
    }

    private static void initializeQueryFavouriteRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryRecipesByName = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Favourite.eq(1)
        ).build();
    }

    private static void initializeQueryVegetarianRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryRecipesByName = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Vegetarian.eq(1)
        ).build();
    }

    private static void initializeQueryOwnRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryRecipesByName = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Owner.eq(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE)
        ).build();
    }

    private static void initializeQueryLatestRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
//        queryRecipesByName = recipeDbDao.queryBuilder().where(
//                RecipeDbDao.Properties.Favourite.eq(1)
//        ).build();
    }

    private static void initializeQueryRecipesByType(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryRecipesByType = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Type.like(null)
        ).build();
    }

    private static void initializeCursorAllRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        cursorAllRecipes = recipeDbDao.queryBuilder()
                .orderAsc(RecipeDbDao.Properties.NormalizedName)
                .buildCursor();
    }

    private static void initializeQueryAllRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        queryAllRecipes = recipeDbDao.queryBuilder()
                .orderAsc(RecipeDbDao.Properties.NormalizedName)
                .build();
    }



}
