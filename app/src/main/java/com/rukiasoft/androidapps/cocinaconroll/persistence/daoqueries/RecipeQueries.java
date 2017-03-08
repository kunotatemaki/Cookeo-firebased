package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import android.database.Cursor;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
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
    private static Query queryOnlyRecipesToUpdate;
    private static Query queryOnlyPicturesToUpdate;
    private static Query queryRecipeByPictureName;
    private static Query queryRecipeByKey;
    private static Query queryRecipeById;
    private static Query queryRecipesByName;
    private static CursorQuery cursorFavouriteRecipes;
    private static CursorQuery cursorVegetarianRecipes;
    private static CursorQuery cursorOwnRecipes;
    private static CursorQuery cursorLatestRecipes;
    private static CursorQuery cursorAllRecipes;
    private static Query queryAllRecipes;

    public static Query getQueryBothRecipesAndPicturesToDownload(DaoSession session) {
        if(queryBothRecipesAndPicturesToDownload == null){
            initializeQueryBothRecipesAndPicturesToDownload(session);
        }
        return queryBothRecipesAndPicturesToDownload.forCurrentThread();
    }

    public static Query getQueryOnlyRecipesToUpdate(DaoSession session, boolean download) {
        if(queryOnlyRecipesToUpdate == null){
            initializeQueryOnlyRecipesToUpdate(session, download);
        }
        return queryOnlyRecipesToUpdate.forCurrentThread();
    }

    public static Query getQueryOnlyPicturesToUpdate(DaoSession session, boolean download) {
        if(queryOnlyPicturesToUpdate == null){
            initializeQueryOnlyPicturesToUpdate(session, download);
        }
        return queryOnlyPicturesToUpdate.forCurrentThread();
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

    public static Cursor getCursorRecipesByName(DaoSession session, String name) {
        CursorQuery cursor = initializeCursorRecipesByName(session, name);

        return cursor.forCurrentThread().query();
    }

    public static Cursor getCursorRecipesByType(DaoSession session, String type) {
        CursorQuery cursorRecipesByType = initializeCursorRecipesByType(session, type);
        return cursorRecipesByType.forCurrentThread().query();
    }

    public static Cursor getCursorVegetarianRecipes(DaoSession session) {
        if(cursorVegetarianRecipes == null){
            initializeCursorVegetarianRecipes(session);
        }
        return cursorVegetarianRecipes.forCurrentThread().query();
    }

    public static Cursor getCursorFavouriteRecipes(DaoSession session) {
        if(cursorFavouriteRecipes == null){
            initializeCursorFavouriteRecipes(session);
        }
        return cursorFavouriteRecipes.forCurrentThread().query();
    }

    public static Cursor getCursorOwnRecipes(DaoSession session) {
        if(cursorOwnRecipes == null){
            initializeCursorOwnRecipes(session);
        }
        return cursorOwnRecipes.forCurrentThread().query();
    }

    public static Cursor getCursorLatestRecipes(DaoSession session) {
        if(cursorLatestRecipes == null){
            initializeCursorLatestRecipes(session);
        }
        return cursorLatestRecipes.forCurrentThread().query();
    }

    private static void initializeQueryBothRecipesAndPicturesToDownload(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        QueryBuilder qb = recipeDbDao.queryBuilder();
        queryBothRecipesAndPicturesToDownload = qb.where(
                qb.or(RecipeDbDao.Properties.UpdateRecipe.eq(RecetasCookeoConstants.FLAG_DOWNLOAD_RECIPE),
                        RecipeDbDao.Properties.UpdatePicture.eq(RecetasCookeoConstants.FLAG_DOWNLOAD_PICTURE))
        ).build();
    }

    private static void initializeQueryOnlyRecipesToUpdate(DaoSession session, boolean download){
        int action = download? RecetasCookeoConstants.FLAG_DOWNLOAD_RECIPE : RecetasCookeoConstants.FLAG_UPLOAD_RECIPE;
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        QueryBuilder qb = recipeDbDao.queryBuilder();
        queryOnlyRecipesToUpdate = qb.where(
                RecipeDbDao.Properties.UpdateRecipe.eq(action)
        ).build();
    }

    private static void initializeQueryOnlyPicturesToUpdate(DaoSession session, boolean download){
        int action = download? RecetasCookeoConstants.FLAG_DOWNLOAD_PICTURE : RecetasCookeoConstants.FLAG_UPLOAD_PICTURE;
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        QueryBuilder qb = recipeDbDao.queryBuilder();
        queryOnlyPicturesToUpdate = qb.where(
                RecipeDbDao.Properties.UpdatePicture.eq(action)
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

    private static void initializeCursorFavouriteRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        cursorFavouriteRecipes = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Favourite.eq(1)
        ).buildCursor();
    }

    private static void initializeCursorVegetarianRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        cursorVegetarianRecipes = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Vegetarian.eq(1)
        ).buildCursor();
    }

    private static void initializeCursorOwnRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        QueryBuilder qb = recipeDbDao.queryBuilder();
        cursorOwnRecipes = qb.where(
                qb.or(RecipeDbDao.Properties.Owner.eq(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE),
                        RecipeDbDao.Properties.Edited.eq(1))
        ).buildCursor();

    }

    private static void initializeCursorLatestRecipes(DaoSession session){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        long timestamp = System.currentTimeMillis() -
                RecetasCookeoConstants.TIMEFRAME_NEW_RECIPE_DAYS * RecetasCookeoConstants.TIMEFRAME_MILI_SECONDS_DAY;
        cursorLatestRecipes = recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Timestamp.ge(timestamp)
        ).buildCursor();
    }

    private static CursorQuery initializeCursorRecipesByType(DaoSession session, String type){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        return recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.Type.like(type)
        ).buildCursor();
    }

    private static CursorQuery initializeCursorRecipesByName(DaoSession session, String name){
        RecipeDbDao recipeDbDao = session.getRecipeDbDao();
        recipeDbDao.detachAll();
        Query<RecipeDb> query = recipeDbDao.queryRawCreate("select name as suggest_text_1, icon as suggest_icon_1, normalized_name as suggest_intent_data_id where normalized_name like ?", "%" + name + "%");
        return recipeDbDao.queryBuilder().where(
                RecipeDbDao.Properties.NormalizedName.like("%" + name + "%")
        ).buildCursor();
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
