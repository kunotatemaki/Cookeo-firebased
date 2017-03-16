package com.rukiasoft.androidapps.cocinaconroll.database;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.util.HashMap;

public class RecipesDB {

    private final CocinaConRollDatabaseHelper mCocinaConRollDatabaseHelper;

	private final HashMap<String, String> mAliasMap;


	public RecipesDB(Context context){
        mCocinaConRollDatabaseHelper = new CocinaConRollDatabaseHelper(context);
		
		// This HashMap is used to map table fields to Custom Suggestion fields
    	mAliasMap = new HashMap<>();
    	
    	// Unique id for the each Suggestions ( Mandatory ) 
    	mAliasMap.put("_ID", RecetasCookeoConstants.FIELD_ID + " as " + "_id" );
    	
    	// Text for Suggestions ( Mandatory )
    	mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1,  RecetasCookeoConstants.FIELD_NAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
    	
    	// Icon for Suggestions ( Optional ) 
    	mAliasMap.put(SearchManager.SUGGEST_COLUMN_ICON_1, RecetasCookeoConstants.FIELD_ICON + " as " + SearchManager.SUGGEST_COLUMN_ICON_1);
    	
    	// This value will be appended to the Intent data on selecting an item from Search result or Suggestions ( Optional )
    	mAliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, RecetasCookeoConstants.FIELD_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	}
		

	/** Returns Recipes  */
    public Cursor getSuggestions(String[] selectionArgs){
        //call from search widget
    	String selection =  RecetasCookeoConstants.FIELD_NAME_NORMALIZED + " like ? ";
        Tools tools = new Tools();
        if(selectionArgs!=null){
    		selectionArgs[0] = "%" + tools.getNormalizedString(selectionArgs[0]) + "%";
    	}
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	queryBuilder.setProjectionMap(mAliasMap);
    	
    	queryBuilder.setTables(RecetasCookeoConstants.RECIPES_TABLE_NAME);

		return queryBuilder.query(mCocinaConRollDatabaseHelper.getReadableDatabase(),
                new String[]{"_ID",
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_ICON_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID},
                selection,
                selectionArgs,
                null,
                null,
                RecetasCookeoConstants.FIELD_NAME_NORMALIZED + " asc ", "50"
        );
    }

    /** Returns Suggestions  */
    public Cursor getSuggestions(String[] projection, String selection,
                             String[] selectionArgs, String sortOrder){

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RecetasCookeoConstants.RECIPES_TABLE_NAME);

        Tools tools = new Tools();
        if(selection == null){
        //call from search widget when pressed, when user presses "Go" in the Keyboard of Search Dialog
            selection =  RecetasCookeoConstants.FIELD_NAME_NORMALIZED + " like ? ";
            if(selectionArgs!=null){
                for(int i=0; i<selectionArgs.length; i++){
                    selectionArgs[i] = "%"+ tools.getNormalizedString(selectionArgs[i]) + "%";
                }
            }
        }
        if(projection == null){
            //call from search widget when pressed, when user presses "Go" in the Keyboard of Search Dialog
            queryBuilder.setProjectionMap(mAliasMap);
            projection = new String[]{"_ID",
                    SearchManager.SUGGEST_COLUMN_TEXT_1,
                    SearchManager.SUGGEST_COLUMN_ICON_1,
                    SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};
        }
        if(sortOrder == null){
            sortOrder = RecetasCookeoConstants.FIELD_NAME_NORMALIZED + " asc ";
        }


        return queryBuilder.query(mCocinaConRollDatabaseHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /** Return Suggestion corresponding to the id */
    public Cursor getSuggestion(String id){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables( RecetasCookeoConstants.RECIPES_TABLE_NAME);
        return queryBuilder.query(mCocinaConRollDatabaseHelper.getReadableDatabase(),
                new String[]{RecetasCookeoConstants.FIELD_ID, RecetasCookeoConstants.FIELD_NAME,
                        RecetasCookeoConstants.FIELD_NAME_NORMALIZED, RecetasCookeoConstants.FIELD_ICON},
                RecetasCookeoConstants.FIELD_ID + " = ?", new String[]{id}, null, null, null, "1"
        );
    }


}