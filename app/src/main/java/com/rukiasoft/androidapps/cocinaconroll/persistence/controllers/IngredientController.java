package com.rukiasoft.androidapps.cocinaconroll.persistence.controllers;

import android.app.Application;

import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.IngredientQueries;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.Ingredient;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by iRoll on 11/2/17.
 */

public class IngredientController {

    public IngredientController(){

    }

    /**
     * Salva los ingredientes de una receta en la base de datos local
     * @param ingredients lista de ingredientes
     * @param key identificador de la receta
     */
    public void saveIngredientsToDatabase(Application application, List<String> ingredients, String key){
        //Grabo los ingredientes (primero borro los que había)
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Ingredient");
        IngredientDao ingredientDao = session.getIngredientDao();
        Query query = IngredientQueries.getQueryGetIngredientByKeyAndPosition(session);

        DeleteQuery<Ingredient> delete = IngredientQueries.getDeleteQueryIngredientByKey(session);
        delete.setParameter(0, key);
        delete.executeDeleteWithoutDetachingEntities();

        for(int i=0; i<ingredients.size(); i++){
            query.setParameter(0, key);
            query.setParameter(1, i);
            Ingredient ingredient = (Ingredient) query.unique();
            if(ingredient == null){
                ingredient = new Ingredient();
            }
            ingredient.setKey(key);
            ingredient.setPosition(i);
            ingredient.setIngredient(ingredients.get(i));
            ingredientDao.insertOrReplace(ingredient);

        }
    }
}
