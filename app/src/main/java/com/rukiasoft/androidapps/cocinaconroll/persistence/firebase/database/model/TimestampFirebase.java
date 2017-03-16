package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model;

/*
  Created by iRoll on 15/1/17.
 */

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

@IgnoreExtraProperties
public class TimestampFirebase {
    private Object timestamp;

    public TimestampFirebase(){
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Long getTimestamp() {
        if (timestamp instanceof Long) {
            return (Long) timestamp;
        }
        else {
            return null;
        }
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("timestamp", timestamp);
        return result;
    }
}
