package com.rukiasoft.androidapps.cocinaconroll.ui.model;

import android.content.ContentValues;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.List;

/**
 * Created by iRoll on 19/2/17.
 */

public class RecipeComplete{
    private Long id;
    private String key;
    private String name;
    private String type;
    private Integer icon;
    private String picture;
    private Boolean vegetarian;
    private Boolean favourite;
    private Integer minutes;
    private Integer portions;
    private Integer language;
    private String author;
    private String link;
    private String tip;
    private Integer owner;
    private Boolean edited;
    private Long timestamp;
    private List<String> ingredients;
    private List<String> steps;





    public static RecipeComplete getRecipeFromDatabase(RecipeDb recipeDb) {
        RecipeComplete recipe = new RecipeComplete();

        recipe.setId(recipeDb.getId());
        recipe.setKey(recipeDb.getKey());
        recipe.setName(recipeDb.getName());
        recipe.setIcon(recipeDb.getIcon());
        recipe.setPicture(recipeDb.getPicture());
        recipe.setTimestamp(recipeDb.getTimestamp());
        recipe.setVegetarian(recipeDb.getVegetarian());
        recipe.setFavourite(recipeDb.getFavourite());
        recipe.setOwner(recipeDb.getOwner());
        recipe.setTimestamp(recipeDb.getTimestamp());
        recipe.setType(recipeDb.getType());
        recipe.setMinutes(recipeDb.getMinutes());
        recipe.setPortions(recipeDb.getPortions());
        recipe.setAuthor(recipeDb.getAuthor());
        recipe.setLink(recipeDb.getLink());
        recipe.setTip(recipeDb.getTip());
        recipe.setLanguage(recipeDb.getLanguage());
        recipe.setIngredients(recipeDb.getIngredientsAsStringList());
        recipe.setSteps(recipeDb.getStepsAsStringList());
        recipe.setEdited(recipeDb.getEdited());
        return recipe;
    }

    // TODO: 26/3/17 poner la receta a  int owner = RecetasCookeoConstants.FLAG_PERSONAL_RECIPE;
    // mantener id, favourite, owner
    public static ContentValues getContentValues(RecipeComplete recipe){
        ContentValues content = new ContentValues();
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_ID, recipe.getId());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_KEY, recipe.getKey());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_NAME, recipe.getName());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_TYPE, recipe.getType());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_ICON, recipe.getIcon());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_PICTURE, recipe.getPicture());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_VEGETARIAN, recipe.getVegetarian());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_FAVOURITE, recipe.getFavourite());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_MINUTES, recipe.getMinutes());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_PORTIONS, recipe.getPortions());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_LANGUAGE, recipe.getLanguage());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_AUTHOR, recipe.getAuthor());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_LINK, recipe.getLink());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_TIP, recipe.getTip());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_OWNER, recipe.getOwner());
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_EDITED, true);
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_TIMESTAMP, recipe.getTimestamp());
        if(recipe.getIngredients() != null){
            for(int i=0; i<recipe.getIngredients().size(); i++){
                content.put(RecetasCookeoConstants.RECIPE_COMPLETE_INGREDIENT + i, recipe.getIngredients().get(i));
            }
        }
        if(recipe.getSteps() != null){
            for(int i=0; i<recipe.getSteps().size(); i++){
                content.put(RecetasCookeoConstants.RECIPE_COMPLETE_STEP + i, recipe.getSteps().get(i));
            }
        }

        return content;
    }

    public static ContentValues getEmptyPersonalValues(String key, String author, boolean edited){
        ContentValues content = new ContentValues();
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_KEY, key);
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_FAVOURITE, false);
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_LANGUAGE, RecetasCookeoConstants.LANG_SPANISH);
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_AUTHOR, author);
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_LINK, "");
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_OWNER, RecetasCookeoConstants.FLAG_PERSONAL_RECIPE);
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_EDITED, edited);
        content.put(RecetasCookeoConstants.RECIPE_COMPLETE_TIMESTAMP, System.currentTimeMillis());

        return content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Boolean getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getPortions() {
        return portions;
    }

    public void setPortions(Integer portions) {
        this.portions = portions;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }


}
