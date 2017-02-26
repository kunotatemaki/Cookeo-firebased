package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.rukiasoft.androidapps.cocinaconroll.BuildConfig;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.gcm.QuickstartPreferences;
import com.rukiasoft.androidapps.cocinaconroll.gcm.RegistrationIntentService;
import com.rukiasoft.androidapps.cocinaconroll.permissions.ErrorListener;
import com.rukiasoft.androidapps.cocinaconroll.permissions.RecetasCookeoMultiplePermissionListener;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.utilities.CommonRecipeOperations;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.State;

public class RecipeListActivity extends ToolbarAndProgressActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = LogHelper.makeLogTag(RecipeListActivity.class);


    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navview)
    NavigationView navigationView;
    @BindView(R.id.adview_list)
    AdView mAdViewList;
    @BindView(android.R.id.content)
    ViewGroup rootView;

    private Unbinder mUnbinder;

    private MenuItem mSearchMenuItem;
    //@State boolean mShowMenuSignOut = false;
    private int mMagnifyingX;
    private int mMagnifyingY;
    private int mOpenCircleRevealX;
    private int mOpenCircleRevealY;
    private boolean animate;
    @State String mLastFilter;
    @State boolean mAskForPermission = true;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Permissions
    private RecetasCookeoMultiplePermissionListener recetasCookeoMultiplePermissionListener;
    private ErrorListener errorListener;



    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mUnbinder = ButterKnife.bind(this);

        //Pido los permisos si procede
        if(mAskForPermission) {
            createPermissionListeners();
            Dexter.withActivity(this).continueRequestingPendingPermissions(recetasCookeoMultiplePermissionListener);
            askForAllPermissions();
            mAskForPermission = false;
        }

        // [END auth_state_listener]
        final Tools mTools = new Tools();
        if(mTools.getAppVersion(getApplication()) > mTools.getIntegerFromPreferences(this, RecetasCookeoConstants.PROPERTY_APP_VERSION_STORED)){
            //first installation, or recently updated app
            mTools.savePreferences(this, QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_APP_VERSION_STORED, mTools.getAppVersion(getApplication()));
            // TODO: 15/1/17 Esto posiblemente ya no haga falta
        }

        mLastFilter = RecetasCookeoConstants.FILTER_ALL_RECIPES;
        if(getIntent() != null && getIntent().hasExtra(RecetasCookeoConstants.KEY_TYPE)){
            mLastFilter = getIntent().getStringExtra(RecetasCookeoConstants.KEY_TYPE);
        }
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            if(!mTools.getBooleanFromPreferences(this, QuickstartPreferences.SENT_TOKEN_TO_SERVER)) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

        //Set default values for preferences
        if (mTools.hasVibrator(getApplicationContext())) {
            setDefaultValuesForOptions(R.xml.options);
        }else{
            setDefaultValuesForOptions(R.xml.options_not_vibrate);
        }

        setupDrawerLayout();

        //set up advertises
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice(BuildConfig.Z3_DEVICE_ID)  // My Xperia Z3 test device
                .setGender(AdRequest.GENDER_FEMALE)
                .build();

        mAdViewList.loadAd(adRequest);

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter();
        mStatusIntentFilter.addAction(RecetasCookeoConstants.ACTION_BROADCAST_UPLOADED_RECIPE);
        mStatusIntentFilter.addAction(RecetasCookeoConstants.ACTION_BROADCAST_DELETED_RECIPE);


        /*ReadWriteTools readWriteTools = new ReadWriteTools();
        readWriteTools.loadOldEditedAndOriginalRecipes(getApplicationContext());*/

        if(savedInstanceState == null) {
            clearGarbage();
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
        if(mRecipeListFragment != null){
            if(intent != null && intent.hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
                String name = intent.getStringExtra(RecetasCookeoConstants.KEY_RECIPE);
                mRecipeListFragment.searchAndShow(name);
            }
            if(intent != null && intent.hasExtra(RecetasCookeoConstants.KEY_TYPE)){
                mLastFilter = intent.getStringExtra(RecetasCookeoConstants.KEY_TYPE);
                restartLoader();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        Tools tools = new Tools();
        switch (requestCode) {
            case RecetasCookeoConstants.REQUEST_DETAILS:
                //return from RecipeDetailsActivity
                if (resultCode == RecetasCookeoConstants.RESULT_DELETE_RECIPE && intentData != null && intentData.hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
                    RecipeItemOld recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
                    if (recipe != null) {
                        removeRecipeFromDiskAndDatabase(recipe);
                    }
                }else if(resultCode == RecetasCookeoConstants.RESULT_UPDATE_RECIPE){
                    if (intentData != null && intentData.hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
                        RecipeItemOld recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
                        RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                        if (mRecipeListFragment != null) {
                            mRecipeListFragment.updateRecipe(recipe);
                        }
                    }
                }
                break;
            case RecetasCookeoConstants.REQUEST_EDIT_RECIPE:
                if(resultCode == RecetasCookeoConstants.RESULT_UPDATE_RECIPE && intentData != null && intentData.hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
                    RecipeItemOld recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
                    CommonRecipeOperations commonRecipeOperations = new CommonRecipeOperations(this, recipe);
                    String oldPicture = "";
                    if (intentData.hasExtra(RecetasCookeoConstants.KEY_DELETE_OLD_PICTURE)) {
                        oldPicture = intentData.getStringExtra(RecetasCookeoConstants.KEY_DELETE_OLD_PICTURE);
                    }
                    commonRecipeOperations.updateRecipe(oldPicture);
                    RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                    if (mRecipeListFragment != null) {
                        mRecipeListFragment.updateRecipe(recipe);
                    }
                }
                break;
            case RecetasCookeoConstants.REQUEST_CREATE_RECIPE:
                if (resultCode == RecetasCookeoConstants.RESULT_UPDATE_RECIPE && intentData != null && intentData.hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
                    RecipeItemOld recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
                    RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                    ReadWriteTools readWriteTools = new ReadWriteTools();
                    String path = readWriteTools.saveRecipeOnEditedPath(getApplicationContext(), recipe);
                    recipe.setPathRecipe(path);
                    if (mRecipeListFragment != null) {
                        mRecipeListFragment.createRecipe(recipe);
                    }
                }
                break;
            /*case REQUEST_CODE_RESOLUTION:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    connectToDrive(true);
                }
                break;*/
            case RecetasCookeoConstants.REQUEST_CODE_SIGNING_FROM_RECIPELIST:
                // TODO: 28/1/17 revisar qué hace aquí cuando le llamo desde esta actividad
                if(!tools.getBooleanFromPreferences(this, RecetasCookeoConstants.PROPERTY_INIT_DATABASE_WITH_EDITED_PATH)) {
                    // TODO: 26/2/17 atualizar recetas porque acabo de hacer sign in (o no)
                    //loadEditedRecipes();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intentData);
        }
    }

    private void removeRecipeFromDiskAndDatabase(RecipeItemOld recipe){
        //// TODO: 21/2/17 hacer con recipeControler
//        ReadWriteTools rwTools = new ReadWriteTools();
//        rwTools.deleteRecipe(recipe);
//        RecipeController recipeController = new RecipeController();
//        recipeController.removeRecipe(getApplicationContext(), recipe.get_id());
//        restartLoader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_list, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                final Toolbar toolbar = mRecipeListFragment.getToolbarRecipeListFragment();
                if (toolbar == null)
                    return true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    /*Window window = mActivity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(mActivity, R.color.ColorPrimaryDark));*/
                    if (animate) {
                        toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                v.removeOnLayoutChangeListener(this);
                                Animator animator = ViewAnimationUtils.createCircularReveal(
                                        toolbar,
                                        mOpenCircleRevealX,
                                        mOpenCircleRevealY,
                                        (float) Math.hypot(toolbar.getWidth(), toolbar.getHeight()),
                                        0);

                                // Set a natural ease-in/ease-out interpolator.
                                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                // make the view invisible when the animation is done
                                animator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        toolbar.setBackgroundResource(R.color.ColorPrimary);
                                    }
                                });

                                // make the view visible and start the animation
                                animator.start();
                            }
                        });
                    } else toolbar.setBackgroundResource(R.color.ColorPrimary);
                } else {
                    toolbar.setBackgroundResource(R.color.ColorPrimary);
                }

                //show the bar and button
                mRecipeListFragment.setVisibilityWithSearchWidget(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                final Toolbar toolbar = mRecipeListFragment.getToolbarRecipeListFragment();
                if (toolbar == null)
                    return true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animate = true;
                    /*Window window = mActivity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(mActivity, R.color.ColorPrimarySearchDark));*/
                    toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            v.removeOnLayoutChangeListener(this);
                            Animator animator = ViewAnimationUtils.createCircularReveal(
                                    toolbar,
                                    mMagnifyingX,
                                    mMagnifyingY,
                                    0,
                                    (float) Math.hypot(toolbar.getWidth(), toolbar.getHeight()));
                            mOpenCircleRevealX = mMagnifyingX;
                            mOpenCircleRevealY = mMagnifyingY;
                            // Set a natural ease-in/ease-out interpolator.
                            animator.setInterpolator(new AccelerateDecelerateInterpolator());

                            // make the view visible and start the animation
                            animator.start();
                        }
                    });
                }
                toolbar.setBackgroundResource(R.color.ColorPrimarySearch);
                //hide the bar and button
                mRecipeListFragment.setVisibilityWithSearchWidget(View.GONE);
                //hide the floating button

                return true;
            }

        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //the searchable is in another activity, so instead of getcomponentname(), create a new one for that activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent finalIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(finalIntent, RecetasCookeoConstants.REQUEST_CODE_SETTINGS);
                return true;
            case R.id.menu_thanks:
                finalIntent = new Intent(this, ThanksActivity.class);
                startActivity(finalIntent);
                return true;
            case R.id.menu_sign_in:
                launchSignInActivity();
                return true;
            case R.id.menu_sign_out:
                launchSignInActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        if(mProgressDialog.isShowing()){
            return;
        }
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            confirmExit();
        }
    }

    private void confirmExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int stringId = getApplicationInfo().labelRes;
        String name = getString(stringId);
        String question = String.format(getResources().getString(R.string.exit_application), name);
        builder.setTitle(getResources().getString(R.string.exit_application_title));
        builder.setMessage(question).setPositiveButton((getResources().getString(R.string.Yes)), dialogClickListener)
                .setNegativeButton((getResources().getString(R.string.No)), dialogClickListener);
        builder.show();
    }

    /**
     * Setup the drawer layout
     */
    private void setupDrawerLayout(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                        if(mRecipeListFragment != null) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu_all_recipes:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_ALL_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_ALL_RECIPES;
                                    break;
                                case R.id.menu_starters:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_STARTER_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_STARTER_RECIPES;
                                    break;
                                case R.id.menu_main_courses:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_MAIN_COURSES_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_MAIN_COURSES_RECIPES;
                                    break;
                                case R.id.menu_desserts:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_DESSERT_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_DESSERT_RECIPES;
                                    break;
                                case R.id.menu_vegetarians:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_VEGETARIAN_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_VEGETARIAN_RECIPES;
                                    break;
                                case R.id.menu_favorites:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_FAVOURITE_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_FAVOURITE_RECIPES;
                                    break;
                                case R.id.menu_own_recipes:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_OWN_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_OWN_RECIPES;
                                    break;
                                case R.id.menu_last_downloaded:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_LATEST_RECIPES);
                                    mLastFilter = RecetasCookeoConstants.FILTER_LATEST_RECIPES;
                                    break;
                            }
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void onResume(){
        super.onResume();

        if (mAdViewList != null) {
            mAdViewList.resume();
        }

        closeSearchView();
        //to start the reveal effecy from the magnifying glass
        final ViewTreeObserver viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                View menuButton = findViewById(R.id.action_search);
                // This could be called when the button is not there yet, so we must test for null
                if (menuButton != null) {
                    // Found it! Do what you need with the button
                    int[] location = new int[2];
                    menuButton.getLocationInWindow(location);
                    //Log.d(TAG, "x=" + location[0] + " y=" + location[1]);
                    mMagnifyingX = location[0] + menuButton.getWidth() / 2;
                    mMagnifyingY = location[1];
                    // Now you can get rid of this listener
                    if (mMagnifyingX != 0 && mMagnifyingY != 0 && viewTreeObserver.isAlive()) {
                        if (Build.VERSION.SDK_INT < 16) {
                            viewTreeObserver.removeGlobalOnLayoutListener(this);
                        } else {
                            viewTreeObserver.removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            }
        });
    }

    public void onPause(){
        super.onPause();
        if (mAdViewList != null) {
            mAdViewList.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null && mAuth != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void closeSearchView(){
        animate = false;
        if(mSearchMenuItem != null){
            MenuItemCompat.collapseActionView(mSearchMenuItem);
        }
    }

    public void performClickInDrawerIfNecessary() {
        if(mLastFilter.equals(RecetasCookeoConstants.FILTER_LATEST_RECIPES)){
            navigationView.setCheckedItem(R.id.menu_last_downloaded);
            RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
            if(mRecipeListFragment != null) {
                mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_LATEST_RECIPES);
            }
        } else {
            navigationView.setCheckedItem(R.id.menu_all_recipes);
        }
    }

    public void restartLoader(){
        RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
        if(mRecipeListFragment != null) {
            getSupportLoaderManager().restartLoader(RecetasCookeoConstants.LOADER_ID, null, mRecipeListFragment);
        }
    }

    private void clearGarbage(){
        //creo fichero nomedia
        ReadWriteTools rwTools = new ReadWriteTools();
        File file = new File(rwTools.getEditedStorageDir() + ".nomedia");
        try {
            if(!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //borro temporales de la cámara
        List<String> list = rwTools.loadFiles(getApplicationContext(), null, true);
        for(int i=0; i<list.size(); i++) {
            if(list.get(i).contains(RecetasCookeoConstants.TEMP_CAMERA_NAME)){
                file = new File(rwTools.getEditedStorageDir() + list.get(i));
                if(file.exists())
                    file.delete();
            }
        }


    }



    private void createPermissionListeners() {
        recetasCookeoMultiplePermissionListener = new RecetasCookeoMultiplePermissionListener(this);
        errorListener = new ErrorListener();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void askForAllPermissions(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(recetasCookeoMultiplePermissionListener)
                .withErrorListener(errorListener)
                .check();
    }

    @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
        Tools tools = new Tools();
        if(tools.getBooleanFromPreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_SIGNED_IN)){
            menu.findItem(R.id.menu_sign_out).setVisible(true);
            menu.findItem(R.id.menu_sign_in).setVisible(false);
        }else{
            menu.findItem(R.id.menu_sign_in).setVisible(true);
            menu.findItem(R.id.menu_sign_out).setVisible(false);
        }
        return true;
    }

    public void launchSignInActivity(){
        Intent intent = new Intent(RecipeListActivity.this, SignInActivity.class);
        startActivityForResult(intent, RecetasCookeoConstants.REQUEST_CODE_SIGNING_FROM_RECIPELIST);
    }




}
