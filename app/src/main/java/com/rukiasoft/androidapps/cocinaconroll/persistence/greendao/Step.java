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
        nameInDb = "STEPS"
)
public class Step {
    @Id(autoincrement = true)
    private Long id;
    @Index
    @NotNull
    private String key;
    @Index
    @NotNull
    private int position;
    @NotNull
    private String step;
/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;
/** Used for active entity operations. */
@Generated(hash = 838845936)
private transient StepDao myDao;
@Generated(hash = 1871213660)
public Step(Long id, @NotNull String key, @NotNull String position,
        @NotNull String step) {
    this.id = id;
    this.key = key;
    this.position = position;
    this.step = step;
}
@Generated(hash = 561308863)
public Step() {
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
public String getStep() {
    return this.step;
}
public void setStep(String step) {
    this.step = step;
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
public String getPosition() {
    return this.position;
}
public void setPosition(int position) {
    this.position = position;
}
/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1241179918)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getStepDao() : null;
}


}