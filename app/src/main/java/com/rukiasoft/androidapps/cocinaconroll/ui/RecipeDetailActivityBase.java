package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rukiasoft.androidapps.cocinaconroll.BuildConfig;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeComplete;
import com.rukiasoft.androidapps.cocinaconroll.utilities.CommonRecipeOperations;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeDetailActivityBase extends ToolbarAndProgressActivity {



    @BindView(R.id.adview_details)
    AdView mAdViewDetails;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        unbinder = ButterKnife.bind(this);
        Intent intent = getIntent();
        if(intent == null || !intent.hasExtra(RecetasCookeoConstants.KEY_RECIPE)){
            finish();
        }
        RecipeComplete recipeComplete = getIntent().getExtras().getParcelable(RecetasCookeoConstants.KEY_RECIPE);

        RecipeDetailsFragment recipeDetailsFragment = (RecipeDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_recipes_fragment);
        if(recipeDetailsFragment != null){
            recipeDetailsFragment.setRecipe(recipeComplete);
        }else{
            finish();
        }
        //set up advertises
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice(BuildConfig.Z3_DEVICE_ID)  // My Xperia Z3 test device
                .setGender(AdRequest.GENDER_FEMALE)
                .build();

        mAdViewDetails.loadAd(adRequest);

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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        if(requestCode == RecetasCookeoConstants.REQUEST_EDIT_RECIPE){
            if(resultCode == RecetasCookeoConstants.RESULT_UPDATE_RECIPE && intentData != null && intentData.hasExtra(RecetasCookeoConstants.KEY_RECIPE)){
                // TODO: 27/2/17 salvar la receta
                /*RecipeComplete recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
                RecipeDetailsFragment recipeDetailsFragment = (RecipeDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_recipes_fragment);
                if(recipeDetailsFragment != null)
                    recipeDetailsFragment.updateRecipe(recipe);
                //save recipe
                CommonRecipeOperations commonRecipeOperations = new CommonRecipeOperations(this, recipe);
                String oldPicture = "";
                if(intentData.hasExtra(RecetasCookeoConstants.KEY_DELETE_OLD_PICTURE)){
                    oldPicture = intentData.getStringExtra(RecetasCookeoConstants.KEY_DELETE_OLD_PICTURE);
                }
                commonRecipeOperations.updateRecipe(oldPicture);
                //set results
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(RecetasCookeoConstants.KEY_RECIPE, recipe);
                returnIntent.putExtras(bundle);
                setResult(RecetasCookeoConstants.RESULT_UPDATE_RECIPE, returnIntent);*/
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RecetasCookeoConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RecipeDetailsFragment mRecipeDetailsFragment = (RecipeDetailsFragment) getSupportFragmentManager().
                            findFragmentById(R.id.details_recipes_fragment);
                    if(mRecipeDetailsFragment != null) {
                        mRecipeDetailsFragment.editRecipe();
                    }
                } else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(this);

                    builder.setMessage(getResources().getString(R.string.write_external_denied))
                            .setTitle(getResources().getString(R.string.permissions_title))
                            .setPositiveButton(getResources().getString(R.string.accept),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    builder.create().show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
