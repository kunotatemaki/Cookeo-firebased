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
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rukiasoft.androidapps.cocinaconroll.BuildConfig;
import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.classes.ZipItem;
import com.rukiasoft.androidapps.cocinaconroll.database.DatabaseRelatedTools;
import com.rukiasoft.androidapps.cocinaconroll.gcm.QuickstartPreferences;
import com.rukiasoft.androidapps.cocinaconroll.gcm.RegistrationIntentService;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.RecipeTimestamp;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.RecipeShort;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.RecipeShortDao;
import com.rukiasoft.androidapps.cocinaconroll.utilities.CommonRecipeOperations;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.State;

public class RecipeListActivity extends FirebaseAuthBase implements RecipeListFragment.TaskCallback{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = LogHelper.makeLogTag(RecipeListActivity.class);
    private static final int REQUEST_CODE_SETTINGS = 20;
    private static final int REQUEST_CODE_SIGNING = 22;


    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navview)
    NavigationView navigationView;
    @BindView(R.id.adview_list)
    AdView mAdViewList;
    private Unbinder unbinder;

    private MenuItem searchMenuItem;
    @State boolean showMenuSignOut = false;
    private int magnifyingX;
    private int magnifyingY;
    private int openCircleRevealX;
    private int openCircleRevealY;
    private boolean animate;
    @State String lastFilter;
    @State Boolean checkRecipesTimestampFromFirebase = true;
    @State Boolean downloadPendingRecipesFromFirebase = false;

    //Firebase values
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRecipeTimestamps;
    private ValueEventListener timestampListener;


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
    public void onInitDatabasePostExecute() {
        restartLoader();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        unbinder = ButterKnife.bind(this);

        final Tools mTools = new Tools();

        //Para hacer debug de las querys de la base de datos
        if(BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    showMenuSignOut = true;
                } else {
                    // User is signed out
                    showMenuSignOut = false;
                    if(!mTools.getBooleanFromPreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_AVOID_GOOGLE_SIGN_IN)){
                        launchSignInActivity();
                    }
                }

            }
        };
        // [END auth_state_listener]

        if(mTools.getAppVersion(getApplication()) > mTools.getIntegerFromPreferences(this, RecetasCookeoConstants.PROPERTY_APP_VERSION_STORED)){
            //first instalation, or recently updated app
            mTools.savePreferences(this, QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_APP_VERSION_STORED, mTools.getAppVersion(getApplication()));
            // TODO: 15/1/17 Meter aquí la migración de las recetas propias de la antigua versión
        }

        lastFilter = RecetasCookeoConstants.FILTER_ALL_RECIPES;
        if(getIntent() != null && getIntent().hasExtra(RecetasCookeoConstants.KEY_TYPE)){
            lastFilter = getIntent().getStringExtra(RecetasCookeoConstants.KEY_TYPE);
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
                .addTestDevice("B29C1F71528C79C864D503360C5225C0")  // My Xperia Z3 test device
                .setGender(AdRequest.GENDER_FEMALE)
                .build();

        mAdViewList.loadAd(adRequest);

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter();
        mStatusIntentFilter.addAction(RecetasCookeoConstants.ACTION_BROADCAST_UPLOADED_RECIPE);
        mStatusIntentFilter.addAction(RecetasCookeoConstants.ACTION_BROADCAST_DELETED_RECIPE);

        //Compruebo si hay nuevas recetas o modificaciones en la base de datos (sólo en el arranque)
        if(checkRecipesTimestampFromFirebase){
            connectToFirebaseForNewRecipes();
        }


        if(savedInstanceState == null) {
            clearGarbage();
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbinder.unbind();
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
                lastFilter = intent.getStringExtra(RecetasCookeoConstants.KEY_TYPE);
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
                    RecipeItem recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
                    if (recipe != null) {
                        removeRecipeFromDiskAndDatabase(recipe);
                    }
                }else if(resultCode == RecetasCookeoConstants.RESULT_UPDATE_RECIPE){
                    if (intentData != null && intentData.hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
                        RecipeItem recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
                        RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                        if (mRecipeListFragment != null) {
                            mRecipeListFragment.updateRecipe(recipe);
                        }
                    }
                }
                break;
            case RecetasCookeoConstants.REQUEST_EDIT_RECIPE:
                if(resultCode == RecetasCookeoConstants.RESULT_UPDATE_RECIPE && intentData != null && intentData.hasExtra(RecetasCookeoConstants.KEY_RECIPE)) {
                    RecipeItem recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
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
                    RecipeItem recipe = intentData.getParcelableExtra(RecetasCookeoConstants.KEY_RECIPE);
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
            case REQUEST_CODE_SIGNING:
                if(!tools.getBooleanFromPreferences(this, RecetasCookeoConstants.PROPERTY_INIT_DATABASE_WITH_EDITED_PATH)) {
                    askForPermissionAndLoadEditedRecipes();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intentData);
        }
    }

    private void askForPermissionAndLoadEditedRecipes(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(this);

                builder.setMessage(getResources().getString(R.string.read_external_explanation))
                        .setTitle(getResources().getString(R.string.permissions_title))
                        .setPositiveButton(getResources().getString(R.string.accept),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        ActivityCompat.requestPermissions(RecipeListActivity.this,
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                RecetasCookeoConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                    }
                                });
                builder.create().show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        RecetasCookeoConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }else{
            RecipeListFragment fragment = (RecipeListFragment)getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
            if(fragment != null) {
                fragment.loadEditedRecipes();
            }
        }
    }

    private void removeRecipeFromDiskAndDatabase(RecipeItem recipe){
        ReadWriteTools rwTools = new ReadWriteTools();
        rwTools.deleteRecipe(recipe);
        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
        dbTools.removeRecipefromDatabase(getApplicationContext(), recipe.get_id());
        restartLoader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_list, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

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
                                        openCircleRevealX,
                                        openCircleRevealY,
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
                                    magnifyingX,
                                    magnifyingY,
                                    0,
                                    (float) Math.hypot(toolbar.getWidth(), toolbar.getHeight()));
                            openCircleRevealX = magnifyingX;
                            openCircleRevealY = magnifyingY;
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
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
                startActivityForResult(finalIntent, REQUEST_CODE_SETTINGS);
                return true;
            case R.id.menu_thanks:
                finalIntent = new Intent(this, ThanksActivity.class);
                startActivity(finalIntent);
                return true;
            case R.id.menu_sign_in:
                launchSignInActivity();
                return true;
            case R.id.menu_sign_out:
                revokeAccess();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
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
                                    lastFilter = RecetasCookeoConstants.FILTER_ALL_RECIPES;
                                    break;
                                case R.id.menu_starters:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_STARTER_RECIPES);
                                    lastFilter = RecetasCookeoConstants.FILTER_STARTER_RECIPES;
                                    break;
                                case R.id.menu_main_courses:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_MAIN_COURSES_RECIPES);
                                    lastFilter = RecetasCookeoConstants.FILTER_MAIN_COURSES_RECIPES;
                                    break;
                                case R.id.menu_desserts:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_DESSERT_RECIPES);
                                    lastFilter = RecetasCookeoConstants.FILTER_DESSERT_RECIPES;
                                    break;
                                case R.id.menu_vegetarians:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_VEGETARIAN_RECIPES);
                                    lastFilter = RecetasCookeoConstants.FILTER_VEGETARIAN_RECIPES;
                                    break;
                                case R.id.menu_favorites:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_FAVOURITE_RECIPES);
                                    lastFilter = RecetasCookeoConstants.FILTER_FAVOURITE_RECIPES;
                                    break;
                                case R.id.menu_own_recipes:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_OWN_RECIPES);
                                    lastFilter = RecetasCookeoConstants.FILTER_OWN_RECIPES;
                                    break;
                                case R.id.menu_last_downloaded:
                                    mRecipeListFragment.filterRecipes(RecetasCookeoConstants.FILTER_LATEST_RECIPES);
                                    lastFilter = RecetasCookeoConstants.FILTER_LATEST_RECIPES;
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
                    magnifyingX = location[0] + menuButton.getWidth() / 2;
                    magnifyingY = location[1];
                    // Now you can get rid of this listener
                    if (magnifyingX != 0 && magnifyingY != 0 && viewTreeObserver.isAlive()) {
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


    public void closeSearchView(){
        animate = false;
        if(searchMenuItem != null){
            MenuItemCompat.collapseActionView(searchMenuItem);
        }
    }

    public void performClickInDrawerIfNecessary() {
        if(lastFilter.equals(RecetasCookeoConstants.FILTER_LATEST_RECIPES)){
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

        //veo si hay algún zip en la base de datos que no tenga el formato correcto
        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
        List<ZipItem> listZips = dbTools.getAllZips(getApplicationContext());
        for(ZipItem zip : listZips){
            if(!zip.getName().contains(".zip")){
                dbTools.removeZipfromDatabase(getApplicationContext(), zip.getId());
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
                    RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().
                            findFragmentById(R.id.list_recipes_fragment);
                    if(mRecipeListFragment != null) {
                        mRecipeListFragment.createRecipe();
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
            case RecetasCookeoConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RecipeListFragment mRecipeListFragment = (RecipeListFragment) getSupportFragmentManager().
                            findFragmentById(R.id.list_recipes_fragment);
                    if(mRecipeListFragment != null) {
                        mRecipeListFragment.loadEditedRecipes();
                    }
                } else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(this);

                    builder.setMessage(getResources().getString(R.string.read_external_denied))
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if(timestampListener != null){
            mRecipeTimestamps.removeEventListener(timestampListener);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(showMenuSignOut){
            menu.findItem(R.id.menu_sign_out).setVisible(true);
            menu.findItem(R.id.menu_sign_in).setVisible(false);
        }else{
            menu.findItem(R.id.menu_sign_in).setVisible(true);
            menu.findItem(R.id.menu_sign_out).setVisible(false);
        }
        return true;
    }

    private void launchSignInActivity(){
        Intent intent = new Intent(RecipeListActivity.this, SignInActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SIGNING);
    }

    private void connectToFirebaseForNewRecipes(){
        // TODO: 19/1/17 hacer lo mismo para las prohibidas, para que lo tengamos marieta y yo
        mRecipeTimestamps = FirebaseDatabase.getInstance().getReference(RecetasCookeoConstants.ALLOWED_RECIPES_NODE +
            "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE);
        timestampListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecipeShortDao recipeShortDao = ((CocinaConRollApplication)getApplication()).getDaoSession().getRecipeShortDao();
                recipeShortDao.detachAll();
                String key = "";
                Query query = recipeShortDao.queryBuilder().where(
                        RecipeShortDao.Properties.Key.eq(key)
                ).build();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RecipeTimestamp recipeTimestamp = postSnapshot.getValue(RecipeTimestamp.class);
                    key = postSnapshot.getKey();
                    query.setParameter(0, key);
                    RecipeShort recipeFromDatabase = (RecipeShort) query.unique();
                    if(recipeFromDatabase == null){
                        //no existía, la creo
                        recipeFromDatabase = new RecipeShort();
                        //Log.d(TAG, "no existe, la creo");
                    }else if(recipeFromDatabase.getTimestamp() >= recipeTimestamp.getTimestamp()){
                        //Log.d(TAG, "ACTUALIZADA: " + recipeFromDatabase.getName());
                        continue;
                    }
                    recipeFromDatabase.setKey(key);
                    recipeFromDatabase.setTimestamp(recipeTimestamp.getTimestamp());
                    recipeFromDatabase.setDownloadRecipe(true);
                    recipeShortDao.insertOrReplace(recipeFromDatabase);

                    /*if(recipeFromDatabase == null){
                        //no existe en la base de datos -> la creo.
                        recipeFromDatabase = new RecipeShort();
                        recipeFromDatabase.setKey(key);
                        recipeFromDatabase.setTimestamp(recipeTimestamp.getTimestamp());
                        recipeFromDatabase.setDownloadRecipe(true);
                        recipeShortDao.insert(recipeFromDatabase);
                    }else{
                        //ya existe
                        if(recipeTimestamp.getTimestamp() > recipeFromDatabase.getTimestamp()){
                            //existe -> la actualizo
                            recipeFromDatabase.setTimestamp(recipeTimestamp.getTimestamp());
                            recipeFromDatabase.setDownloadRecipe(true);
                            recipeFromDatabase.update();
                        }
                    }*/
                }
                checkRecipesTimestampFromFirebase = false;
                //Si el fragment existe, llamo a descargar (haya recetas nuevas o no).
                //Si no, lo marco como pendiente
                RecipeListFragment fragment = (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.list_recipes_fragment);
                if(fragment != null){
                    Log.d(TAG, "muestro directamente el loader");
                    fragment.downloadRecipesFromFirebase();
                }else{
                    Log.d(TAG, "solicito que muestre en onResume");
                    downloadPendingRecipesFromFirebase = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRecipeTimestamps.addListenerForSingleValueEvent(timestampListener);
    }


}
