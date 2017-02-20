package com.rukiasoft.androidapps.cocinaconroll.persistence.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by iRoll on 15/1/17.
 */

@Entity(
        active = true,
        nameInDb = "INGREDIENTS"
)
public class IngredientDb {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String key;
    @Index
    @NotNull
    private int position;
    @NotNull
    private String ingredient;
/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;
/** Used for active entity operations. */
@Generated(hash = 277625371)
private transient IngredientDbDao myDao;
@Generated(hash = 779520936)
public IngredientDb(Long id, @NotNull String key, int position,
        @NotNull String ingredient) {
    this.id = id;
    this.key = key;
    this.position = position;
    this.ingredient = ingredient;
}
@Generated(hash = 775569234)
public IngredientDb() {
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
public int getPosition() {
    return this.position;
}
public void setPosition(int position) {
    this.position = position;
}
public String getIngredient() {
    return this.ingredient;
}
public void setIngredient(String ingredient) {
    this.ingredient = ingredient;
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
@Generated(hash = 2004575383)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getIngredientDbDao() : null;
}

}
