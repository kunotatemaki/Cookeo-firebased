package com.rukiasoft.androidapps.cocinaconroll.database;

import android.app.Application;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

public class CocinaConRollContentProvider extends ContentProvider {


	public static final String AUTHORITY = "com.rukiasoft.androidapps.cocinaconroll.database.cocinaconrollcontentprovider";
	public static final Uri CONTENT_URI_SUGGESTIONS_WHEN_KEYBOARD_GO = Uri.parse("content://" + AUTHORITY + "/suggestions");
	//public static final Uri CONTENT_URI_RECIPES = Uri.parse("content://" + AUTHORITY + "/" + RecipesTable.TABLE_NAME);

    private RecipesDB mRecipesDB = null;
    private RecipeController recipeController = null;

    private static final int COINCIDENCES = 1;
    private static final int GET_SEARCHED_RECIPE = 3;
    private static final int GET_ALL_RECIPES = 4;
    private static final int GET_RECIPE = 5;
    private static final int GET_MAINS = 6;
    private static final int GET_STARTERS = 7;
    private static final int GET_DESSERTS = 8;
    private static final int GET_VEGETARIANS = 9;
    private static final int GET_FAVOURITES = 10;
    private static final int GET_OWN = 11;
    private static final int GET_LATEST = 12;

    private final UriMatcher mUriMatcher = buildUriMatcher();

    public static Uri getUri(String lastPaht){
        return Uri.parse("content://" + AUTHORITY + "/" + lastPaht);
    }

    private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Suggestion items of Search Dialog is provided by this uri
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, COINCIDENCES);

        // This URI is invoked, when user presses "Go" in the Keyboard of Search Dialog
        // Listview items of SearchableActivity is provided by this uri
        uriMatcher.addURI(AUTHORITY, "suggestions", COINCIDENCES);
        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        uriMatcher.addURI(AUTHORITY, "suggestions/#", GET_SEARCHED_RECIPE);

        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_ALL, GET_ALL_RECIPES);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_MAIN, GET_MAINS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_STARTERS, GET_STARTERS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_DESSERTS, GET_DESSERTS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_VEGETARIAN, GET_VEGETARIANS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_FAVOURITES, GET_FAVOURITES);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_OWN, GET_OWN);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_LATEST, GET_LATEST);

        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        uriMatcher.addURI(AUTHORITY, RecipesTable.TABLE_NAME + "/#", GET_RECIPE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mRecipesDB = new RecipesDB(getContext());
        recipeController = new RecipeController();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor c = null;
        String id_recipe;
        switch(mUriMatcher.match(uri)){
            case COINCIDENCES:
                c = recipeController.getRecipesByName((Application)getContext().getApplicationContext(), selectionArgs[0]);
                break;
            case GET_SEARCHED_RECIPE:
                id_recipe = uri.getLastPathSegment();
                c = mRecipesDB.getSuggestion(id_recipe);
                break;
            case GET_ALL_RECIPES:
                c = recipeController.getRecipesInCursorFormat((Application)getContext().getApplicationContext());
                //c = mRecipesDB.getRecipes(projection, selection, selectionArgs, sortOrder);
                break;
            case GET_STARTERS:
                c = recipeController.getRecipesByTypeInCursorFormat((Application)getContext().getApplicationContext(),
                        RecetasCookeoConstants.TYPE_STARTERS);
                break;
            case GET_MAINS:
                c = recipeController.getRecipesByTypeInCursorFormat((Application)getContext().getApplicationContext(),
                        RecetasCookeoConstants.TYPE_MAIN);
                break;
            case GET_DESSERTS:
                c = recipeController.getRecipesByTypeInCursorFormat((Application)getContext().getApplicationContext(),
                        RecetasCookeoConstants.TYPE_DESSERTS);
                break;
            case GET_FAVOURITES:
                c = recipeController.getFavouriteRecipesInCursorFormat((Application)getContext().getApplicationContext());
                break;
            case GET_VEGETARIANS:
                c = recipeController.getVegetarianRecipesInCursorFormat((Application)getContext().getApplicationContext());
                break;
            case GET_OWN:
                c = recipeController.getOwnRecipesInCursorFormat((Application)getContext().getApplicationContext());
                break;
            case GET_LATEST:
                c = recipeController.getLatestRecipesInCursorFormat((Application)getContext().getApplicationContext());
                break;
            case GET_RECIPE:
                id_recipe = uri.getLastPathSegment();
                c = mRecipesDB.getRecipe(id_recipe);
                break;
        }
        return c;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (mUriMatcher.match(uri)){
            case GET_ALL_RECIPES:
                return mRecipesDB.delete(selection, selectionArgs);
            default: throw new SQLException("Failed to delete row " + uri);
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri returnUri;
        switch (mUriMatcher.match(uri)){
            case GET_ALL_RECIPES:
                returnUri = mRecipesDB.insert(values);
                break;
            default: throw new SQLException("Failed to insert row into " + uri);
        }
        return returnUri;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int index;
        switch (mUriMatcher.match(uri)){
            case GET_ALL_RECIPES:
                index = mRecipesDB.updateFavorite(values, selection, selectionArgs);
                break;
            default: throw new SQLException("Failed to insert row into " + uri);
        }
        return index;

    }
}