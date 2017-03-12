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
    public static ObjectQeue create(ArrayList<RecipeDb> listRecipes, ArrayList<RecipeDb> listPictures) {
        return new AutoValue_ObjectQeue(listRecipes, listPictures);
    }

    @Nullable abstract List<RecipeDb> listRecipes();
    @Nullable abstract List<RecipeDb> listPictureNames();

    public boolean isPictureListEmpty(){
        return listPictureNames() == null || listPictureNames().isEmpty();
    }
    public boolean isRecipeListEmpty(){
        return listRecipes() == null || listRecipes().isEmpty();
    }

    public RecipeDb getPicture(int index){
        if(listPictureNames().size() > index) {
            return listPictureNames().get(index);
        }else{
            return null;
        }
    }

    public RecipeDb getRecipe(int index){
        if(listRecipes().size() > index) {
            return listRecipes().get(index);
        }else{
            return null;
        }
    }


    public RecipeDb removePicture(int index){
        if(listPictureNames() != null && listPictureNames().size() > index) {
            return listPictureNames().remove(index);
        }else{
            return null;
        }
    }

    public RecipeDb removeRecipe(int index){
        if(listRecipes() != null && listRecipes().size() > index) {
            return listRecipes().remove(index);
        }else{
            return null;
        }
    }


}
