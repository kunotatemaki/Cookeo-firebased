package com.rukiasoft.androidapps.cocinaconroll.persistence.controllers;

import android.app.Application;

import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.IngredientQueries;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDbDao;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;

/**
 * Created by iRoll on 11/2/17.
 */

public class IngredientController {

    public IngredientController(){

    }

    /**
     * Salva los ingredientes de una receta en la base de datos local
     * @param ingredientDbs lista de ingredientes
     */
    public void saveIngredientsToDatabase(Application application, List<IngredientDb> ingredientDbs, String key){

        //Grabo los ingredientes (primero borro los que había)
        DaoSession session = CommonController.getDaosessionFromApplication(application, "IngredientDb");
        IngredientDbDao ingredientDao = session.getIngredientDbDao();

        DeleteQuery<IngredientDb> delete = IngredientQueries.getDeleteQueryIngredientByKey(session);
        delete.setParameter(0, key);
        delete.executeDeleteWithoutDetachingEntities();

        for(IngredientDb ingredientDb : ingredientDbs){
            ingredientDao.insertOrReplace(ingredientDb);
        }
    }
}
