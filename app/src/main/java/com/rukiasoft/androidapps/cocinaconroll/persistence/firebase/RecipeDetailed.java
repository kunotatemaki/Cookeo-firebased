package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase;

/**
 * Created by iRoll on 15/1/17.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class RecipeDetailed{

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

    public RecipeDetailed() {

    }

    /*public RecipeDetailedFirebase(RecipeItem recipe) {
        this.name = recipe.getName();
        this.type = recipe.getType();
        this.picture = recipe.getPicture().equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME)? null : recipe.getUidRecipe();
        this.ingredients = recipe.getIngredients();
        this.steps = recipe.getSteps();
        this.author = recipe.getAuthor();
        this.link = recipe.getLink();
        this.vegetarian = recipe.getVegetarian();
        this.portions = recipe.getPortions();
        this.minutes = recipe.getMinutes();
        this.language = CukioConstants.LANG_SPANISH;
        this.tip = recipe.getTip();
    }*/

    /*public RecipeDetailedFirebase(DatabaseItem recipe) {
        this.picture = recipe.getPicture();
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