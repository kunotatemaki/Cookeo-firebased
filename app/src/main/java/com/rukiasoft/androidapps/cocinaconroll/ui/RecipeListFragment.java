package com.rukiasoft.androidapps.cocinaconroll.ui;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rukiasoft.androidapps.cocinaconroll.BuildConfig;
import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.database.CocinaConRollContentProvider;
import com.rukiasoft.androidapps.cocinaconroll.database.DatabaseRelatedTools;
import com.rukiasoft.androidapps.cocinaconroll.database.RecipesTable;
import com.rukiasoft.androidapps.cocinaconroll.fastscroller.FastScroller;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.RecipeDetailed;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.RecipeTimestamp;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.Ingredient;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.IngredientDao;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.RecipeShort;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.RecipeShortDao;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.Step;
import com.rukiasoft.androidapps.cocinaconroll.persistence.greendao.StepDao;
import com.rukiasoft.androidapps.cocinaconroll.utilities.CommonRecipeOperations;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.State;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, RecipeListRecyclerViewAdapter.OnCardClickListener,
        AppBarLayout.OnOffsetChangedListener{

    private static final String TAG = LogHelper.makeLogTag(RecipeListFragment.class);
    private static final String KEY_SCROLL_POSITION = RecetasCookeoConstants.PACKAGE_NAME + ".scrollposition";
    private static final String KEY_RECIPE_LIST = RecetasCookeoConstants.PACKAGE_NAME + ".recipelist";
    private static final int LOAD_ORIGINAL_PATH = 0;
    private static final int LOAD_EDITED_PATH = 1;
    private static final long MAX_MSECONDS_REFRESING = 30000;


    @Nullable
    @BindView(R.id.toolbar_recipe_list_fragment) Toolbar mToolbarRecipeListFragment;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @Nullable @BindView((R.id.fastscroller))
    FastScroller fastScroller;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.recipe_list_number_recipes)
    TextView nRecipesInRecipeList;
    @BindView(R.id.recipe_list_type_recipes)
    TextView typeRecipesInRecipeList;
    @BindView(R.id.recipe_list_type_icon)
    ImageView typeIconInRecipeList;
    @BindView(R.id.numberandtype_recipes_bar)
    RelativeLayout numberAndTypeBar;
    @BindView(R.id.add_recipe_fab)
    FloatingActionButton addRecipeButtonFAB;
    @BindView(R.id.init_database_text) TextView initDatabaseText;
    private Unbinder unbinder;
    private CountDownTimer timeoutRefreshing;
    @State Boolean counting = false;    //Para controlar si está contando o no
    private Query queryRecipesToDownload;

    //Firebase values
    private DatabaseReference mRecipeTimestamps;
    private ValueEventListener timestampListener;
    @State Boolean checkRecipesTimestampFromFirebase = true;
    //Firebase storage
    FirebaseStorage storage;

    interface TaskCallback {
        void onInitDatabasePostExecute();
    }

    private TaskCallback mInitDatabaseCallback;
    //private SlideInBottomAnimationAdapter slideAdapter;
    //private RecipeListRecyclerViewAdapter adapter;
    private List<RecipeItem> mRecipes;
    private int savedScrollPosition = 0;
    private int columnCount = 10;
    private String lastFilter;
    private InterstitialAd mInterstitialAd;
    private RecipeItem recipeToShow;
    private boolean readExternalPermisionDialogShown;

    private class InitDatabase extends AsyncTask<Void, Integer, Void> {
        final Context mContext;
        final int mode;

        public InitDatabase(Context context, int mode){
            this.mContext = context;
            this.mode = mode;
        }

        protected Void doInBackground(Void... data) {
            ReadWriteTools rwTools = new ReadWriteTools();
            switch (mode) {
                case LOAD_ORIGINAL_PATH:
                    rwTools.initDatabaseWithOriginalPath(getActivity().getApplicationContext());
                    break;
                case LOAD_EDITED_PATH:
                    rwTools.initDatabaseWithEditedPath(getActivity().getApplicationContext());
                    break;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Void result) {
            Tools mTools = new Tools();
            switch (mode) {
                case LOAD_ORIGINAL_PATH:
                    mTools.savePreferences(mContext, RecetasCookeoConstants.PROPERTY_INIT_DATABASE_WITH_ORIGINAL_PATH, true);
                    break;
                case LOAD_EDITED_PATH:
                    mTools.savePreferences(mContext, RecetasCookeoConstants.PROPERTY_INIT_DATABASE_WITH_EDITED_PATH, true);
                    break;
            }
            mInitDatabaseCallback.onInitDatabasePostExecute();
        }
    }

    public RecipeListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id_intersticial));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                if(recipeToShow != null) {
                    launchActivityDetails();
                    recipeToShow = null;
                }
            }
        });

        requestNewInterstitial();

        //Inicio el temporizador si es necesario
        if(timeoutRefreshing == null){
            timeoutRefreshing = new CountDownTimer(MAX_MSECONDS_REFRESING, 5000) {

                public void onTick(long millisUntilFinished) {
                    counting = true;
                }

                public void onFinish() {
                    counting = false;
                    ((RecipeListActivity)getActivity()).hideProgressDialog();
                }
            };
        }

        //inicio la queryRecipesToDownload si es necesario
        if(queryRecipesToDownload == null){
            initializeQueryRecipesToDownload();
        }

        //inicio el storage si es necesario
        if(storage == null){
            storage = FirebaseStorage.getInstance();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(BuildConfig.Z3_DEVICE_ID)  // My Xperia Z3 test device
                .setGender(AdRequest.GENDER_FEMALE)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        //Set the mToolbarRecipeListFragment
        if(getActivity() instanceof ToolbarAndProgressActivity){
            ((ToolbarAndProgressActivity) getActivity()).setToolbar(mToolbarRecipeListFragment);
        }

        savedScrollPosition = 0;
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_SCROLL_POSITION)){
                savedScrollPosition = savedInstanceState.getInt(KEY_SCROLL_POSITION);
            }
            if(savedInstanceState.containsKey(KEY_RECIPE_LIST)){
                mRecipes = savedInstanceState.getParcelableArrayList(KEY_RECIPE_LIST);
            }
        }


        if(mAppBarLayout != null){
            mAppBarLayout.addOnOffsetChangedListener(this);
        }

        typeRecipesInRecipeList.setText(getResources().getString(R.string.all_recipes));
        typeIconInRecipeList.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_all_24));
        lastFilter = RecetasCookeoConstants.FILTER_ALL_RECIPES;

        if(addRecipeButtonFAB != null) {
            addRecipeButtonFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(getActivity());

                            builder.setMessage(getResources().getString(R.string.write_external_explanation))
                                    .setTitle(getResources().getString(R.string.permissions_title))
                                    .setPositiveButton(getResources().getString(R.string.accept),
                                            new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            ActivityCompat.requestPermissions(getActivity(),
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    RecetasCookeoConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                        }
                                    });
                            builder.create().show();
                        } else {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    RecetasCookeoConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }else{
                        createRecipe();
                    }
                }
            });
        }

        //Compruebo si hay nuevas recetas o modificaciones en la base de datos (sólo en el arranque)
        if(checkRecipesTimestampFromFirebase){
            connectToFirebaseForNewRecipes(RecetasCookeoConstants.ALLOWED_RECIPES_NODE);
            connectToFirebaseForNewRecipes(RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE);
            checkRecipesTimestampFromFirebase = false;
        }

        return view;
    }

    public void createRecipe(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
                getActivity().startActivityForResult(intent, RecetasCookeoConstants.REQUEST_CREATE_RECIPE);
            }
        }, 150);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addRecipeButtonFAB.setOnClickListener(null);
        if(timestampListener != null){
            mRecipeTimestamps.removeEventListener(timestampListener);
        }
        unbinder.unbind();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(getActivity() instanceof ToolbarAndProgressActivity){
            ToolbarAndProgressActivity activity = (ToolbarAndProgressActivity) getActivity();
            if(activity.needToShowRefresh){
                activity.showProgressDialog();
            }else{
                activity.hideProgressDialog();
            }
        }

    }

    public void loadEditedRecipes(){
        InitDatabase initDatabase = new InitDatabase(getActivity().getApplicationContext(), LOAD_EDITED_PATH);
        initDatabase.execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        if(mRecipes == null || mRecipes.size() == 0) {
            getLoaderManager().initLoader(RecetasCookeoConstants.LOADER_ID, null, this);
        }else{
            setData();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        try {
            if (mRecyclerView != null) {
                int[] scrollPosition = new int[columnCount];
                if (mRecyclerView.getLayoutManager() != null) {
                    scrollPosition = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPositions(scrollPosition);
                    savedInstanceState.putSerializable(KEY_SCROLL_POSITION, scrollPosition[0]);
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        if(mRecipes != null) {
            savedInstanceState.putParcelableArrayList(KEY_RECIPE_LIST, (ArrayList<RecipeItem>) mRecipes);
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(getActivity() instanceof ToolbarAndProgressActivity){
            if(isResumed()){
                Tools tools = new Tools();
                //tools.showRefreshLayout(getActivity());
            }else {
                //((ToolbarAndProgressActivity) getActivity()).needToShowRefresh = true;
            }
        }
        //return new RecipeListLoader(getActivity().getApplicationContext());
        Uri CONTENT_URI = CocinaConRollContentProvider.CONTENT_URI_RECIPES;
        String sortOrder = RecipesTable.FIELD_NAME_NORMALIZED + " asc ";
        return new CursorLoader(getActivity(), CONTENT_URI, null, null, null, sortOrder);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("", "+++ onLoadFinished() called! +++");
        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
        mRecipes = dbTools.getRecipesFromCursor(data);
        Tools mTools = new Tools();
        if(mRecipes.size() == 0 || !mTools.getBooleanFromPreferences(getActivity(), RecetasCookeoConstants.PROPERTY_INIT_DATABASE_WITH_ORIGINAL_PATH)){
            initDatabaseText.setVisibility(View.VISIBLE);
            InitDatabase initDatabase = new InitDatabase(getActivity().getApplicationContext(), LOAD_ORIGINAL_PATH);
            initDatabase.execute();
            return;
        }
        if(mTools.getBooleanFromPreferences(getActivity(), RecetasCookeoConstants.PROPERTY_RELOAD_NEW_ORIGINALS)){
            ReadWriteTools rwTools = new ReadWriteTools();
            rwTools.loadNewFilesAndInsertInDatabase(getActivity().getApplicationContext());
            mTools.savePreferences(getActivity(), RecetasCookeoConstants.PROPERTY_RELOAD_NEW_ORIGINALS, false);
            ((RecipeListActivity)getActivity()).restartLoader();
            return;
        }

        setData();
        ((RecipeListActivity)getActivity()).performClickInDrawerIfNecessary();

        if(!mTools.getBooleanFromPreferences(getActivity(), RecetasCookeoConstants.PROPERTY_UPLOADED_RECIPES_ON_FIRST_BOOT)){
            for(RecipeItem recipe : mRecipes){
                if((recipe.getState() & (RecetasCookeoConstants.FLAG_EDITED | RecetasCookeoConstants.FLAG_OWN)) != 0){
                    dbTools.updateStateById(getActivity().getApplicationContext(),
                            recipe.get_id(), recipe.getState());
                    recipe.setVersion(recipe.getVersion() + 1);
                    dbTools.updatePathsAndVersion(getActivity().getApplicationContext(), recipe);

                }
            }
            mTools.savePreferences(getActivity(), RecetasCookeoConstants.PROPERTY_UPLOADED_RECIPES_ON_FIRST_BOOT, true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            Tools tools = new Tools();
            //tools.hideRefreshLayout(getActivity());
        }
    }

    private void setData(){
        initDatabaseText.setVisibility(View.GONE);
        //orderRecipesByName();
        //((ToolbarAndProgressActivity) getActivity()).needToShowRefresh = false;
        if(isResumed()) {
            Tools tools = new Tools();
            //tools.hideRefreshLayout(getActivity());
        }

        RecipeListRecyclerViewAdapter adapter = new RecipeListRecyclerViewAdapter(getActivity(), mRecipes);
        adapter.setHasStableIds(true);
        adapter.setOnCardClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SlideInBottomAnimationAdapter slideAdapter = wrapAdapter(adapter);
            mRecyclerView.setAdapter(slideAdapter);
        }else{
            mRecyclerView.setAdapter(adapter);
        }
        //mRecyclerView.setAdapter(adapter);
        columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);


        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.scrollToPosition(savedScrollPosition);
        //Set the fast Scroller
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if(fastScroller != null) {
                fastScroller.setRecyclerView(mRecyclerView);
            }
        }

        //set the number of recipes
        String nRecipes = String.format(getResources().getString(R.string.recipes), mRecipes.size());
        nRecipesInRecipeList.setText(nRecipes);
    }

    private SlideInBottomAnimationAdapter wrapAdapter(RecipeListRecyclerViewAdapter adapter){
        SlideInBottomAnimationAdapter slideAdapter = new SlideInBottomAnimationAdapter(adapter);
        slideAdapter.setInterpolator(new OvershootInterpolator(2.0f));
        slideAdapter.setDuration(2000);
        return slideAdapter;
    }

    @Override
    public void onCardClick(View view, RecipeItem recipeItem) {
        showRecipeDetails(recipeItem);
    }



    private void showRecipeDetails(RecipeItem recipeItem){
        //interstitial
        Tools tools = new Tools();
        int number = tools.getIntegerFromPreferences(getActivity().getApplicationContext(), RecetasCookeoConstants.PREFERENCE_INTERSTITIAL);
        if(number<0 || number> RecetasCookeoConstants.N_RECIPES_TO_INTERSTICIAL){
            number = 0;
        }
        CommonRecipeOperations commonRecipeOperations = new CommonRecipeOperations(getActivity(), recipeItem);
        recipeItem = commonRecipeOperations.loadRecipeDetailsFromRecipeCard();

        recipeToShow = recipeItem;
        if(number != RecetasCookeoConstants.N_RECIPES_TO_INTERSTICIAL) {
            launchActivityDetails();
        }else if(mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            number = 0;
        }else{
            launchActivityDetails();
            requestNewInterstitial();
            return;
        }
        tools.savePreferences(getActivity(), RecetasCookeoConstants.PREFERENCE_INTERSTITIAL, ++number);

    }

    private void launchActivityDetails(){
        Intent intent = new Intent(getActivity(), RecipeDetailActivityBase.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RecetasCookeoConstants.KEY_RECIPE, recipeToShow);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivityForResult(getActivity(), intent, RecetasCookeoConstants.REQUEST_DETAILS, activityOptions.toBundle());

        recipeToShow = null;

    }


    public void filterRecipes(String filter) {
        lastFilter = filter;
        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
        String type = "";
        int iconResource = 0;
        if(filter.compareTo(RecetasCookeoConstants.FILTER_ALL_RECIPES) == 0) {
            type = getResources().getString(R.string.all_recipes);
            mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext());
            iconResource = R.drawable.ic_all_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_MAIN_COURSES_RECIPES) == 0){
            type = getResources().getString(R.string.main_courses);
            mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(), RecipesTable.FIELD_TYPE, RecetasCookeoConstants.TYPE_MAIN);
            iconResource = R.drawable.ic_main_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_STARTER_RECIPES) == 0){
            type = getResources().getString(R.string.starters);
            mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
                    RecipesTable.FIELD_TYPE, RecetasCookeoConstants.TYPE_STARTERS);
            iconResource = R.drawable.ic_starters_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_DESSERT_RECIPES) == 0){
            type = getResources().getString(R.string.desserts);
            mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
                    RecipesTable.FIELD_TYPE, RecetasCookeoConstants.TYPE_DESSERTS);
            iconResource = R.drawable.ic_dessert_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_VEGETARIAN_RECIPES) == 0){
            type = getResources().getString(R.string.vegetarians);
            mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
                    RecipesTable.FIELD_VEGETARIAN, 1);
            iconResource = R.drawable.ic_vegetarians_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_FAVOURITE_RECIPES) == 0){
            type = getResources().getString(R.string.favourites);
            mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
                    RecipesTable.FIELD_FAVORITE, 1);
            iconResource = R.drawable.ic_favorite_black_24dp;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_OWN_RECIPES) == 0){
            type = getResources().getString(R.string.own_recipes);
            mRecipes = dbTools.searchRecipesInDatabaseByState(getActivity().getApplicationContext(),
                    RecetasCookeoConstants.FLAG_EDITED | RecetasCookeoConstants.FLAG_OWN);
            iconResource = R.drawable.ic_own_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_LATEST_RECIPES) == 0){
            type = getResources().getString(R.string.last_downloaded);
            Tools mTools = new Tools();
            mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
                    RecipesTable.FIELD_DATE, mTools.getTimeframe());
            iconResource = R.drawable.ic_latest_24;
        }
        typeRecipesInRecipeList.setText(type);
        String nrecipes = String.format(getResources().getString(R.string.recipes), mRecipes.size());
        nRecipesInRecipeList.setText(nrecipes);
        typeIconInRecipeList.setImageDrawable(ContextCompat.getDrawable(getActivity(), iconResource));
        //Change the adapter
        RecipeListRecyclerViewAdapter newAdapter = new RecipeListRecyclerViewAdapter(getActivity(), mRecipes);
        newAdapter.setHasStableIds(true);
        newAdapter.setOnCardClickListener(this);
        mRecyclerView.setHasFixedSize(true);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SlideInBottomAnimationAdapter newSlideAdapter = wrapAdapter(newAdapter);
            mRecyclerView.swapAdapter(newSlideAdapter, false);
        }else{
            mRecyclerView.swapAdapter(newAdapter, false);
        }
        //mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        /*adapter = newAdapter;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            slideAdapter = newSlideAdapter;
        }*/
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.scrollToPosition(0);

        //Set the fast Scroller
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && fastScroller != null) {
            fastScroller.setRecyclerView(mRecyclerView);
        }
    }


    @Nullable
    public Toolbar getToolbarRecipeListFragment() {
        return mToolbarRecipeListFragment;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        if(percentage > 0.5f){
            if(getActivity() instanceof RecipeListActivity){
                ((RecipeListActivity) getActivity()).closeSearchView();
            }
        }
    }

    public void setVisibilityWithSearchWidget(int visibility){
        numberAndTypeBar.setVisibility(visibility);
        if(visibility == View.GONE) addRecipeButtonFAB.hide();
        //else addRecipeButton.show();
    }

    public void updateRecipe(RecipeItem recipe) {
        if(mRecipes == null){
            return;
        }
        for(int i=0; i<mRecipes.size(); i++){
            if(mRecipes.get(i).get_id().intValue() == recipe.get_id().intValue()){
                mRecipes.remove(i);
                mRecipes.add(i, recipe);
                filterRecipes(lastFilter);
            }
        }
    }

    public void createRecipe(RecipeItem recipe) {
        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
        dbTools.addRecipeToArrayAndDatabase(getActivity().getApplicationContext(), mRecipes, recipe);
        filterRecipes(lastFilter);
    }

    public void searchAndShow(String name) {
        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
        name = dbTools.getNormalizedString(name);
        List<RecipeItem> coincidences = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
                RecipesTable.FIELD_NAME_NORMALIZED, dbTools.getNormalizedString(name));
        if (coincidences.size() > 0) {
            showRecipeDetails(coincidences.get(0));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mInitDatabaseCallback = (TaskCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInitDatabaseCallback = null;
    }

    private void initializeQueryRecipesToDownload(){
        RecipeShortDao recipeShortDao = ((CocinaConRollApplication)getActivity().getApplication()).getDaoSession().getRecipeShortDao();
        recipeShortDao.detachAll();
        QueryBuilder qb = recipeShortDao.queryBuilder();
        queryRecipesToDownload = qb.where(
                qb.or(RecipeShortDao.Properties.DownloadRecipe.eq(1),
                RecipeShortDao.Properties.DownloadPicture.eq(1))
        ).build();
    }

    public void downloadRecipesFromFirebase(){
        //Veo si hay que descargar recetas
        if(queryRecipesToDownload == null){
            initializeQueryRecipesToDownload();
        }
        List<RecipeShort> recipes = queryRecipesToDownload.list();
        if(recipes == null || recipes.isEmpty()){
            return;
        }

        //inicio el timer
        if(!counting){
            counting = true;
            timeoutRefreshing.start();
        }
        //Pongo el loading
        if(this.isResumed()) {
            ((RecipeListActivity) getActivity()).showProgressDialog(getString(R.string.downloading_recipes));
        }else{
            ((RecipeListActivity) getActivity()).needToShowRefresh = true;
        }
        int i=1;
        //Descargo las recetas de Firebase
        for(RecipeShort recipe : recipes){
            if(i==0) break;
            if(recipe.getDownloadRecipe()) {
                //descargo la receta y luego si procede, la foto
                DatabaseReference mRecipeRefDetailed = FirebaseDatabase.getInstance().getReference(RecetasCookeoConstants.ALLOWED_RECIPES_NODE +
                        "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + recipe.getKey());
                mRecipeRefDetailed.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DowloadRecipeTask downloadTask = new DowloadRecipeTask();
                        downloadTask.execute(dataSnapshot);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else if(recipe.getDownloadPicture()){
                //sólo descargo la foto
                downloadPictureFromDatabase(recipe.getPicture());
            }
            i++;
        }
    }

    private void saveIngredientsToDatabase(List<String> ingredients, String key){
        //Grabo los ingredientes (primero borro los que había)
        IngredientDao ingredientDao = ((CocinaConRollApplication)getActivity().getApplication())
                .getDaoSession().getIngredientDao();
        Query query = ingredientDao.queryBuilder().where(
                IngredientDao.Properties.Key.eq(""),
                IngredientDao.Properties.Position.eq(0)
        ).build();

        DeleteQuery<Ingredient> delete = ingredientDao.queryBuilder().where(
                IngredientDao.Properties.Key.eq(key)
        ).buildDelete();
        delete.executeDeleteWithoutDetachingEntities();

        for(int i=0; i<ingredients.size(); i++){
            query.setParameter(0, key);
            query.setParameter(1, i);
            Ingredient ingredient = (Ingredient) query.unique();
            if(ingredient == null){
                ingredient = new Ingredient();
            }
            ingredient.setKey(key);
            ingredient.setPosition(i);
            ingredient.setIngredient(ingredients.get(i));
            ingredientDao.insertOrReplace(ingredient);

        }
    }

    private void saveStepsToDatabase(List<String> steps, String key){
        //Grabo los pasos
        StepDao stepDao = ((CocinaConRollApplication)getActivity().getApplication())
                .getDaoSession().getStepDao();
        Query query = stepDao.queryBuilder().where(
                StepDao.Properties.Key.eq(""),
                StepDao.Properties.Position.eq(0)
        ).build();

        DeleteQuery<Step> delete = stepDao.queryBuilder().where(
                StepDao.Properties.Key.eq(key)
        ).buildDelete();
        delete.executeDeleteWithoutDetachingEntities();

        for(int i=0; i<steps.size(); i++){
            query.setParameter(0, key);
            query.setParameter(1, i);
            Step step = (Step) query.unique();
            if(step == null){
                step = new Step();
            }
            step.setKey(key);
            step.setPosition(i);
            step.setStep(steps.get(i));
            stepDao.insertOrReplace(step);
        }
    }

    private void downloadPictureFromDatabase(final String name){
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("recipes/" + name);
        final File imageFile;
        ReadWriteTools rwTools = new ReadWriteTools();
        String path = rwTools.getOriginalStorageDir(getContext());
        imageFile = new File(path + name);

        imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d(TAG, "Salvado correctamente: " + name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if(imageFile != null && imageFile.exists()){
                    imageFile.delete();
                }
            }
        });

    }

    private void connectToFirebaseForNewRecipes(String node){
        // TODO: 19/1/17 hacer lo mismo para las prohibidas, para que lo tengamos marieta y yo
        mRecipeTimestamps = FirebaseDatabase.getInstance().getReference(node +
                "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE);
        timestampListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DownloadTimestampsTask downloadTimestampsTask = new DownloadTimestampsTask();
                downloadTimestampsTask.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRecipeTimestamps.addListenerForSingleValueEvent(timestampListener);
    }

    //ASYNCTASKS
    private class DowloadRecipeTask extends AsyncTask<DataSnapshot, Integer, RecipeShort> {

        @Override
        protected RecipeShort doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot dataSnapshot = dataSnapshots[0];
            RecipeDetailed recipe = dataSnapshot.getValue(RecipeDetailed.class);
            RecipeShort recipeShort = new RecipeShort(recipe, dataSnapshot.getKey());
            RecipeShortDao recipeShortDao = ((CocinaConRollApplication)getActivity().getApplication())
                    .getDaoSession().getRecipeShortDao();
            recipeShortDao.detachAll();
            recipeShortDao.insertOrReplace(recipeShort);
            //grabo los ingredientes
            saveIngredientsToDatabase(recipe.getIngredients(), dataSnapshot.getKey());
            //grabo los pasos
            saveStepsToDatabase(recipe.getSteps(), dataSnapshot.getKey());
            return recipeShort;
        }

        @Override
        protected void onPostExecute(RecipeShort recipeShort) {
            if(recipeShort.getDownloadPicture()){
                downloadPictureFromDatabase(recipeShort.getPicture());
            }
        }
    }

    private class DownloadTimestampsTask extends AsyncTask<DataSnapshot, Integer, Void>{

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot dataSnapshot = dataSnapshots[0];
            RecipeShortDao recipeShortDao = ((CocinaConRollApplication)getActivity()
                    .getApplication()).getDaoSession().getRecipeShortDao();
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
                if(recipeFromDatabase.getTimestamp() == null ||
                        recipeFromDatabase.getTimestamp() < recipeTimestamp.getTimestamp()) {
                    recipeFromDatabase.setKey(key);
                    recipeFromDatabase.setTimestamp(System.currentTimeMillis());
                    recipeFromDatabase.setDownloadRecipe(true);
                    recipeShortDao.insertOrReplace(recipeFromDatabase);
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Llamo a descargar (haya recetas nuevas o no).
            downloadRecipesFromFirebase();
        }
    }
}



