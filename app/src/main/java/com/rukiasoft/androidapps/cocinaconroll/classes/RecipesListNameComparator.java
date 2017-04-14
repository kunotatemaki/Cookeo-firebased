package com.rukiasoft.androidapps.cocinaconroll.classes;


import java.io.Serializable;

public class RecipesListNameComparator implements java.util.Comparator<RecipeItemOld>, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(RecipeItemOld p1, RecipeItemOld p2) {
        if(p1==null || p1.getName() == null){
            return -1;
        }
        if(p2==null || p2.getName() == null){
            return 1;
        }
        return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
    }
}
