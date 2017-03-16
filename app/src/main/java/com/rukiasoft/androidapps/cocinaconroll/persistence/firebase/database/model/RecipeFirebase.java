package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model;

/*
  Created by iRoll on 15/1/17.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class RecipeFirebase {

    private String name;
    private String type;
    private String picture;
    private List<String> ingredients;
    private List<String> steps;
    private String author;
    private String link;
    private Boolean vegetarian;
    private Integer portions;
    private Integer minutes;
    private int language = RecetasCookeoConstants.LANG_SPANISH;
    private String tip;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("author", author);
        result.put("link", link);
        result.put("type", type);
        if(picture != null) {
            result.put("picture", picture);
        }
        result.put("vegetarian", vegetarian);
        result.put("portions", portions);
        result.put("minutes", minutes);
        result.put("language", language);
        result.put("tip", tip);
        result.put("ingredients", ingredients);
        result.put("steps", steps);

        return result;
    }

    public RecipeFirebase() {

    }


    public RecipeFirebase(RecipeItemOld recipeItemOld) {
        this.name = recipeItemOld.getName();
        this.type = recipeItemOld.getType();
        this.picture = recipeItemOld.getPicture().equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME)? null : recipeItemOld.getPicture();
        this.ingredients = recipeItemOld.getIngredients();
        this.steps = recipeItemOld.getSteps();
        this.author = recipeItemOld.getAuthor();
        this.vegetarian = recipeItemOld.getVegetarian();
        this.portions = recipeItemOld.getPortions();
        this.minutes = recipeItemOld.getMinutes();
        this.language = RecetasCookeoConstants.LANG_SPANISH;
        this.tip = recipeItemOld.getTip();
    }

    public RecipeFirebase(RecipeDb recipeDb) {
        this.name = recipeDb.getName();
        this.type = recipeDb.getType();
        this.picture = recipeDb.getPicture();
        this.ingredients = recipeDb.getIngredientsAsStringList();
        this.steps = recipeDb.getStepsAsStringList();
        this.author = recipeDb.getAuthor();
        this.vegetarian = recipeDb.getVegetarian();
        this.portions = recipeDb.getPortions();
        this.minutes = recipeDb.getMinutes();
        this.language = RecetasCookeoConstants.LANG_SPANISH;
        this.tip = recipeDb.getTip();
    }



    /*public RecipeDetailedFirebase(DatabaseItem recipe) {
        this.picture = recipe.getPullPicture();
        this.language = CukioConstants.LANG_SPANISH;
    }*/

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Boolean getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPortions() {
        return portions;
    }

    public void setPortions(Integer portions) {
        this.portions = portions;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}