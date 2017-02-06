package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.Ingredient;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.IngredientDao;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.RecipeShortDao;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.Step;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daos.StepDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by iRoll on 27/1/17.
 */

public class IngredientQueries {

    private static Query queryGetIngredientByKeyAndPosition;
    private static DeleteQuery<Ingredient> deleteQueryIngredientsByKey;



    public static Query getQueryGetIngredientByKeyAndPosition(DaoSession session) {
        if(queryGetIngredientByKeyAndPosition == null){
            initializeQueryGetIngredientByKeyAndPosition(session);
        }
        return queryGetIngredientByKeyAndPosition.forCurrentThread();
    }

    public static DeleteQuery<Ingredient> getDeleteQueryIngredientByKey(DaoSession session) {
        if(deleteQueryIngredientsByKey == null){
            initializeDeleteQueryIngredientsByKey(session);
        }
        return deleteQueryIngredientsByKey.forCurrentThread();
    }

    private static void initializeQueryGetIngredientByKeyAndPosition(DaoSession session){
        IngredientDao ingredientDao = session.getIngredientDao();
        ingredientDao.detachAll();
        queryGetIngredientByKeyAndPosition = ingredientDao.queryBuilder().where(
                IngredientDao.Properties.Key.eq(""),
                IngredientDao.Properties.Position.eq(0)
        ).build();
    }

    private static void initializeDeleteQueryIngredientsByKey(DaoSession session){
        IngredientDao ingredientDao = session.getIngredientDao();
        ingredientDao.detachAll();
        deleteQueryIngredientsByKey = ingredientDao.queryBuilder().where(
                IngredientDao.Properties.Key.eq("")
        ).buildDelete();
    }

}
