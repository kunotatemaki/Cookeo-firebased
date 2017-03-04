package com.rukiasoft.androidapps.cocinaconroll.ui.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;


/**
 * Created by iRoll on 19/2/17.
 */
@AutoValue
public abstract class RecipeSearch implements Parcelable{
    abstract Long id();
    abstract String name();
    abstract Integer icon();

    public static RecipeSearch create(Long id, String name, Integer icon) {
        return new AutoValue_RecipeSearch(id, name, icon);
    }


    public long getId(){
        return id();
    }

    public String getName(){
        return name();
    }

    public Integer getIcon(){
        return icon();
    }

}
