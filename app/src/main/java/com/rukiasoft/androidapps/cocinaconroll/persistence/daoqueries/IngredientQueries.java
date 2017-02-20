package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDbDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;

/**
 * Created by iRoll on 27/1/17.
 */

public class IngredientQueries {

    private static Query queryGetIngredientByKeyAndPosition;
    private static DeleteQuery<IngredientDb> deleteQueryIngredientsByKey;



    public static Query getQueryGetIngredientByKeyAndPosition(DaoSession session) {
        if(queryGetIngredientByKeyAndPosition == null){
            initializeQueryGetIngredientByKeyAndPosition(session);
        }
        return queryGetIngredientByKeyAndPosition.forCurrentThread();
    }

    public static DeleteQuery<IngredientDb> getDeleteQueryIngredientByKey(DaoSession session) {
        if(deleteQueryIngredientsByKey == null){
            initializeDeleteQueryIngredientsByKey(session);
        }
        return deleteQueryIngredientsByKey.forCurrentThread();
    }

    private static void initializeQueryGetIngredientByKeyAndPosition(DaoSession session){
        IngredientDbDao ingredientDao = session.getIngredientDbDao();
        ingredientDao.detachAll();
        queryGetIngredientByKeyAndPosition = ingredientDao.queryBuilder().where(
                IngredientDbDao.Properties.Key.eq(""),
                IngredientDbDao.Properties.Position.eq(0)
        ).build();
    }

    private static void initializeDeleteQueryIngredientsByKey(DaoSession session){
        IngredientDbDao ingredientDao = session.getIngredientDbDao();
        ingredientDao.detachAll();
        deleteQueryIngredientsByKey = ingredientDao.queryBuilder().where(
                IngredientDbDao.Properties.Key.eq("")
        ).buildDelete();
    }

}
