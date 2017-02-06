package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.RecipeShortDao;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by iRoll on 27/1/17.O
 */

public class RecipeQueries {

    private static Query queryRecipesAndPicturesToDownload;
    private static Query queryRecipesToDownload;
    private static Query queryGetRecipeByPictureName;
    private static Query queryGetRecipeByKey;

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

}
