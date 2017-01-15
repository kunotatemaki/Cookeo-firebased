package com.rukiasoft.androidapps.cocinaconroll.persistence.greendao;

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
public class Ingredient {
    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    @NotNull
    private String key;
    @NotNull
    private String ingredient;
/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;
/** Used for active entity operations. */
@Generated(hash = 942581853)
private transient IngredientDao myDao;
@Generated(hash = 90575321)
public Ingredient(Long id, @NotNull String key, @NotNull String ingredient) {
    this.id = id;
    this.key = key;
    this.ingredient = ingredient;
}
@Generated(hash = 1584798654)
public Ingredient() {
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
@Generated(hash = 1386056592)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getIngredientDao() : null;
}


}
