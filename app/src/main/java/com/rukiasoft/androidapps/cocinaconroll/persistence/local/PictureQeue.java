package com.rukiasoft.androidapps.cocinaconroll.persistence.local;


import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iRoll on 27/1/17.
 */

@AutoValue
public abstract class PictureQeue implements Parcelable {
    public static PictureQeue create(ArrayList<String> pullPictures) {
        return new AutoValue_PictureQeue(pullPictures);
    }

    abstract List<String> pullPictures();

    public void add(String value){
        pullPictures().add(value);
    }

    public boolean isEmpty(){
        return pullPictures().isEmpty();
    }

    public String get(int index){
        if(pullPictures().size() > index) {
            return pullPictures().get(index);
        }else{
            return null;
        }
    }

    public boolean remove(String pictureName){
        return pullPictures().remove(pictureName);
    }

    public int size(){
        return pullPictures().size();
    }
}
