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

    public boolean isPictureListEmpty(){
        return pullPictureNames() == null || pullPictureNames().isEmpty();
    }
    public boolean isRecipeListEmpty(){
        return pullRecipes() == null || pullRecipes().isEmpty();
    }

    public RecipeDb getPicture(int index){
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

    public int recipeSize(){
        return pullRecipes().size();
    }
    public RecipeDb removePicture(int index){
        return pullPictureNames().remove(index);
    }

    public RecipeDb removeRecipe(int index){
        return pullRecipes().remove(index);
    }

}
