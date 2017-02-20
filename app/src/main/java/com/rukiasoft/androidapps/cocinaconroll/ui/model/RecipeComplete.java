package com.rukiasoft.androidapps.cocinaconroll.ui.model;

import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDb;

import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by iRoll on 19/2/17.
 */

public class RecipeComplete {
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
    private List<IngredientDb> ingredients;
    private List<StepDb> steps;

    public RecipeComplete() {
    }

    public RecipeComplete(RecipeDb recipeDb) {
        this.id = recipeDb.getId();
        this.key = recipeDb.getKey();
        this.name = recipeDb.getName();
        this.type = recipeDb.getType();
        this.icon = recipeDb.getIcon();
        this.picture = recipeDb.getPicture();
        this.vegetarian = recipeDb.getVegetarian();
        this.favourite = recipeDb.getFavourite();
        this.minutes = recipeDb.getMinutes();
        this.portions = recipeDb.getPortions();
        this.language = recipeDb.getLanguage();
        this.author = recipeDb.getAuthor();
        this.link = recipeDb.getLink();
        this.tip = recipeDb.getTip();
        this.owner = recipeDb.getOwner();
        this.ingredients = recipeDb.getIngredients();
        this.steps = recipeDb.getSteps();
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

    public List<IngredientDb> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDb> ingredients) {
        this.ingredients = ingredients;
    }

    public List<StepDb> getSteps() {
        return steps;
    }

    public void setSteps(List<StepDb> steps) {
        this.steps = steps;
    }
}
