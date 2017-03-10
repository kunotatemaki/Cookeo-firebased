package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeComplete;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.State;


public class EditRecipeActivity extends AppCompatActivity {

    private EditRecipePhotoFragment editRecipePhotoFragment;
    private EditRecipeIngredientsFragment editRecipeIngredientsFragment;
    private EditRecipeStepsFragment editRecipeStepsFragment;
    RecipeComplete recipe;
    @State String title;
    @BindView(R.id.standard_toolbar) Toolbar mToolbar;
    @State String oldPicture;
    private Unbinder unbinder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate");
        if(recipe == null && getIntent() != null && getIntent().hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
            recipe = getIntent().getExtras().getParcelable(RecetasCookeoConstants.KEY_RECIPE);
            //check if the picture is previosly edited, to delete the old picture
            if(recipe == null){
                setResult(RESULT_CANCELED);
                finish();
            }
            if(recipe.getOwner().equals(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE)){
                oldPicture = recipe.getPicture();
            }
            title = getResources().getString(R.string.edit_recipe);
        }else{
            title = getResources().getString(R.string.create_recipe);
        }

        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            editRecipeIngredientsFragment = (EditRecipeIngredientsFragment) getSupportFragmentManager().findFragmentByTag(EditRecipeIngredientsFragment.class.getSimpleName());
            editRecipeStepsFragment = (EditRecipeStepsFragment) getSupportFragmentManager().findFragmentByTag(EditRecipeStepsFragment.class.getSimpleName());
            editRecipePhotoFragment = (EditRecipePhotoFragment) getSupportFragmentManager().findFragmentByTag(EditRecipePhotoFragment.class.getSimpleName());
        }

        setContentView(R.layout.activity_edit_recipe);
        unbinder = ButterKnife.bind(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setTitle(title);
            }
        }

        try {
            Field f;
            if (mToolbar != null) {
                f = mToolbar.getClass().getDeclaredField("mTitleTextView");
                f.setAccessible(true);
                TextView titleTextView = (TextView) f.get(mToolbar);
                titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                titleTextView.setFocusable(true);
                titleTextView.setFocusableInTouchMode(true);
                titleTextView.requestFocus();
                titleTextView.setSingleLine(true);
                titleTextView.setSelected(true);
                titleTextView.setMarqueeRepeatLimit(-1);
            }

        } catch (NoSuchFieldException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        if (savedInstanceState == null) {
            if(editRecipePhotoFragment == null) {
                editRecipePhotoFragment = new EditRecipePhotoFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.edit_recipe_container, editRecipePhotoFragment, EditRecipePhotoFragment.class.getSimpleName())
                        .commit();
            }
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Tools tools = new Tools();
        tools.hideSoftKeyboard(this);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            case R.id.menu_edit_recipe:
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.edit_recipe_container);
                if (f instanceof EditRecipePhotoFragment) {
                    if (!(((EditRecipePhotoFragment) f).checkInfoOk())) {
                        return super.onOptionsItemSelected(item);
                    }
                    editRecipeIngredientsFragment = (EditRecipeIngredientsFragment) getSupportFragmentManager().findFragmentByTag(EditRecipeIngredientsFragment.class.getSimpleName());
                    if(editRecipeIngredientsFragment == null) {
                        editRecipeIngredientsFragment = new EditRecipeIngredientsFragment();
                    }
                    //editRecipePhotoFragment = null;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.edit_recipe_container, editRecipeIngredientsFragment, EditRecipeIngredientsFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
                    getSupportFragmentManager().executePendingTransactions();
                } else if (f instanceof EditRecipeIngredientsFragment) {
                    //editRecipeStepsFragment = (EditRecipeStepsFragment) getSupportFragmentManager().findFragmentByTag(EditRecipeStepsFragment.class.getSimpleName());
                    setRecipe(editRecipeIngredientsFragment.saveData());
                    editRecipeIngredientsFragment = null;
                    if(editRecipeStepsFragment == null) {
                        editRecipeStepsFragment = new EditRecipeStepsFragment();
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.edit_recipe_container, editRecipeStepsFragment, EditRecipeStepsFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
                    getSupportFragmentManager().executePendingTransactions();
                } else if (f instanceof EditRecipeStepsFragment) {
                    setRecipe(editRecipeStepsFragment.saveData());
                    setResultData();
                }
                invalidateOptionsMenu();// creates call to onPrepareOptionsMenu()
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem icon = menu.findItem(R.id.menu_edit_recipe);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.edit_recipe_container);
        if(f instanceof EditRecipeStepsFragment) {
            icon.setTitle(getResources().getString(R.string.menu_save_text));
        }else {
            icon.setTitle(getResources().getString(R.string.next));
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_recipe_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        checkBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void finishWithoutSave(){
        if(!editRecipePhotoFragment.getNameOfNewImage().isEmpty()){
            ReadWriteTools rwTools = new ReadWriteTools();
            rwTools.deleteImage(getApplicationContext(), editRecipePhotoFragment.getNameOfNewImage());
        }
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    private void checkBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.exit_edit_title));
        builder.setMessage(getResources().getString(R.string.exit_edit)).setPositiveButton((getResources().getString(R.string.Yes)), dialogClickListener)
                .setNegativeButton((getResources().getString(R.string.No)), dialogClickListener);
        builder.show();
    }
    private void setResultData(){
        RecipeController recipeController = new RecipeController();
        RecipeDb recipeDb = RecipeDb.fromRecipeComplete(recipe);
        recipeDb.setUpdateRecipe(RecetasCookeoConstants.FLAG_UPLOAD_RECIPE);
        if(!recipe.getPicture().equals(RecetasCookeoConstants.DEFAULT_PICTURE_NAME)){
            recipeDb.setUpdatePicture(RecetasCookeoConstants.FLAG_UPLOAD_PICTURE);
        }
        recipeController.insertOrReplaceRecipe(getApplication(), recipeDb);
        FirebaseDbMethods firebaseDbMethods = new FirebaseDbMethods(recipeController);
        firebaseDbMethods.updateRecipesToPersonalStorage(getApplicationContext());
        finish();
    }

    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    performPressBack();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public RecipeComplete getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeComplete recipe) {
        this.recipe = recipe;
    }

    private void performPressBack(){
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.edit_recipe_container);
        if(f instanceof EditRecipePhotoFragment){
            finishWithoutSave();
        }else{
            invalidateOptionsMenu();

            if(f instanceof EditRecipeIngredientsFragment)
                editRecipeIngredientsFragment = null;
            else if(f instanceof EditRecipeStepsFragment)
                editRecipeStepsFragment = null;
            super.onBackPressed();
            f = getSupportFragmentManager().findFragmentById(R.id.edit_recipe_container);
            if(f instanceof EditRecipeIngredientsFragment)
                editRecipeIngredientsFragment = (EditRecipeIngredientsFragment) f;
            else if(f instanceof EditRecipeStepsFragment)
                editRecipeStepsFragment = (EditRecipeStepsFragment) f;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RecetasCookeoConstants.MY_PERMISSIONS_REQUEST_CAMERA: {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.edit_recipe_container);
                Boolean cameraAllowed = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (f instanceof EditRecipePhotoFragment) {
                    ((EditRecipePhotoFragment) f).selectPhoto(cameraAllowed);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(recipe != null){
            outState.putParcelable(RecetasCookeoConstants.KEY_RECIPE, recipe);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(RecetasCookeoConstants.KEY_RECIPE)){
            recipe = savedInstanceState.getParcelable(RecetasCookeoConstants.KEY_RECIPE);
        }
    }
}

