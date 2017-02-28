package com.rukiasoft.androidapps.cocinaconroll.ui;


import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.BuildConfig;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.database.CocinaConRollContentProvider;
import com.rukiasoft.androidapps.cocinaconroll.fastscroller.FastScroller;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.TimestampFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.local.ObjectQeue;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeComplete;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeReduced;
import com.rukiasoft.androidapps.cocinaconroll.utilities.CommonRecipeOperations;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import java.io.File;
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
    @State Boolean isDownloadingRecipes = false;    //Para controlar si está contando o no
    @State Boolean isDownloadingPics = false;

    @State Boolean checkRecipesTimestampFromFirebase = true;
    @State Boolean checkPersonalRecipesTimestampFromFirebase = true;
    FirebaseDbMethods firebaseDbMethods;

    @State Boolean firstLoad = true;
    @State int isDownloadingTimestamps;

    Tools mTools;



    //Pull de fotos a descargar
    @State
    ObjectQeue pullItems;

    @State Boolean needToRefresh = false;

    //private SlideInBottomAnimationAdapter slideAdapter;
    //private RecipeListRecyclerViewAdapter adapter;
    List<RecipeReduced> mRecipes;
    private int savedScrollPosition = 0;
    private int columnCount = 10;
    @State String lastFilter = null;
    private InterstitialAd mInterstitialAd;
    // TODO: 27/02/2017 revisar si esto no casca cuando es null
    @State RecipeComplete recipeToShow;
    private RecipeController mRecipeController;
    @State int numRecipesDownloaded;
    @State int numNodesOnInitDatabase;

    Application application;


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
        mTools = new Tools();

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

        mRecipeController = new RecipeController();
        firebaseDbMethods = new FirebaseDbMethods(mRecipeController);

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

        if(addRecipeButtonFAB != null) {
            addRecipeButtonFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTools.getBooleanFromPreferences(getContext().getApplicationContext(),
                            RecetasCookeoConstants.PROPERTY_SIGNED_IN)){
                        createRecipe();
                    }else{
                        requestSignInForNewRecipe();
                    }


                }
            });
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

        unbinder.unbind();
        mRecipeController = null;
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
        if(application == null){
            application = getActivity().getApplication();
        }

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.


        boolean isDatabaseCreated = mTools.getBooleanFromPreferences(getContext(), RecetasCookeoConstants.PROPERTY_DATABASE_CREATED);

        if(lastFilter == null){
            lastFilter = RecetasCookeoConstants.FILTER_ALL_RECIPES;
        }

        if(!isDatabaseCreated){
            downloadRecipesOnFirstLoad();
        }else if(firstLoad){
            firstLoad = false;
            checkNewRecipesFromFirebase();
        }else if(mRecipes == null || mRecipes.size() == 0 || needToRefresh) {
            needToRefresh = false;
            filterRecipes(lastFilter);
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
                if(recipe != null) {
                    mRecipes.add(recipe);
                }
            }
        }

        setData();

    }

    private Boolean needToDownloadRecipes(){
        List<RecipeDb> recipes = mRecipeController.getListBothRecipeAndPicturesToDownload(getActivity().getApplication());
        return recipes != null && !recipes.isEmpty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
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
    public void onCardClick(View view, RecipeReduced recipeReduced) {
        showRecipeDetails(recipeReduced);
    }



    private void showRecipeDetails(RecipeReduced recipeReduced){
        //interstitial
        int number = mTools.getIntegerFromPreferences(getActivity().getApplicationContext(), RecetasCookeoConstants.PREFERENCE_INTERSTITIAL);
        if(number<0 || number> RecetasCookeoConstants.N_RECIPES_TO_INTERSTICIAL){
            number = 0;
        }
        RecipeDb recipeDb = mRecipeController.getRecipeById(getActivity().getApplication(), recipeReduced.getId());
        recipeToShow = RecipeComplete.getRecipeFromDatabase(recipeDb);
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
        mTools.savePreferences(getActivity(), RecetasCookeoConstants.PREFERENCE_INTERSTITIAL, ++number);

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
        if(filter != null && !filter.isEmpty()) {
            lastFilter = filter;
        }
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
            //mRecipes = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
              //      RecipesTable.FIELD_DATE, mTools.getTimeframe());
            iconResource = R.drawable.ic_latest_24;
        }
        bundle.putInt(RecetasCookeoConstants.SEARCH_ICON_TYPE, iconResource);
        bundle.putString(RecetasCookeoConstants.SEARCH_NAME_TYPE, type);
        restartLoader(bundle);

        typeRecipesInRecipeList.setText(type);
        typeIconInRecipeList.setImageDrawable(ContextCompat.getDrawable(getActivity(), iconResource));


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

    public void insertRecipe(RecipeComplete recipe) {
        RecipeDb recipeDb = RecipeDb.fromRecipeComplete(recipe);
        mRecipeController.insertOrReplaceRecipe(application, recipeDb);
        filterRecipes(lastFilter);
    }

    public void searchAndShow(String name) {
        //// TODO: 21/2/17 hacer con recipecontroler
//        DatabaseRelatedTools dbTools = new DatabaseRelatedTools();
//        name = dbTools.getNormalizedString(name);
//        List<RecipeItemOld> coincidences = dbTools.searchRecipesInDatabase(getActivity().getApplicationContext(),
//                RecipesTable.FIELD_NAME_NORMALIZED, dbTools.getNormalizedString(name));
//        if (coincidences.sizePicture() > 0) {
//            showRecipeDetails(coincidences.getPicture(0));
//        }
    }

    ///////////// PARTE DE DESCARGA DE LAS RECETAS ///////////////

    private void downloadRecipesOnFirstLoad() {
        if (isDownloadingRecipes) {
            return;
        }
        if (application == null) {
            return;
        }
        if (getActivity() != null) {
            ((RecipeListActivity) getActivity()).showProgressDialog(getString(R.string.downloading_recipes_first_load));
        }
        numNodesOnInitDatabase = 2;
        downloadRecipesOnFirstLoad(RecetasCookeoConstants.ALLOWED_RECIPES_NODE);
        downloadRecipesOnFirstLoad(RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE);
        boolean isSigned = mTools.getBooleanFromPreferences(application.getApplicationContext(),
                RecetasCookeoConstants.PROPERTY_SIGNED_IN);
        if(isSigned){
            numNodesOnInitDatabase++;
            downloadRecipesOnFirstLoad(RecetasCookeoConstants.PERSONAL_RECIPES_NODE);
        }
    }
    
    private void downloadRecipesOnFirstLoad(String node){

        //descargo las recetas
        DatabaseReference mRecipeRefDetailed = FirebaseDatabase.getInstance()
                .getReference(RecetasCookeoConstants.ALLOWED_RECIPES_NODE +
                        "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE );
        mRecipeRefDetailed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DownloadRecipesOnFirsLoadTask downloadTask = new DownloadRecipesOnFirsLoadTask();
                downloadTask.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: 27/02/2017 ver si cuando no puede acceder al nodo entra aquí (forbidden)
                numNodesOnInitDatabase--;
            }
        });

    }

    private class DownloadRecipesOnFirsLoadTask extends AsyncTask<DataSnapshot, Integer, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... snapshot) {
            RecipeController recipeController = new RecipeController();
            DataSnapshot dataSnapshot = snapshot[0];
            int contador = 0;
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                RecipeFirebase recipeFromFirebase = postSnapshot.getValue(RecipeFirebase.class);
                if (recipeFromFirebase == null) continue;
                //String key = dataSnapshot.getRef().getParent().getParent().getKey();
                if (application == null) {
                    continue;
                }
                recipeController.insertRecipeFromFirebase(application, postSnapshot, recipeFromFirebase);
                contador++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            numNodesOnInitDatabase--;
            if(numNodesOnInitDatabase == 0) {
                mTools.savePreferences(application.getApplicationContext(),
                        RecetasCookeoConstants.PROPERTY_DATABASE_CREATED, true);
                filterRecipes(lastFilter);
                if (getActivity() != null) {
                    ((RecipeListActivity) getActivity()).hideProgressDialog();
                }
                downloadPicturesFromStorage();
            }
        }
    }

    private void checkNewRecipesFromFirebase(){
        if(isDownloadingTimestamps != 0){
            return;
        }
        Boolean isSignedIn = mTools.getBooleanFromPreferences(getContext(), RecetasCookeoConstants.PROPERTY_SIGNED_IN);
        isDownloadingTimestamps = 2;
        connectToFirebaseForNewRecipes(RecetasCookeoConstants.ALLOWED_RECIPES_NODE);
        connectToFirebaseForNewRecipes(RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE);
        if(isSignedIn){
            isDownloadingTimestamps++;
            connectToFirebaseForNewRecipes(RecetasCookeoConstants.PERSONAL_RECIPES_NODE);
        }
    }

    private void connectToFirebaseForNewRecipes(String node){
        if(getActivity() != null) {
            ((RecipeListActivity) getActivity()).showProgressDialog(getString(R.string.downloading_recipes));
        }
        if(node.equals(RecetasCookeoConstants.PERSONAL_RECIPES_NODE)){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            node += "/" + user.getUid();
        }
        DatabaseReference mRecipeTimestamps = FirebaseDatabase.getInstance().getReference(node +
                "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE);
        ValueEventListener timestampListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DownloadTimestampsTask downloadTimestampsTask = new DownloadTimestampsTask();
                downloadTimestampsTask.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.d("no descarga los timestamps");
                isDownloadingTimestamps--;
                startDownloadRecipesIfPossible();
            }
        };
        mRecipeTimestamps.addListenerForSingleValueEvent(timestampListener);
    }

    /**
     * Tarea que comprueba los timestamps descargados
     */
    private class DownloadTimestampsTask extends AsyncTask<DataSnapshot, Integer, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot dataSnapshot = dataSnapshots[0];
            RecipeController recipeController = new RecipeController();
            Integer recipeOwner = FirebaseDbMethods.getRecipeFlagFromNodeName(dataSnapshot.getRef().getParent().getKey());
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                TimestampFirebase timestampFirebase = postSnapshot.getValue(TimestampFirebase.class);
                String key = postSnapshot.getKey();
                if(application == null){
                    continue;
                }
                RecipeDb recipeDbFromDatabase = recipeController.getRecipeByKey(application, key);
                if(recipeDbFromDatabase == null){
                    //no existía, la creo
                    recipeDbFromDatabase = new RecipeDb();
                }else{
                    //compruebo si la receta es personal
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
                    recipeController.insertOrReplaceRecipe(application, recipeDbFromDatabase);
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Llamo a descargar (haya recetas nuevas o no).
            isDownloadingTimestamps--;
            startDownloadRecipesIfPossible();
        }
    }
    private void startDownloadRecipesIfPossible(){
        if(isDownloadingTimestamps == 0){
            numRecipesDownloaded = 0;
            downloadRecipesFromFirebase();
        }
    }

    public void downloadRecipesFromFirebase(){
        //reseteo los contadores
        //Veo si hay que descargar recetas

        isDownloadingRecipes = true;
        if(application == null){
            return;
        }
        RecipeController recipeController = new RecipeController();
        if(pullItems == null || pullItems.isRecipeListEmpty()) {
            List<RecipeDb> list = recipeController.getListOnlyRecipeToDownload(application);
            pullItems = ObjectQeue.create((ArrayList<RecipeDb>) list, null);
        }

        if(pullItems.isRecipeListEmpty()){
            //veo si hay que descargar fotos
            Logger.d("Descargadas todas las recetas");
            isDownloadingRecipes = false;
            if(getActivity() != null){
                ((RecipeListActivity)getActivity()).hideProgressDialog();
            }
            filterRecipes(lastFilter);
            downloadPicturesFromStorage();
            return;
        }

        RecipeDb recipe = pullItems.getRecipe(0);
        String node = FirebaseDbMethods.getNodeNameFromRecipeFlag(recipe.getOwner());

        if (node.equals(RecetasCookeoConstants.PERSONAL_RECIPES_NODE)) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !user.isAnonymous()) {
                node += "/" + user.getUid();
            } else {
                return;
            }
        }
        //descargo la receta
        DatabaseReference mRecipeRefDetailed = FirebaseDatabase.getInstance()
                .getReference(node +
                        "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + recipe.getKey());
        mRecipeRefDetailed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DownloadRecipeTask downloadTask = new DownloadRecipeTask();
                downloadTask.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                numRecipesDownloaded++;
                pullItems.removeRecipe(0);
                downloadRecipesFromFirebase();
            }
        });

    }

    //ASYNCTASKS
    /**
     * Tarea que almacena las recetas descargadas
     */
    private class DownloadRecipeTask extends AsyncTask<DataSnapshot, Integer, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... snapshot) {
            RecipeController recipeController = new RecipeController();
            DataSnapshot dataSnapshot = snapshot[0];
            RecipeFirebase recipeFromFirebase = dataSnapshot.getValue(RecipeFirebase.class);
            if(recipeFromFirebase == null)  return null;
            //String key = dataSnapshot.getRef().getParent().getParent().getKey();
            if(application == null){
                return null;
            }
            recipeController.insertRecipeFromFirebase(application, dataSnapshot, recipeFromFirebase);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            numRecipesDownloaded++;
            if(numRecipesDownloaded%20 == 0){
                filterRecipes(lastFilter);
            }
            pullItems.removeRecipe(0);
            downloadRecipesFromFirebase();
        }
    }


    private void downloadPicturesFromStorage(){
        isDownloadingPics = true;
        if(application == null){
            return;
        }
        RecipeController recipeController = new RecipeController();
        if(pullItems == null || pullItems.isPictureListEmpty()) {
            List<RecipeDb> list = recipeController.getListOnlyPicturesToDownload(application);
            pullItems = ObjectQeue.create(null, (ArrayList<RecipeDb>) list);
            if(getContext() != null) {
                Toast.makeText(getContext(), getString(R.string.downloading_pictures),
                        Toast.LENGTH_LONG).show();
            }
            // TODO: 26/2/17 mostrar toast
        }
        if(pullItems.isPictureListEmpty()){
            //veo si hay que descargar fotos
            Logger.d("Descargadas todas las fotos");
            isDownloadingPics = false;
            filterRecipes(lastFilter);
            return;
        }

        RecipeDb picture = pullItems.getPicture(0);
        final String name = picture.getPicture();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef;
        if(picture.getOwner().equals(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE)){
            FirebaseDbMethods fbMethods = new FirebaseDbMethods(new RecipeController());
            FirebaseUser user = fbMethods.getCurrentUser();
            imageRef = storageRef.child("personal/" + user.getUid() + "/" + name);
        }else{
            imageRef = storageRef.child("recipes/" + name);
        }

        ReadWriteTools rwTools = new ReadWriteTools();
        String path = rwTools.getOriginalStorageDir(application);
        final File imageFile = new File(path + name);

        imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Logger.d("Salvado correctamente: " + name);
                if(application != null){
                    RecipeController recipeController = new RecipeController();
                    recipeController.updateDownloadRecipeFlag(application, name, false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Logger.d("error descargando: " + name);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                pullItems.removePicture(0);
                downloadPicturesFromStorage();
            }
        });

    }



}



