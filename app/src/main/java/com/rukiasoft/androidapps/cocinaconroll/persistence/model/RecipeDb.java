package com.rukiasoft.androidapps.cocinaconroll.persistence.model;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;

import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.CommonController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;


@Entity(
        active = true,
        nameInDb = "RECIPES"
)
public class RecipeDb {

    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    @NotNull
    private String key;
    private String name;
    private String normalizedName;
    private String type;
    private Integer icon;
    private String picture;

    @Override
    public String toString() {
        return "RecipeDb{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", normalizedName='" + normalizedName + '\'' +
                ", type='" + type + '\'' +
                ", icon=" + icon +
                ", picture='" + picture + '\'' +
                ", vegetarian=" + vegetarian +
                ", favourite=" + favourite +
                ", minutes=" + minutes +
                ", portions=" + portions +
                ", language=" + language +
                ", author='" + author + '\'' +
                ", link='" + link + '\'' +
                ", tip='" + tip + '\'' +
                ", owner=" + owner +
                ", edited=" + edited +
                ", timestamp=" + timestamp +
                ", updateRecipe=" + updateRecipe +
                ", updatePicture=" + updatePicture +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                '}';
    }

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
    @NotNull
    private Long timestamp;
    @NotNull
    private Integer updateRecipe = RecetasCookeoConstants.FLAG_NOT_UPDATE_RECIPE;
    private Integer updatePicture = RecetasCookeoConstants.FLAG_NOT_UPDATE_PICTURE;

    @ToMany(joinProperties = {
            @JoinProperty(name = "key", referencedName = "key")
    })
    @OrderBy("position ASC")
    private List<IngredientDb> ingredients;

    @ToMany(joinProperties = {
            @JoinProperty(name = "key", referencedName = "key")
    })
    @OrderBy("position ASC")
    private List<StepDb> steps;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 261850341)
    private transient RecipeDbDao myDao;

    public RecipeDb() {
    }

    public static RecipeDb getFromCursor(Application application, Cursor cursor){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        RecipeDbDao recipeDao = session.getRecipeDbDao();
        return recipeDao.readEntity(cursor, 0);
    }

    @Generated(hash = 1012747291)
    public RecipeDb(Long id, @NotNull String key, String name, String normalizedName, String type, Integer icon,
            String picture, Boolean vegetarian, Boolean favourite, Integer minutes, Integer portions, Integer language,
            String author, String link, String tip, Integer owner, Boolean edited, @NotNull Long timestamp,
            @NotNull Integer updateRecipe, Integer updatePicture) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.normalizedName = normalizedName;
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
        this.edited = edited;
        this.timestamp = timestamp;
        this.updateRecipe = updateRecipe;
        this.updatePicture = updatePicture;
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

    public String getNormalizedName() {
        return this.normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
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

    public Boolean getEdited() {
        return this.edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUpdateRecipe() {
        return this.updateRecipe;
    }

    public void setUpdateRecipe(Integer updateRecipe) {
        this.updateRecipe = updateRecipe;
    }

    public Integer getUpdatePicture() {
        return this.updatePicture;
    }

    public void setUpdatePicture(Integer updatePicture) {
        this.updatePicture = updatePicture;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 221742525)
    public List<IngredientDb> getIngredients() {
        if (ingredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IngredientDbDao targetDao = daoSession.getIngredientDbDao();
            List<IngredientDb> ingredientsNew = targetDao._queryRecipeDb_Ingredients(key);
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
    @Generated(hash = 265820585)
    public List<StepDb> getSteps() {
        if (steps == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StepDbDao targetDao = daoSession.getStepDbDao();
            List<StepDb> stepsNew = targetDao._queryRecipeDb_Steps(key);
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

    public static RecipeDb fromContentValues(ContentValues recipe){
        RecipeDb recipeDb = new RecipeDb();
        recipeDb.setId(recipe.getAsLong(RecetasCookeoConstants.RECIPE_COMPLETE_ID));
        recipeDb.setKey(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_KEY));
        recipeDb.setName(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_NAME));
        Tools tools = new Tools();
        recipeDb.setNormalizedName(tools.getNormalizedString(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_NAME)));
        recipeDb.setType(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_TYPE));
        recipeDb.setIcon(recipe.getAsInteger(RecetasCookeoConstants.RECIPE_COMPLETE_ICON));
        recipeDb.setPicture(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_PICTURE));
        recipeDb.setFavourite(recipe.getAsBoolean(RecetasCookeoConstants.RECIPE_COMPLETE_FAVOURITE));
        recipeDb.setVegetarian(recipe.getAsBoolean(RecetasCookeoConstants.RECIPE_COMPLETE_VEGETARIAN));
        recipeDb.setMinutes(recipe.getAsInteger(RecetasCookeoConstants.RECIPE_COMPLETE_MINUTES));
        recipeDb.setPortions(recipe.getAsInteger(RecetasCookeoConstants.RECIPE_COMPLETE_PORTIONS));
        recipeDb.setLanguage(recipe.getAsInteger(RecetasCookeoConstants.RECIPE_COMPLETE_LANGUAGE));
        recipeDb.setAuthor(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_AUTHOR));
        recipeDb.setLink(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_LINK));
        recipeDb.setTip(recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_TIP));
        recipeDb.setOwner(recipe.getAsInteger(RecetasCookeoConstants.RECIPE_COMPLETE_OWNER));
        recipeDb.setTimestamp(System.currentTimeMillis());
        recipeDb.setEdited(recipe.getAsBoolean(RecetasCookeoConstants.RECIPE_COMPLETE_EDITED));
        recipeDb.setUpdateRecipe(0);
        recipeDb.setUpdatePicture(0);
        int i = 0;
        List<String> ingredients = new ArrayList<>();
        while(recipe.containsKey(RecetasCookeoConstants.RECIPE_COMPLETE_INGREDIENT + i)){
            String ingredient = recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_INGREDIENT + i);
            ingredients.add(ingredient);
            i++;
        }
        recipeDb.setIngredients(
                RecipeDb.getIngredientsFromList(ingredients, recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_KEY))
        );
        i = 0;
        List<String> steps = new ArrayList<>();
        while(recipe.containsKey(RecetasCookeoConstants.RECIPE_COMPLETE_STEP + i)){
            String step = recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_STEP + i);
            steps.add(step);
            i++;
        }
        recipeDb.setSteps(
                RecipeDb.getStepsFromList(steps, recipe.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_KEY))
        );
        return recipeDb;
    }

    public RecipeDb(RecipeFirebase recipe, String key, Integer owner){
        this.key = key;
        this.owner = owner;
        this.name = recipe.getName();
        Tools tools = new Tools();
        this.normalizedName = tools.getNormalizedString(recipe.getName());
        this.type = recipe.getType();
        this.icon = Tools.getIconFromType(recipe.getType());
        this.picture = recipe.getPicture()!=null? recipe.getPicture() : RecetasCookeoConstants.DEFAULT_PICTURE_NAME;
        this.updatePicture = this.picture.equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME)?
                0 : RecetasCookeoConstants.FLAG_DOWNLOAD_PICTURE;
        this.vegetarian = recipe.getVegetarian();
        this.favourite = false;
        this.updateRecipe = RecetasCookeoConstants.FLAG_NOT_UPDATE_PICTURE;
        this.timestamp = System.currentTimeMillis();
        this.author = recipe.getAuthor();
        this.minutes = recipe.getMinutes();
        this.portions = recipe.getPortions();
        this.tip = recipe.getTip();
        this.language = recipe.getLanguage();
        this.link = recipe.getLink();
        this.edited = false;
        if(recipe.getIngredients() != null) {
            this.ingredients = RecipeDb.getIngredientsFromList(recipe.getIngredients(), this.key);
        }else{
            this.ingredients = new ArrayList<>();
        }
        if(recipe.getSteps() != null) {
            this.steps = RecipeDb.getStepsFromList(recipe.getSteps(), this.key);
        }else{
            this.steps = new ArrayList<>();
        }

    }



    private static List<IngredientDb> getIngredientsFromList(List<String> ingredients, String key){
        List<IngredientDb> mIngredients = new ArrayList<>();
        for(int i=0; i<ingredients.size(); i++){
            IngredientDb ingredientDb = new IngredientDb();
            ingredientDb.setIngredient(ingredients.get(i));
            ingredientDb.setPosition(i);
            ingredientDb.setKey(key);
            mIngredients.add(ingredientDb);
        }
        return mIngredients;
    }

    private static List<StepDb> getStepsFromList(List<String> steps, String key){
        List<StepDb> mSteps = new ArrayList<>();
        for(int i=0; i<steps.size(); i++){
            StepDb stepDb = new StepDb();
            stepDb.setStep(steps.get(i));
            stepDb.setPosition(i);
            stepDb.setKey(key);
            mSteps.add(stepDb);
        }
        return mSteps;

    }


    public void setIngredients(List<IngredientDb> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSteps(List<StepDb> steps) {
        this.steps = steps;
    }



    public List<String> getStepsAsStringList(){
        List<String> list = new ArrayList<>();
        for(StepDb stepDb : steps){
            list.add(stepDb.getStep());
        }
        return list;
    }

    public List<String> getIngredientsAsStringList(){
        List<String> list = new ArrayList<>();
        for(IngredientDb ingredientDb : ingredients){
            list.add(ingredientDb.getIngredient());
        }
        return list;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 115976254)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecipeDbDao() : null;
    }

    

}