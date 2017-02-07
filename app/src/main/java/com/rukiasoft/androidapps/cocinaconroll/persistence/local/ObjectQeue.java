package com.rukiasoft.androidapps.cocinaconroll.persistence.local;


import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iRoll on 27/1/17.
 */

@AutoValue
public abstract class ObjectQeue implements Parcelable {
    public static ObjectQeue create(ArrayList<String> pullObjects) {
        return new AutoValue_ObjectQeue(pullObjects);
    }

    abstract List<String> pullNames();

    public void add(String value){
        pullNames().add(value);
    }

    public boolean isEmpty(){
        return pullNames().isEmpty();
    }

    public String get(int index){
        if(pullNames().size() > index) {
            return pullNames().get(index);
        }else{
            return null;
        }
    }

    public boolean remove(String objectName){
        return pullNames().remove(objectName);
    }

    public int size(){
        return pullNames().size();
    }
}
