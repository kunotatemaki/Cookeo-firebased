package com.rukiasoft.androidapps.cocinaconroll.classes;


import java.io.Serializable;

public class RecipesListNameComparator implements java.util.Comparator<RecipeItemOld>, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(RecipeItemOld p1, RecipeItemOld p2) {
        return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
    }
}
