package com.rukiasoft.androidapps.cocinaconroll.persistence.database;

import android.app.Application;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

public class CocinaConRollContentProvider extends ContentProvider {


	public static final String AUTHORITY = "com.rukiasoft.androidapps.cocinaconroll.persistence.database.cocinaconrollcontentprovider";
	public static final Uri CONTENT_URI_SUGGESTIONS_WHEN_KEYBOARD_GO = Uri.parse("content://" + AUTHORITY + "/suggestions");

    private RecipesDB mRecipesDB = null;
    private RecipeController recipeController = null;

    private static final int SUGGESTIONS_RECIPE = 1;
    private static final int SEARCH_SUGGESTION = 2;
    private static final int GET_SUGGESTION = 3;
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

    public static Uri getUriForRecipe(long id) {

        Uri uri = new Uri.Builder().scheme("content")
                .authority(AUTHORITY)
                .appendPath(RecetasCookeoConstants.SEARCH_RECIPE)
                .build();
        return ContentUris.withAppendedId(uri, id);
    }

    private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Suggestion items of Search Dialog is provided by this uri
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS_RECIPE);

        // This URI is invoked, when user presses "Go" in the Keyboard of Search Dialog
        // Listview items of SearchableActivity is provided by this uri
        uriMatcher.addURI(AUTHORITY, "suggestions", SEARCH_SUGGESTION);
        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        uriMatcher.addURI(AUTHORITY, "suggestions/#", GET_SUGGESTION);

        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_ALL, GET_ALL_RECIPES);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_MAIN, GET_MAINS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_STARTERS, GET_STARTERS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_DESSERTS, GET_DESSERTS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_VEGETARIAN, GET_VEGETARIANS);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_FAVOURITES, GET_FAVOURITES);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_OWN, GET_OWN);
        uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.SEARCH_LATEST, GET_LATEST);

        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        //uriMatcher.addURI(AUTHORITY, RecetasCookeoConstants.RECIPES_TABLE_NAME + "/#", GET_RECIPE);

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
        if(getContext() == null){
            return null;
        }
        String id_recipe;
        switch(mUriMatcher.match(uri)){
            case SUGGESTIONS_RECIPE:
                c = mRecipesDB.getSuggestions(selectionArgs);
                break;
            case SEARCH_SUGGESTION:
                c = mRecipesDB.getSuggestions(projection, selection, selectionArgs, sortOrder);
                break;
            case GET_SUGGESTION:
                id_recipe = uri.getLastPathSegment();
                c = mRecipesDB.getSuggestion(id_recipe);
                break;
            case GET_ALL_RECIPES:
                c = recipeController.getRecipesInCursorFormat((Application)getContext().getApplicationContext());
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
        }
        if(c != null && getContext() != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return -1;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        return uri;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        return -1;

    }
}