package com.rukiasoft.androidapps.cocinaconroll.ui;


import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rukiasoft.androidapps.cocinaconroll.BuildConfig;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.database.CocinaConRollContentProvider;
import com.rukiasoft.androidapps.cocinaconroll.database.RecipesTable;
import com.rukiasoft.androidapps.cocinaconroll.fastscroller.FastScroller;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.TimestampFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.local.ObjectQeue;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeReduced;
import com.rukiasoft.androidapps.cocinaconroll.utilities.CommonRecipeOperations;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.io.File;
import java.nio.channels.NonWritableChannelException;
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
    private static final long MAX_MILI_SECONDS_DOWNLOADING_RECIPES = 240000;
    private static final long MAX_MILI_SECONDS_DOWNLOADING_PICTURES = 300000;
    private static final long MAX_MILI_SECONDS_SHOWING_REFRESH_LAYOUT = 30000;


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
    private CountDownTimer timeoutDownloadingRecipes;
    private CountDownTimer timeoutDownloadingPictures;
    @State Boolean isDownloadingRecipes = false;    //Para controlar si está contando o no
    @State Boolean isDownloadingPics = false;

    //Firebase values
    private DatabaseReference mRecipeTimestamps;
    private ValueEventListener timestampListener;
    @State Boolean checkRecipesTimestampFromFirebase = true;
    @State Boolean checkPersonalRecipesTimestampFromFirebase = true;
    //Firebase storage
    FirebaseStorage storage;
    FirebaseDbMethods firebaseDbMethods;


    //Pull de fotos a descargar
    @State
    ObjectQeue pullPictures;

    //private SlideInBottomAnimationAdapter slideAdapter;
    //private RecipeListRecyclerViewAdapter adapter;
    List<RecipeReduced> mRecipes;
    private int savedScrollPosition = 0;
    private int columnCount = 10;
    private String lastFilter;
    private InterstitialAd mInterstitialAd;
    private RecipeItemOld recipeToShow;
    private RecipeController recipeController;

    public RecipeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        pullPictures = ObjectQeue.create(new ArrayList<String>());
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

        //Inicio el temporizador de las recetas si es necesario
        if(timeoutDownloadingRecipes == null){
            timeoutDownloadingRecipes = new CountDownTimer(MAX_MILI_SECONDS_DOWNLOADING_RECIPES, 10000) {

                public void onTick(long millisUntilFinished) {
                    List<RecipeDb> recipes = recipeController.getListOnlyRecipeToDownload(getActivity().getApplication());
                    //Log.d(TAG, "RECETAS --> " + recipeDbs.size() + " : sec --> " + millisUntilFinished/1000);
                    if(recipes.isEmpty()){
                        Log.d(TAG, "Cancelo el timer recetas");
                        ((RecipeListActivity)getActivity()).hideProgressDialog();
                        isDownloadingRecipes = false;
                        Tools tools = new Tools();
                        tools.savePreferences(getContext(), RecetasCookeoConstants.PROPERTY_CAN_UPLOAD_OWN_RECIPES, true);
                        // TODO: 21/2/17 revisar nullpointer al arrancar por primera vez y no dar permisos 
                        firebaseDbMethods.updateOldRecipesToPersonalStorage(getActivity().getApplicationContext());
                        this.cancel();
                    }
                    if((MAX_MILI_SECONDS_DOWNLOADING_RECIPES - millisUntilFinished) > MAX_MILI_SECONDS_SHOWING_REFRESH_LAYOUT){
                        ((RecipeListActivity)getActivity()).hideProgressDialog();
                    }

                }

                public void onFinish() {
                    Log.d(TAG, "acabó el timer recetas");
                    isDownloadingRecipes = false;
                    ((RecipeListActivity)getActivity()).hideProgressDialog();
                    Tools tools = new Tools();
                    tools.savePreferences(getContext(), RecetasCookeoConstants.PROPERTY_CAN_UPLOAD_OWN_RECIPES, true);
                    firebaseDbMethods.updateOldRecipesToPersonalStorage(getActivity().getApplicationContext());
                }
            };
        }

        //Inicio el temporizador de las fotos si es necesario
        if(timeoutDownloadingPictures == null){
            timeoutDownloadingPictures = new CountDownTimer(MAX_MILI_SECONDS_DOWNLOADING_PICTURES, 10000) {

                public void onTick(long millisUntilFinished) {
                    isDownloadingPics = true;
                }

                public void onFinish() {
                    isDownloadingPics = false;
                    // TODO: 26/1/17 refresh recipe list
                }
            };
        }

        //inicio el storage si es necesario
        if(storage == null){
            storage = FirebaseStorage.getInstance();
        }

        recipeController = new RecipeController();
        firebaseDbMethods = new FirebaseDbMethods(recipeController);
    }

    private void requestSignInForNewRecipe(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setMessage(getResources().getString(R.string.create_recipe_explanation))
                .setTitle(getResources().getString(R.string.permissions_title))
                .setPositiveButton(getResources().getString(R.string.sign_in),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ((RecipeListActivity) getActivity()).launchSignInActivity();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
        ;

        builder.create().show();
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
                    if(((RecipeListActivity)getActivity()).isSignedIn){
                        createRecipe();
                    }else{
                        requestSignInForNewRecipe();
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

    public void checkPersonalRecipesFromFirebase(){
        if(checkPersonalRecipesTimestampFromFirebase) {
            connectToFirebaseForNewRecipes(RecetasCookeoConstants.PERSONAL_RECIPES_NODE);
            checkPersonalRecipesTimestampFromFirebase = false;
        }
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
        recipeController = null;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        if(mRecipes == null || mRecipes.size() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_ALL);
            restartLoader(bundle);
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
            savedInstanceState.putParcelableArrayList(KEY_RECIPE_LIST, (ArrayList<RecipeReduced>) mRecipes);
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
        String endPath = args.getString(RecetasCookeoConstants.SEARCH_FIELD);
        Uri CONTENT_URI = CocinaConRollContentProvider.getUri(endPath);
        return new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }else {
            mRecipes.clear();
        }
        List<RecipeDb> recipeDbList = RecipeController.getRecipesFromCursor(getActivity().getApplication(), data);
        if(recipeDbList != null){
            for(RecipeDb recipeDb : recipeDbList){
                RecipeReduced recipe = RecipeReduced.getRecipeFromDatabase(recipeDb);
                mRecipes.add(recipe);
            }
        }

        setData();
        //((RecipeListActivity)getActivity()).performClickInDrawerIfNecessary();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            Tools tools = new Tools();
            //tools.hideRefreshLayout(getActivity());
        }
    }

    public void restartLoader(Bundle bundle){
        getActivity().getSupportLoaderManager().restartLoader(RecetasCookeoConstants.LOADER_ID, bundle, this);
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
    public void onCardClick(View view, RecipeItemOld recipeItemOld) {
        showRecipeDetails(recipeItemOld);
    }



    private void showRecipeDetails(RecipeItemOld recipeItemOld){
        //interstitial
        Tools tools = new Tools();
        int number = tools.getIntegerFromPreferences(getActivity().getApplicationContext(), RecetasCookeoConstants.PREFERENCE_INTERSTITIAL);
        if(number<0 || number> RecetasCookeoConstants.N_RECIPES_TO_INTERSTICIAL){
            number = 0;
        }
        CommonRecipeOperations commonRecipeOperations = new CommonRecipeOperations(getActivity(), recipeItemOld);
        recipeItemOld = commonRecipeOperations.loadRecipeDetailsFromRecipeCard();

        recipeToShow = recipeItemOld;
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
        String type = "";
        int iconResource = 0;
        Bundle bundle = new Bundle();
        if(filter.equals(RecetasCookeoConstants.FILTER_ALL_RECIPES)) {
            type = getResources().getString(R.string.all_recipes);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_ALL);
            iconResource = R.drawable.ic_all_24;
        }else if(filter.equals(RecetasCookeoConstants.FILTER_MAIN_COURSES_RECIPES)){
            type = getResources().getString(R.string.main_courses);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_MAIN);
            iconResource = R.drawable.ic_main_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_STARTER_RECIPES) == 0){
            type = getResources().getString(R.string.starters);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_STARTERS);
            iconResource = R.drawable.ic_starters_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_DESSERT_RECIPES) == 0){
            type = getResources().getString(R.string.desserts);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_DESSERTS);
            iconResource = R.drawable.ic_dessert_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_VEGETARIAN_RECIPES) == 0){
            type = getResources().getString(R.string.vegetarians);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_VEGETARIAN);
            iconResource = R.drawable.ic_vegetarians_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_FAVOURITE_RECIPES) == 0){
            type = getResources().getString(R.string.favourites);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_FAVOURITES);
            iconResource = R.drawable.ic_favorite_black_24dp;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_OWN_RECIPES) == 0){
            type = getResources().getString(R.string.own_recipes);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_OWN);
            iconResource = R.drawable.ic_own_24;
        }else if(filter.compareTo(RecetasCookeoConstants.FILTER_LATEST_RECIPES) == 0){
            type = getResources().getString(R.string.last_downloaded);
            bundle.putString(RecetasCookeoConstants.SEARCH_FIELD, RecetasCookeoConstants.SEARCH_LATEST);
            Tools mTools = new Tools();
            //mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
              //      RecipesTable.FIELD_DATE, mTools.getTimeframe());
            iconResource = R.drawable.ic_latest_24;
        }
        bundle.putInt(RecetasCookeoConstants.SEARCH_ICON_TYPE, iconResource);
        bundle.putString(RecetasCookeoConstants.SEARCH_NAME_TYPE, type);
        restartLoader(bundle);

        typeRecipesInRecipeList.setText(type);
        typeIconInRecipeList.setImageDrawable(ContextCompat.getDrawable(getActivity(), iconResource));

        /*String nrecipes = String.format(getResources().getString(R.string.recipes), mRecipes.size());
        nRecipesInRecipeList.setText(nrecipes);
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

        //adapter = newAdapter;
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        //    slideAdapter = newSlideAdapter;
        //}
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.scrollToPosition(0);

        //Set the fast Scroller
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && fastScroller != null) {
            fastScroller.setRecyclerView(mRecyclerView);
        }*/
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

    public void updateRecipe(RecipeItemOld recipe) {
        // TODO: 21/2/17 hacer esto por recipecontroles
//        if(mRecipes == null){
//            return;
//        }
//        for(int i = 0; i< mRecipes.size(); i++){
//            if(mRecipes.get(i).get_id().intValue() == recipe.get_id().intValue()){
//                mRecipes.remove(i);
//                mRecipes.add(i, recipe);
//                filterRecipes(lastFilter);
//            }
//        }
    }

    public void createRecipe(RecipeItemOld recipe) {
        // TODO: 21/2/17 hacer esto por recipecontroler
//        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
//        dbTools.addRecipeToArrayAndDatabase(getActivity().getApplicationContext(), mRecipes, recipe);
        filterRecipes(lastFilter);
    }

    public void searchAndShow(String name) {
        //// TODO: 21/2/17 hacer con recipecontroler
//        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
//        name = dbTools.getNormalizedString(name);
//        List<RecipeItemOld> coincidences = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
//                RecipesTable.FIELD_NAME_NORMALIZED, dbTools.getNormalizedString(name));
//        if (coincidences.size() > 0) {
//            showRecipeDetails(coincidences.get(0));
//        }
    }

    public void downloadRecipesFromFirebase(){
        //Veo si hay que descargar recetas
        List<RecipeDb> recipeDbs = recipeController.getListBothRecipeAndPicturesToDownload(getActivity().getApplication());
        if(recipeDbs == null || recipeDbs.isEmpty()){
            return;
        }
        List<RecipeDb> onlyRecipeDbs = recipeController.getListOnlyRecipeToDownload(getActivity().getApplication());
        //inicio el timer
        if(!onlyRecipeDbs.isEmpty()) {
            if (!isDownloadingRecipes) {
                isDownloadingRecipes = true;
                timeoutDownloadingRecipes.start();
            }
            //Pongo el loading
            if (this.isResumed()) {
                ((RecipeListActivity) getActivity()).showProgressDialog(getString(R.string.downloading_recipes));
            } else {
                ((RecipeListActivity) getActivity()).setMessage(getString(R.string.downloading_recipes));
                ((RecipeListActivity) getActivity()).needToShowRefresh = true;
            }
        }
        //Descargo las recetas de Firebase
        for(RecipeDb recipeDb : recipeDbs){
            if(recipeDb.getDownloadRecipe()) {
                String node = FirebaseDbMethods.getNodeNameFromRecipeFlag(recipeDb.getOwner());


                if(node.equals(RecetasCookeoConstants.PERSONAL_RECIPES_NODE)){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null && !user.isAnonymous()) {
                        node += "/" + user.getUid();
                    }else{
                        return;
                    }
                }
                //descargo la receta y luego si procede, la foto
                DatabaseReference mRecipeRefDetailed = FirebaseDatabase.getInstance()
                        .getReference(node +
                        "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + recipeDb.getKey());
                mRecipeRefDetailed.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DownloadRecipeTask downloadTask = new DownloadRecipeTask();
                        downloadTask.execute(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else if(recipeDb.getDownloadPicture()){
                //añado la foto al pull de descargas y si no ha empezado a descargar, empiezo
                pullPictures.add(recipeDb.getPicture());
                if(!isDownloadingPics) {
                    isDownloadingPics = !isDownloadingPics;
                    timeoutDownloadingPictures.start();
                    downloadPictureFromStorage();
                }
            }
        }
    }

    private void downloadPictureFromStorage(){
        if(pullPictures.isEmpty()){
            isDownloadingPics = !isDownloadingPics;
            timeoutDownloadingPictures.cancel();
            // TODO: 27/1/17 Llamar a refresh
            return;
        }
        final String name = pullPictures.get(0);
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
                //Log.d(TAG, "Salvado correctamente: " + name);
                //quito del pull y sigo descargando
                pullPictures.remove(name);
                recipeController.updateDownloadRecipeFlag(getActivity().getApplication(), name, false);
                downloadPictureFromStorage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Si ha fallado, borro el temporal y continuo
                Log.d(TAG, "No existe " + name);
                if(imageFile.exists()) {
                    imageFile.delete();
                }
                pullPictures.remove(name);
                downloadPictureFromStorage();
            }
        });

    }

    private void connectToFirebaseForNewRecipes(String node){
        if(node.equals(RecetasCookeoConstants.PERSONAL_RECIPES_NODE)){
            if(!((RecipeListActivity)getActivity()).isSignedIn) {
                Log.d(TAG, "Era no sé");
                return;
            }else{
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                node += "/" + user.getUid();
            }

        }
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
    /**
     * Tarea que almacena las recetas descargadas
     */
    private class DownloadRecipeTask extends AsyncTask<DataSnapshot, Integer, RecipeDb> {

        @Override
        protected RecipeDb doInBackground(DataSnapshot... snapshot) {
            DataSnapshot dataSnapshot = snapshot[0];
            RecipeFirebase recipeFromFirebase = dataSnapshot.getValue(RecipeFirebase.class);
            if(recipeFromFirebase == null)  return null;
            //String key = dataSnapshot.getRef().getParent().getParent().getKey();
            RecipeDb recipeDb = recipeController.insertRecipeFromFirebase(getActivity().getApplication(),
                    dataSnapshot, recipeFromFirebase);
            return recipeDb;
        }

        @Override
        protected void onPostExecute(RecipeDb recipeDb) {
            if(recipeDb == null) return;
            if(recipeDb.getDownloadPicture()){
                pullPictures.add(recipeDb.getPicture());
                if(!isDownloadingPics){
                    isDownloadingPics = !isDownloadingPics;
                    timeoutDownloadingPictures.start();
                    downloadPictureFromStorage();
                }

            }
        }
    }

    /**
     * Tarea que comprueba los timestamps descargados
     */
    private class DownloadTimestampsTask extends AsyncTask<DataSnapshot, Integer, Void>{

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot dataSnapshot = dataSnapshots[0];
            Integer recipeOwner = FirebaseDbMethods.getRecipeFlagFromNodeName(dataSnapshot.getRef().getParent().getKey());
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                TimestampFirebase timestampFirebase = postSnapshot.getValue(TimestampFirebase.class);
                String key = postSnapshot.getKey();
                if(key.equals("-KaplijStKu03-HNpnxG")){
                    int i=0;
                    i++;
                }
                RecipeDb recipeDbFromDatabase = recipeController.getRecipeByKey((Application)getContext().getApplicationContext(), key);
                if(recipeDbFromDatabase == null){
                    //no existía, la creo
                    recipeDbFromDatabase = new RecipeDb();
                }else{
                    //compruebo si la receta es personal
                    if(recipeDbFromDatabase.getName() != null && recipeDbFromDatabase.getName().equals("Albaricoques al vapor")){
                        recipeDbFromDatabase.setVegetarian(recipeDbFromDatabase.getVegetarian());
                    }
                    String nodeName = dataSnapshot.getRef().getParent().getRef().getParent().getKey();
                    Boolean recipeDownloadedOwn = true;
                    if(nodeName == null || !nodeName.equals(RecetasCookeoConstants.PERSONAL_RECIPES_NODE)){
                        recipeDownloadedOwn = false;
                    }
                    Boolean recipeStoredOwn = recipeDbFromDatabase.getOwner().equals(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE);
                    //Casos para continuar y no guardar
                    //  Receta descargada personal, receta almacenada personal con timestamp superior
                    if(recipeDownloadedOwn && recipeStoredOwn &&
                            recipeDbFromDatabase.getTimestamp() >= timestampFirebase.getTimestamp()){
                        continue;
                    }
                    //  receta descargada original, receta almacenada personal (da igual el timestamp)
                    if(!recipeDownloadedOwn && recipeStoredOwn){
                        continue;
                    }
                    //  Receta descargada original, receta almacenada original con timestamp superior
                    if(!recipeDownloadedOwn && recipeDbFromDatabase.getTimestamp() >= timestampFirebase.getTimestamp()){
                        continue;
                    }
                }
                if(recipeDbFromDatabase.getTimestamp() == null ||
                        recipeDbFromDatabase.getTimestamp() < timestampFirebase.getTimestamp()) {
                    recipeDbFromDatabase.setKey(key);
                    recipeDbFromDatabase.setTimestamp(System.currentTimeMillis());
                    recipeDbFromDatabase.setDownloadRecipe(true);
                    recipeDbFromDatabase.setOwner(recipeOwner);
                    recipeController.insertOrReplaceRecipe(getActivity().getApplication(), recipeDbFromDatabase);
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



