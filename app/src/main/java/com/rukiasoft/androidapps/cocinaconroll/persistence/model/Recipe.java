package com.rukiasoft.androidapps.cocinaconroll.persistence.model;

import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeDetailed;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;


@Entity(
        active = true,
        nameInDb = "RECIPES"
)
public class Recipe {

    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    @NotNull
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
    @NotNull
    private Long timestamp;
    @NotNull
    private Boolean downloadRecipe = false;
    private Boolean downloadPicture;

    @ToMany(joinProperties = {
            @JoinProperty(name = "key", referencedName = "key")
    })
    @OrderBy("position ASC")
    private List<Ingredient> ingredients;

    @ToMany(joinProperties = {
            @JoinProperty(name = "key", referencedName = "key")
    })
    @OrderBy("position ASC")
    private List<Step> steps;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1947830398)
    private transient RecipeDao myDao;
    /** Used to resolve relations */


    public Recipe() {
    }

    public Recipe(RecipeDetailed recipe, String key, Integer owner){
        this.key = key;
        this.owner = owner;
        this.name = recipe.getName();
        this.type = recipe.getType();
        switch (recipe.getType()) {
            case RecetasCookeoConstants.TYPE_DESSERTS:
                this.icon = R.drawable.ic_dessert_24;
                break;
            case RecetasCookeoConstants.TYPE_STARTERS:
                this.icon = R.drawable.ic_starters_24;
                break;
            case RecetasCookeoConstants.TYPE_MAIN:
                this.icon = R.drawable.ic_main_24;
                break;
            default:
                this.icon = R.drawable.ic_all_24;
                break;
        }
        this.picture = recipe.getPicture()!=null? recipe.getPicture() : RecetasCookeoConstants.DEFAULT_PICTURE_NAME;
        this.downloadPicture = !this.picture.equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME);
        this.vegetarian = recipe.getVegetarian();
        this.favourite = false;
        this.downloadRecipe = false;
        this.timestamp = System.currentTimeMillis();
        this.author = recipe.getAuthor();
        this.minutes = recipe.getMinutes();
        this.portions = recipe.getPortions();
        this.tip = recipe.getTip();
        this.language = recipe.getLanguage();
        this.link = recipe.getLink();
//        this.ingredients = new ArrayList<>();
//        for(int i=0; i<recipe.getIngredients().size(); i++){
//            Ingredient ingredient = new Ingredient();
//            ingredient.setIngredient(recipe.getIngredients().get(i));
//            ingredient.setPosition(i);
//            ingredient.setKey(key);
//            this.ingredients.add(ingredient);
//        }
//        this.steps = new ArrayList<>();
//        for(int i=0; i<recipe.getSteps().size(); i++){
//            Step step = new Step();
//            step.setStep(recipe.getSteps().get(i));
//            step.setPosition(i);
//            step.setKey(key);
//            this.steps.add(step);
//        }

    }

    @Generated(hash = 2012203826)
    public Recipe(Long id, @NotNull String key, String name, String type, Integer icon, String picture, Boolean vegetarian,
            Boolean favourite, Integer minutes, Integer portions, Integer language, String author, String link, String tip,
            Integer owner, @NotNull Long timestamp, @NotNull Boolean downloadRecipe, Boolean downloadPicture) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.picture = picture;
        this.vegetarian = vegetarian;
        this.favourite = favourite;
        this.minutes = minutes;
        this.portions = portions;
        this.language = language;
        this.author = author;
        this.link = link;
        this.tip = tip;
        this.owner = owner;
        this.timestamp = timestamp;
        this.downloadRecipe = downloadRecipe;
        this.downloadPicture = downloadPicture;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIcon() {
        return this.icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Boolean getVegetarian() {
        return this.vegetarian;
    }

    public void setVegetarian(Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public Boolean getFavourite() {
        return this.favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Integer getMinutes() {
        return this.minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getPortions() {
        return this.portions;
    }

    public void setPortions(Integer portions) {
        this.portions = portions;
    }

    public Integer getLanguage() {
        return this.language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTip() {
        return this.tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Integer getOwner() {
        return this.owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getDownloadRecipe() {
        return this.downloadRecipe;
    }

    public void setDownloadRecipe(Boolean downloadRecipe) {
        this.downloadRecipe = downloadRecipe;
    }

    public Boolean getDownloadPicture() {
        return this.downloadPicture;
    }

    public void setDownloadPicture(Boolean downloadPicture) {
        this.downloadPicture = downloadPicture;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1528725721)
    public List<Ingredient> getIngredients() {
        if (ingredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IngredientDao targetDao = daoSession.getIngredientDao();
            List<Ingredient> ingredientsNew = targetDao._queryRecipe_Ingredients(key);
            synchronized (this) {
                if (ingredients == null) {
                    ingredients = ingredientsNew;
                }
            }
        }
        return ingredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 183837919)
    public synchronized void resetIngredients() {
        ingredients = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1983973260)
    public List<Step> getSteps() {
        if (steps == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StepDao targetDao = daoSession.getStepDao();
            List<Step> stepsNew = targetDao._queryRecipe_Steps(key);
            synchronized (this) {
                if (steps == null) {
                    steps = stepsNew;
                }
            }
        }
        return steps;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1662411893)
    public synchronized void resetSteps() {
        steps = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1484851246)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecipeDao() : null;
    }

}