package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rukiasoft.androidapps.cocinaconroll.BuildConfig;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeComplete;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeDetailActivity extends ToolbarAndProgressActivity {




    @BindView(R.id.adview_details)
    AdView mAdViewDetails;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        unbinder = ButterKnife.bind(this);
        if(getIntent() == null || getIntent().getData() == null){
            finish();
            return;
        }

        //set up advertises
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice(BuildConfig.Z3_DEVICE_ID)  // My Xperia Z3 test device
                .setGender(AdRequest.GENDER_FEMALE)
                .build();

        mAdViewDetails.loadAd(adRequest);

    }

    public long getRecipeId(){
        Uri uri = getIntent().getData();
        return ContentUris.parseId(uri);
    }

    public RecipeComplete getRecipe(){
        RecipeComplete recipeComplete =  RecipeComplete.getRecipeFromDatabase(
                new RecipeController().getRecipeById(getApplication(), getRecipeId())
        );
        if(recipeComplete == null){
            finish();
            return null;
        }
        return recipeComplete;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RecetasCookeoConstants.REQUEST_EDIT_RECIPE:
                if(resultCode == Activity.RESULT_OK){
                    RecipeComplete recipe = getRecipe();
                    RecipeDetailsFragment recipeDetailsFragment = (RecipeDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_recipes_fragment);

                    recipeDetailsFragment.loadRecipe(recipe);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onResume() {

        super.onResume();
        Tools tools = new Tools();
        tools.setScreenOnIfSettingsAllowed(this, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Tools tools = new Tools();
        tools.setScreenOnIfSettingsAllowed(this, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
