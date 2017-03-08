package com.rukiasoft.androidapps.cocinaconroll.persistence.model;

import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeComplete;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;


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
    private Boolean downloadRecipe = false;
    private Boolean downloadPicture;

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

    public static RecipeDb fromRecipeComplete(RecipeComplete recipeComplete){
        RecipeDb recipeDb = new RecipeDb();
        recipeDb.setId(recipeComplete.getId());
        recipeDb.setKey(recipeComplete.getKey());
        recipeDb.setName(recipeComplete.getName());
        Tools tools = new Tools();
        recipeDb.setNormalizedName(tools.getNormalizedString(recipeComplete.getName()));
        recipeDb.setType(recipeComplete.getType());
        recipeDb.setIcon(recipeComplete.getIcon());
        recipeDb.setPicture(recipeComplete.getPicture());
        recipeDb.setFavourite(recipeComplete.getFavourite());
        recipeDb.setVegetarian(recipeComplete.getVegetarian());
        recipeDb.setMinutes(recipeComplete.getMinutes());
        recipeDb.setPortions(recipeComplete.getPortions());
        recipeDb.setLanguage(recipeComplete.getLanguage());
        recipeDb.setAuthor(recipeComplete.getAuthor());
        recipeDb.setLink(recipeComplete.getLink());
        recipeDb.setTip(recipeComplete.getTip());
        recipeDb.setOwner(recipeComplete.getOwner());
        recipeDb.setIngredients(RecipeDb.addIngredients(recipeComplete.getIngredients(), recipeComplete.getKey()));
        recipeDb.setSteps(RecipeDb.addSteps(recipeComplete.getSteps(), recipeComplete.getKey()));
        recipeDb.setTimestamp(System.currentTimeMillis());
        recipeDb.setEdited(recipeComplete.getEdited());
        recipeDb.setDownloadRecipe(false);
        recipeDb.setDownloadPicture(false);
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
        this.edited = false;
        this.ingredients = RecipeDb.addIngredients(recipe.getIngredients(), this.key);
        this.steps = RecipeDb.addSteps(recipe.getSteps(), this.key);

    }

    private static List<IngredientDb> addIngredients(List<String> ingredients, String key){
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

    private static List<StepDb> addSteps(List<String> steps, String key){
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

    @Generated(hash = 1803456598)
    public RecipeDb(Long id, @NotNull String key, String name, String normalizedName, String type, Integer icon,
            String picture, Boolean vegetarian, Boolean favourite, Integer minutes, Integer portions, Integer language,
            String author, String link, String tip, Integer owner, Boolean edited, @NotNull Long timestamp,
            @NotNull Boolean downloadRecipe, Boolean downloadPicture) {
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

    String getNormalizedName() {
        return this.normalizedName;
    }

    void setNormalizedName(String normalizedName) {
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

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    Boolean getDownloadRecipe() {
        return this.downloadRecipe;
    }

    public void setDownloadRecipe(Boolean downloadRecipe) {
        this.downloadRecipe = downloadRecipe;
    }

    Boolean getDownloadPicture() {
        return this.downloadPicture;
    }

    public void setIngredients(List<IngredientDb> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSteps(List<StepDb> steps) {
        this.steps = steps;
    }

    public void setDownloadPicture(Boolean downloadPicture) {
        this.downloadPicture = downloadPicture;
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

    public Boolean getEdited() {
        return this.edited;
    }

    void setEdited(Boolean edited) {
        this.edited = edited;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 115976254)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecipeDbDao() : null;
    }
    
}