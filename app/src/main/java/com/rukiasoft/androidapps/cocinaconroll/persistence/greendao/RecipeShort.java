package com.rukiasoft.androidapps.cocinaconroll.persistence.greendao;

import android.bluetooth.BluetoothDevice;

import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.RecipeDetailed;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;


@Entity(
        active = true,
        nameInDb = "SHORT_RECIPES"
)
public class RecipeShort {

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
    @NotNull
    private Long timestamp;
    @NotNull
    private Boolean downloadRecipe = false;
    private Boolean downloadPicture;
/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;
/** Used for active entity operations. */
@Generated(hash = 1756051443)
private transient RecipeShortDao myDao;

    public RecipeShort() {
    }

    public RecipeShort(RecipeDetailed recipe, String key){
        this.key = key;
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
        // TODO: 19/1/17 comprobar si el timestamp actual es mayor o igual que el de la receta de firebase, y poner el mayor
        this.timestamp = System.currentTimeMillis();
    }

@Generated(hash = 619197396)
public RecipeShort(Long id, @NotNull String key, String name, String type,
        Integer icon, String picture, Boolean vegetarian, Boolean favourite,
        @NotNull Long timestamp, @NotNull Boolean downloadRecipe,
        Boolean downloadPicture) {
    this.id = id;
    this.key = key;
    this.name = name;
    this.type = type;
    this.icon = icon;
    this.picture = picture;
    this.vegetarian = vegetarian;
    this.favourite = favourite;
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
@Generated(hash = 1159410144)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getRecipeShortDao() : null;
}

}