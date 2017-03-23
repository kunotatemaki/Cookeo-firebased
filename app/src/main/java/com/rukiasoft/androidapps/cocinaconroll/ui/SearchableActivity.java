package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.SimpleDividerItemDecoration;
import com.rukiasoft.androidapps.cocinaconroll.persistence.database.CocinaConRollContentProvider;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeSearch;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchableActivity extends ToolbarAndProgressActivity implements LoaderCallbacks<Cursor>,
SearchRecyclerViewAdapter.OnItemClickListener{
	
	@BindView(R.id.lv_recipes)
	RecyclerView mLVRecipes;
	@BindView(R.id.standard_toolbar)
	Toolbar mToolbarSearchActivity;
	private Unbinder unbinder;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbinder.unbind();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_searchable);
		unbinder = ButterKnife.bind(this);
		setToolbar(mToolbarSearchActivity);


		// Getting the intent that invoked this activity
		Intent intent = getIntent();		
		
		// If this activity is invoked by selecting an item from Suggestion of Search dialog or 
		// from listview of SearchActivity
		if(intent.getAction().equals(Intent.ACTION_VIEW)){
            Uri uri = intent.getData();
            sendRecipe(uri.getLastPathSegment());

		}else if(intent.getAction().equals(Intent.ACTION_SEARCH)){ // If this activity is invoked, when user presses "Go" in the Keyboard of Search Dialog
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}		
	}

    private void sendRecipe(String sId){
		long id;
		try {
			id = Long.valueOf(sId);
		}catch (NumberFormatException e){
			Logger.d("No se puede convertir a long el valor: " + sId);
			return;
		}
		sendRecipe(id);
    }
	
	private void sendRecipe(long id){

		Intent detailIntent = new Intent(this, RecipeListActivity.class);
        detailIntent.putExtra(RecetasCookeoConstants.KEY_RECIPE, id);
		detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(detailIntent);
        finish();
    }

	private void doSearch(String query){
		Bundle data = new Bundle();
		data.putString("query", query);
		
		// Invoking onCreateLoader() in non-ui thread
		getSupportLoaderManager().initLoader(1, data, this);		
	}


	/** This method is invoked by initLoader() */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle data) {
		Uri uri = CocinaConRollContentProvider.CONTENT_URI_SUGGESTIONS_WHEN_KEYBOARD_GO;
		return new CursorLoader(getBaseContext(), uri, null, null , new String[]{data.getString("query")}, null);
	}

	/** This method is executed in ui thread, after onCreateLoader() */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		List<RecipeSearch> recipeList = getRecipesFromCursor(c);
		setData(recipeList);
	}


	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private void setData(List<RecipeSearch> recipes){
		SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(recipes);
		adapter.setHasStableIds(true);
		adapter.setOnItemClickListener(this);
		mLVRecipes.setHasFixedSize(true);

		mLVRecipes.setAdapter(adapter);

        mLVRecipes.addItemDecoration(new SimpleDividerItemDecoration(this));
		mLVRecipes.setLayoutManager(
				new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


	}

	@Override
	public void onItemClick(View view, RecipeSearch recipe) {
		sendRecipe(recipe.getId());
	}

	public List<RecipeSearch> getRecipesFromCursor(Cursor cursor) {
		List<RecipeSearch> list = new ArrayList<>();
		if(cursor != null && cursor.moveToFirst()){
			do {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID));
				String name = cursor.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1));
				int icon = cursor.getInt(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_ICON_1));

				RecipeSearch recipe = RecipeSearch.create(id, name, icon);

				list.add(recipe);
			}while(cursor.moveToNext());
			cursor.close();
		}

		return list;
	}
}