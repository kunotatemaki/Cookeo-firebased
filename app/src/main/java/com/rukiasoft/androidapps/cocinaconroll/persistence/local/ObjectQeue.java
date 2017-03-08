package com.rukiasoft.androidapps.cocinaconroll.persistence.local;


import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iRoll on 27/1/17.
 */

@AutoValue
public abstract class ObjectQeue implements Parcelable {
    public static ObjectQeue create(ArrayList<RecipeDb> pullRecipes, ArrayList<RecipeDb> pullPictures) {
        return new AutoValue_ObjectQeue(pullRecipes, pullPictures);
    }

    @Nullable abstract List<RecipeDb> pullRecipes();
    @Nullable abstract List<RecipeDb> pullPictureNames();
    @Nullable abstract List<RecipeDb> pushRecipes();
    @Nullable abstract List<RecipeDb> pushPictureNames();

    public boolean isPicturePullListEmpty(){
        return pullPictureNames() == null || pullPictureNames().isEmpty();
    }
    public boolean isRecipePullListEmpty(){
        return pullRecipes() == null || pullRecipes().isEmpty();
    }

    public boolean isPicturePushListEmpty(){
        return pushPictureNames() == null || pushPictureNames().isEmpty();
    }
    public boolean isRecipePushListEmpty(){
        return pushRecipes() == null || pushRecipes().isEmpty();
    }

    public RecipeDb getPullPicture(int index){
        if(pullPictureNames().size() > index) {
            return pullPictureNames().get(index);
        }else{
            return null;
        }
    }

    public RecipeDb getPullRecipe(int index){
        if(pullRecipes().size() > index) {
            return pullRecipes().get(index);
        }else{
            return null;
        }
    }

    public RecipeDb getPushPicture(int index){
        if(pushPictureNames().size() > index) {
            return pushPictureNames().get(index);
        }else{
            return null;
        }
    }

    public RecipeDb getPushRecipe(int index){
        if(pushRecipes().size() > index) {
            return pushRecipes().get(index);
        }else{
            return null;
        }
    }


    public RecipeDb removePullPicture(int index){
        return pullPictureNames().remove(index);
    }

    public RecipeDb removePullRecipe(int index){
        return pullRecipes().remove(index);
    }

    public RecipeDb removePushPicture(int index){
        return pushPictureNames().remove(index);
    }

    public RecipeDb removePushRecipe(int index){
        return pushRecipes().remove(index);
    }

}
