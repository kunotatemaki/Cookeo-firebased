package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.Step;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;

/**
 * Created by iRoll on 27/1/17.
 */

public class StepQueries {

    private static Query queryGetStepByKeyAndPosition;
    private static DeleteQuery<Step> deleteQueryStepsByKey;

    public static Query getQueryGetStepByKeyAndPosition(DaoSession session) {
        if(queryGetStepByKeyAndPosition == null){
            initializeQueryGetStepByKeyAndPosition(session);
        }
        return queryGetStepByKeyAndPosition.forCurrentThread();
    }

    public static DeleteQuery<Step> getDeleteQueryStepByKey(DaoSession session) {
        if(deleteQueryStepsByKey == null){
            initializeDeleteQueryStepsByKey(session);
        }
        return deleteQueryStepsByKey.forCurrentThread();
    }

    private static void initializeQueryGetStepByKeyAndPosition(DaoSession session){
        StepDao stepDao = session.getStepDao();
        stepDao.detachAll();
        queryGetStepByKeyAndPosition = stepDao.queryBuilder().where(
                StepDao.Properties.Key.eq(""),
                StepDao.Properties.Position.eq(0)
        ).build();
    }

    private static void initializeDeleteQueryStepsByKey(DaoSession session){
        StepDao stepDao = session.getStepDao();
        stepDao.detachAll();
        deleteQueryStepsByKey = stepDao.queryBuilder().where(
                StepDao.Properties.Key.eq("")
        ).buildDelete();
    }


}
