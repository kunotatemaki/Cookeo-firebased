package com.rukiasoft.androidapps.cocinaconroll.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by iRoll on 22/2/17.
 */

public class AlarmTools {

    public static void setAlarm(Context context){
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(RecetasCookeoConstants.NAME_ALARM_PENDING_INTENT),
                PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmUp)
        {
            Intent alarmIntent = new Intent(RecetasCookeoConstants.NAME_ALARM_PENDING_INTENT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            //Lo programo a las 9 a.m. más un número aleatorio de minutos
            calendar.add(Calendar.HOUR_OF_DAY, 9);
            Random r = new Random();
            int i1 = r.nextInt(60);
            calendar.add(Calendar.MINUTE, i1);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
