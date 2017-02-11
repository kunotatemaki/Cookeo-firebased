package com.rukiasoft.androidapps.cocinaconroll.persistence.controllers;

import android.app.Application;

import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.StepQueries;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.Step;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by iRoll on 11/2/17.
 */

public class StepController {

    public StepController() {
    }

    /**
     * Grabo los pasos de la receta en la base de datos local
     * @param steps lista de pasos
     * @param key identificador de la receta
     */
    public void saveStepsToDatabase(Application application, List<String> steps, String key){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "Step");
        StepDao stepDao = session.getStepDao();
        Query query = StepQueries.getQueryGetStepByKeyAndPosition(session);

        DeleteQuery<Step> delete = StepQueries.getDeleteQueryStepByKey(session);
        delete.setParameter(0, key);
        delete.executeDeleteWithoutDetachingEntities();

        for(int i=0; i<steps.size(); i++){
            query.setParameter(0, key);
            query.setParameter(1, i);
            Step step = (Step) query.unique();
            if(step == null){
                step = new Step();
            }
            step.setKey(key);
            step.setPosition(i);
            step.setStep(steps.get(i));
            stepDao.insertOrReplace(step);
        }
    }
}
