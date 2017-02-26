package com.rukiasoft.androidapps.cocinaconroll.classes;


import java.io.Serializable;

public class RecipesListDateComparator implements java.util.Comparator<RecipeItemOld>, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Override
    public int compare(RecipeItemOld p1, RecipeItemOld p2) {
        if(p1.getDate() == null)
            return 1;
        if(p2.getDate() == null)
            return -1;
        return p2.getDate().compareTo(p1.getDate());
    }
}
