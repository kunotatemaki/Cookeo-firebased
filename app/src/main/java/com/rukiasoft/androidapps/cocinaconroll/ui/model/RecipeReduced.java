package com.rukiasoft.androidapps.cocinaconroll.ui.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;

/**
 * Created by iRoll on 19/2/17.
 */
@AutoValue
public abstract class RecipeReduced implements Parcelable{
    abstract Long id();
    abstract String name();
    abstract Integer icon();
    abstract String picture();
    abstract Boolean vegetarian();
    abstract Boolean favourite();
    abstract Integer owner();
    abstract Long timestamp();

    static Builder builder() {
        return new $AutoValue_RecipeReduced.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder setId(Long value);
        abstract Builder setName(String value);
        abstract Builder setIcon(Integer value);
        abstract Builder setPicture(String value);
        abstract Builder setVegetarian(Boolean value);
        abstract Builder setFavourite(Boolean value);
        abstract Builder setOwner(Integer value);
        abstract Builder setTimestamp(Long value);
        abstract RecipeReduced build();
    }

    public static RecipeReduced getRecipeFromDatabase(RecipeDb recipeDb) {
        try {
            return RecipeReduced.builder()
                    .setId(recipeDb.getId())
                    .setName(recipeDb.getName())
                    .setIcon(recipeDb.getIcon())
                    .setPicture(recipeDb.getPicture())
                    .setVegetarian(recipeDb.getVegetarian())
                    .setFavourite(recipeDb.getFavourite())
                    .setOwner(recipeDb.getOwner())
                    .setTimestamp(recipeDb.getTimestamp())
                    .build();
        }catch (IllegalStateException e){
            return null;
        }
    }

    public long getId(){
        return id();
    }

    public String getName(){
        return name();
    }

    public String getPicture(){
        return picture();
    }

    public Integer getIcon(){
        return icon();
    }

    public Boolean getFavourite(){
        return favourite();
    }

    public Boolean getVegetarian(){
        return vegetarian();
    }

    public Integer getOwner(){
        return owner();
    }

    public Long getTimestamp(){
        return timestamp();
    }

}
