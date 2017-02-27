package com.rukiasoft.androidapps.cocinaconroll.ui.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.StepQueries;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDb;

import java.util.List;

/**
 * Created by iRoll on 19/2/17.
 */
@AutoValue
public abstract class RecipeComplete implements Parcelable{
    abstract Long id();
    abstract String key();
    abstract String name();
    abstract String type();
    abstract Integer icon();
    abstract String picture();
    abstract Boolean vegetarian();
    abstract Boolean favourite();
    abstract Integer minutes();
    abstract Integer portions();
    abstract Integer language();
    abstract String author();
    abstract String link();
    @Nullable
    abstract String tip();
    abstract Integer owner();
    abstract Long timestamp();
    @Nullable abstract List<String> ingredients();
    @Nullable abstract List<String> steps();

    static RecipeComplete.Builder builder() {
        return new $AutoValue_RecipeComplete.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract RecipeComplete.Builder setId(Long value);
        abstract RecipeComplete.Builder setKey(String value);
        abstract RecipeComplete.Builder setName(String value);
        abstract RecipeComplete.Builder setIcon(Integer value);
        abstract RecipeComplete.Builder setPicture(String value);
        abstract RecipeComplete.Builder setTimestamp(Long value);
        abstract RecipeComplete.Builder setVegetarian(Boolean value);
        abstract RecipeComplete.Builder setFavourite(Boolean value);
        abstract RecipeComplete.Builder setOwner(Integer value);
        abstract RecipeComplete.Builder setType(String value);
        abstract RecipeComplete.Builder setMinutes(Integer value);
        abstract RecipeComplete.Builder setPortions(Integer value);
        abstract RecipeComplete.Builder setAuthor(String value);
        abstract RecipeComplete.Builder setLink(String value);
        abstract RecipeComplete.Builder setTip(String value);
        abstract RecipeComplete.Builder setLanguage(Integer value);
        abstract RecipeComplete.Builder setIngredients(List<String> value);
        abstract RecipeComplete.Builder setSteps(List<String> value);
        abstract RecipeComplete build();

    }

    public static RecipeComplete getRecipeFromDatabase(RecipeDb recipeDb) {
        try {
            return RecipeComplete.builder()
                    .setId(recipeDb.getId())
                    .setKey(recipeDb.getKey())
                    .setName(recipeDb.getName())
                    .setIcon(recipeDb.getIcon())
                    .setPicture(recipeDb.getPicture())
                    .setTimestamp(recipeDb.getTimestamp())
                    .setVegetarian(recipeDb.getVegetarian())
                    .setFavourite(recipeDb.getFavourite())
                    .setOwner(recipeDb.getOwner())
                    .setTimestamp(recipeDb.getTimestamp())
                    .setType(recipeDb.getType())
                    .setMinutes(recipeDb.getMinutes())
                    .setPortions(recipeDb.getPortions())
                    .setAuthor(recipeDb.getAuthor())
                    .setLink(recipeDb.getLink())
                    .setTip(recipeDb.getTip())
                    .setLanguage(recipeDb.getLanguage())
                    .setIngredients(recipeDb.getIngredientsAsStringList())
                    .setSteps(recipeDb.getStepsAsStringList())
                    .build();
        }catch (IllegalStateException e){
            e.printStackTrace();
            return null;
        }
    }

    public static RecipeComplete getRecipeFromRecipe(RecipeComplete recipe) {
        try {
            return RecipeComplete.builder()
                    .setId(recipe.getId())
                    .setKey(recipe.getKey())
                    .setTimestamp(recipe.getTimestamp())
                    .setName(recipe.getName())
                    .setIcon(recipe.getIcon())
                    .setPicture(recipe.getPicture())
                    .setVegetarian(recipe.getVegetarian())
                    .setFavourite(recipe.getFavourite())
                    .setOwner(recipe.getOwner())
                    .setTimestamp(recipe.getTimestamp())
                    .setType(recipe.getType())
                    .setMinutes(recipe.getMinutes())
                    .setPortions(recipe.getPortions())
                    .setAuthor(recipe.getAuthor())
                    .setLink(recipe.getLink())
                    .setTip(recipe.getTip())
                    .setIngredients(recipe.getIngredients())
                    .setSteps(recipe.getSteps())
                    .build();
        }catch (IllegalStateException e){
            return null;
        }
    }

    public Long getId() {
        return id();
    }

    public String getKey() {
        return key();
    }

    public String getName() {
        return name();
    }

    public String getType() {
        return type();
    }

    public Integer getIcon() {
        return icon();
    }

    public String getPicture() {
        return picture();
    }

    public Boolean getVegetarian() {
        return vegetarian();
    }

    public Boolean getFavourite() {
        return favourite();
    }

    public Integer getMinutes() {
        return minutes();
    }

    public Integer getPortions() {
        return portions();
    }

    public Integer getLanguage() {
        return language();
    }

    public String getAuthor() {
        return author();
    }

    public String getLink() {
        return link();
    }

    public String getTip() {
        return tip();
    }

    public Integer getOwner() {
        return owner();
    }

    public Long getTimestamp(){
        return timestamp();
    }

    public List<String> getIngredients() {
        return ingredients();
    }

    public List<String> getSteps() {
        return steps();
    }

}
