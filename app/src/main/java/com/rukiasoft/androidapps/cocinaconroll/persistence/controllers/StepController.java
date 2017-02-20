package com.rukiasoft.androidapps.cocinaconroll.persistence.controllers;

import android.app.Application;

import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.StepQueries;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDbDao;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;

/**
 * Created by iRoll on 11/2/17.
 */

public class StepController {

    public StepController() {
    }

    /**
     * Grabo los pasos de la receta en la base de datos local
     * @param stepDbs lista de pasos
     * @param key identificador de la receta
     */
    public void saveStepsToDatabase(Application application, List<StepDb> stepDbs, String key){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "StepDb");
        StepDbDao stepDao = session.getStepDbDao();

        DeleteQuery<StepDb> delete = StepQueries.getDeleteQueryStepByKey(session);
        delete.setParameter(0, key);
        delete.executeDeleteWithoutDetachingEntities();

        for(StepDb stepDb : stepDbs){
            stepDao.insertOrReplace(stepDb);
        }
    }
}
