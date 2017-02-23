package com.rukiasoft.androidapps.cocinaconroll.persistence.local;


import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iRoll on 27/1/17.
 */

@AutoValue
public abstract class ObjectQeue implements Parcelable {
    public static ObjectQeue create(ArrayList<RecipeDb> pullRecipes, ArrayList<String> pullPictures) {
        return new AutoValue_ObjectQeue(pullRecipes, pullPictures);
    }

    abstract List<RecipeDb> pullRecipes();
    abstract List<String> pullPictureNames();

    public void addPicture(String value){
        pullPictureNames().add(value);
    }

    public void addRecipe(RecipeDb value){
        pullRecipes().add(value);
    }
    public boolean isPictureListEmpty(){
        return pullPictureNames().isEmpty();
    }
    public boolean isRecipeListEmpty(){
        return pullRecipes().isEmpty();
    }

    public String getPicture(int index){
        if(pullPictureNames().size() > index) {
            return pullPictureNames().get(index);
        }else{
            return null;
        }
    }

    public RecipeDb getRecipe(int index){
        if(pullRecipes().size() > index) {
            return pullRecipes().get(index);
        }else{
            return null;
        }
    }

    public boolean removePicture(String objectName){
        return pullPictureNames().remove(objectName);
    }

    public boolean removeRecipe(RecipeDb objectName){
        return pullRecipes().remove(objectName);
    }

    public int sizePicture(){
        return pullPictureNames().size();
    }
    public int sizeRecipe(){
        return pullRecipes().size();
    }
}
