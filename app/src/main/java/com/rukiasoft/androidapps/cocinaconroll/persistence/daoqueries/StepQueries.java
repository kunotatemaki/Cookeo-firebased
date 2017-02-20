package com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDbDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;

/**
 * Created by iRoll on 27/1/17.
 */

public class StepQueries {

    private static Query queryGetStepByKeyAndPosition;
    private static DeleteQuery<StepDb> deleteQueryStepsByKey;

    public static Query getQueryGetStepByKeyAndPosition(DaoSession session) {
        if(queryGetStepByKeyAndPosition == null){
            initializeQueryGetStepByKeyAndPosition(session);
        }
        return queryGetStepByKeyAndPosition.forCurrentThread();
    }

    public static DeleteQuery<StepDb> getDeleteQueryStepByKey(DaoSession session) {
        if(deleteQueryStepsByKey == null){
            initializeDeleteQueryStepsByKey(session);
        }
        return deleteQueryStepsByKey.forCurrentThread();
    }

    private static void initializeQueryGetStepByKeyAndPosition(DaoSession session){
        StepDbDao stepDao = session.getStepDbDao();
        stepDao.detachAll();
        queryGetStepByKeyAndPosition = stepDao.queryBuilder().where(
                StepDbDao.Properties.Key.eq(""),
                StepDbDao.Properties.Position.eq(0)
        ).build();
    }

    private static void initializeDeleteQueryStepsByKey(DaoSession session){
        StepDbDao stepDao = session.getStepDbDao();
        stepDao.detachAll();
        deleteQueryStepsByKey = stepDao.queryBuilder().where(
                StepDbDao.Properties.Key.eq("")
        ).buildDelete();
    }


}
