package com.rukiasoft.androidapps.cocinaconroll.ui;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.database.CocinaConRollContentProvider;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeComplete;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;
import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.State;


public class RecipeDetailsFragment extends Fragment implements
        AppBarLayout.OnOffsetChangedListener{
    private static final float PERCENTAGE_TO_ELLIPSIZE_TITLE  = 0.1f;


    @BindView(R.id.recipe_details_icon_minutes) ImageView iconMinutes;
    @BindView(R.id.recipe_details_icon_portions) ImageView iconPortions;
    @BindView(R.id.recipe_details_text_minutes) TextView textMinutes;
    @BindView(R.id.recipe_details_text_portions) TextView textPortions;
    @BindView(R.id.tip_body_cardview) TextView tip;
    @BindView(R.id.card_tip)
    CardView cardTip;
    @BindView(R.id.recipe_pic) ImageView mPhotoView;
    @Nullable@BindView(R.id.appbarlayout_recipe_details) AppBarLayout mAppBarLayout;
    @Nullable@BindView(R.id.photo_container_recipe_details)
    RelativeLayout photoContainer;
    @BindView(R.id.toolbar_recipe_details)Toolbar toolbarRecipeDetails;
    @BindView(R.id.recipe_name_recipe_details) TextView recipeName;
    @BindView(R.id.recipe_description_fab)
    FloatingActionButton recipeDescriptionFAB;
    @Nullable@BindView(R.id.collapsing_toolbar_recipe_details)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.listview_ingredients_cardview)
    LinearLayout ingredientsList;
    @BindView(R.id.listview_steps_cardview)
    LinearLayout stepsList;
    private Unbinder unbinder;
    private ActionBar actionBar;
    @BindView(R.id.cardview_link_textview) TextView author;
    private boolean land;
    @State boolean animated = false;
    private View viewToReveal;
    private ReadWriteTools rwTools;
    RecipeController mRecipeController;


    private final DialogInterface.OnClickListener editDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    FirebaseDbMethods firebaseDbMethods = new FirebaseDbMethods(mRecipeController);
                    firebaseDbMethods.share(getActivity().getApplication(),
                            ((RecipeDetailActivity)getActivity()).getRecipeId());
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
    private boolean collapsed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().supportPostponeEnterTransition();
        }
        setHasOptionsMenu(true);
        rwTools = new ReadWriteTools();
        mRecipeController = new RecipeController();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_description_menu, menu);
        RecipeComplete recipe = ((RecipeDetailActivity)getActivity()).getRecipe();
        if(recipe != null) {
            menu.findItem(R.id.menu_item_remove).setVisible(recipe.getEdited() |
                    recipe.getOwner().equals(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE));
            menu.findItem(R.id.menu_item_share_recipe).setVisible(recipe.getOwner().equals(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE));
        }


    }


    private final DialogInterface.OnClickListener removeDialogClickListener = new DialogInterface.OnClickListener() {


        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    RecipeComplete recipe = ((RecipeDetailActivity)getActivity()).getRecipe();
                    RecipeController recipeController = new RecipeController();
                    recipeController.setRecipeForDeleting(getActivity().getApplication(), recipe.getId());
                    FirebaseDbMethods firebaseDbMethods = new FirebaseDbMethods(recipeController);
                    firebaseDbMethods.deleteRecipe(getActivity().getApplication(), recipe.getKey(),
                            recipe.getId(), recipe.getPicture());
                    getActivity().finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Tools tools = new Tools();
        RecipeComplete recipe = ((RecipeDetailActivity)getActivity()).getRecipe();
        switch (item.getItemId()) {
            case R.id.menu_item_edit_recipe:
                if(tools.getBooleanFromPreferences(getContext().getApplicationContext(),
                        RecetasCookeoConstants.PROPERTY_SIGNED_IN)){
                    editRecipe();
                }else{
                    requestSignInForNewRecipe();
                }
                return true;
            case R.id.menu_item_remove:
                AlertDialog.Builder removeBuilder = new AlertDialog.Builder(getActivity());
                String message;
                if(recipe.getOwner() == RecetasCookeoConstants.FLAG_PERSONAL_RECIPE ||
                        recipe.getEdited()){
                    message = getActivity().getResources().getString(R.string.delete_recipe_confirmation);
                }else{
                    return false;
                }

                removeBuilder.setMessage(message)
                        .setPositiveButton((getActivity().getResources().getString(R.string.Yes)), removeDialogClickListener)
                        .setNegativeButton((getActivity().getResources().getString(R.string.No)), removeDialogClickListener);
                removeBuilder.show();
                return true;
            case R.id.menu_item_share_recipe:
                AlertDialog.Builder shareBuilder = new AlertDialog.Builder(getActivity());
                message = getResources().getString(R.string.share_confirmation);
                shareBuilder.setMessage(message)
                        .setPositiveButton((getResources().getString(R.string.Yes)), editDialogClickListener)
                        .setNegativeButton((getResources().getString(R.string.No)), editDialogClickListener);
                shareBuilder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editRecipe(){
        Intent intent = new Intent(getActivity(), EditRecipeActivity.class);
        Uri uri = CocinaConRollContentProvider.getUriForRecipe(((RecipeDetailActivity) getActivity()).getRecipeId());
        intent.setData(uri);
        getActivity().startActivityForResult(intent, RecetasCookeoConstants.REQUEST_EDIT_RECIPE);
    }

    private final Runnable scaleIn = new Runnable() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            recipeDescriptionFAB.animate().setDuration(250)
                    .setInterpolator(new AnticipateOvershootInterpolator())
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .withEndAction(scaleOut);
        }
    };

    private final Runnable scaleOut = new Runnable() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            recipeDescriptionFAB.animate().setDuration(250)
                    .setInterpolator(new AnticipateOvershootInterpolator())
                    .scaleX(1.0f)
                    .scaleY(1.0f);
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        land = getResources().getBoolean(R.bool.land);



        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbarRecipeDetails);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(!land);
        }

        if(mAppBarLayout != null){
            mAppBarLayout.addOnOffsetChangedListener(this);
        }

        if(recipeDescriptionFAB != null) {

            recipeDescriptionFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                clickOnHeartButton();
                            }
                        }, 150);
                    }else{
                        clickOnHeartButton();
                    }
                }
            });
        }

        RecipeComplete recipe = ((RecipeDetailActivity)getActivity()).getRecipe();

        loadRecipe(recipe);

        if(animated){
            return mRootView;
        }
        //create de reveal effect either for landscape and portrait
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!land && mAppBarLayout != null) {
                viewToReveal = mAppBarLayout;
            } else if (land && photoContainer != null) {
                viewToReveal = photoContainer;
                collapsed = false;
            }
            viewToReveal.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    Animator animator = ViewAnimationUtils.createCircularReveal(
                            viewToReveal,
                            viewToReveal.getWidth() / 2,
                            viewToReveal.getHeight() / 2,
                            0,
                            (float) Math.hypot(viewToReveal.getWidth(), viewToReveal.getHeight()) / 2);
                    // Set a natural ease-in/ease-out interpolator.
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());

                    // make the view visible and start the animation
                    if (!collapsed) {
                        animator.start();
                        animated = true;
                    }
                }
            });

        }
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = CocinaConRollApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    private void clickOnHeartButton(){
        RecipeComplete recipe = RecipeComplete.getRecipeFromDatabase(mRecipeController.switchFavourite(getActivity().getApplication(),
                ((RecipeDetailActivity)getActivity()).getRecipeId()));
        if(recipe == null){
            return;
        }
        if (!recipe.getFavourite()) {
            recipeDescriptionFAB.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.ic_favorite_outline_white_24dp));
        } else {
            recipeDescriptionFAB.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.ic_favorite_white_24dp));
        }
        getActivity().setResult(Activity.RESULT_OK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            scaleIn.run();
        }
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        if(land){
            collapsed = false;
            return;
        }
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        collapsed = percentage == 1;
        handleTitleBehavior(percentage);
        //handleToolbarTitleVisibility(percentage);

    }

    private void handleTitleBehavior(float percentage) {
        if (percentage >= PERCENTAGE_TO_ELLIPSIZE_TITLE) {
            recipeName.setVisibility(View.GONE);
        }else{
            recipeName.setVisibility(View.VISIBLE);
            recipeName.setAlpha(1 - percentage / PERCENTAGE_TO_ELLIPSIZE_TITLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void loadRecipe(RecipeComplete recipe) {

        if (recipeName != null) {
            recipeName.setText(recipe.getName());
        }
        if (recipe.getMinutes() > 0) {
            textMinutes.setText(String.valueOf(recipe.getMinutes()));
            textMinutes.setVisibility(View.VISIBLE);
            iconMinutes.setVisibility(View.VISIBLE);
        } else {
            textMinutes.setVisibility(View.GONE);
            iconMinutes.setVisibility(View.GONE);
        }
        if (recipe.getPortions() > 0) {
            textPortions.setText(String.valueOf(recipe.getPortions()));
            textPortions.setVisibility(View.VISIBLE);
            iconPortions.setVisibility(View.VISIBLE);
        } else {
            textPortions.setVisibility(View.GONE);
            iconPortions.setVisibility(View.GONE);
        }
        if (actionBar != null) {
            actionBar.setTitle(recipe.getName());
        }
        if (recipeDescriptionFAB != null) {
            if (recipe.getFavourite()) {
                recipeDescriptionFAB.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_white_24dp));
            } else {
                recipeDescriptionFAB.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_outline_white_24dp));
            }
        }
        if (mPhotoView != null) {
            BitmapImageViewTarget bitmapImageViewTarget = new BitmapImageViewTarget(mPhotoView) {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                    super.onResourceReady(bitmap, anim);
                    applyPalette(bitmap);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    Bitmap bitmap = ((BitmapDrawable) errorDrawable).getBitmap();
                    //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_dish);
                    applyPalette(bitmap);
                }
            };
            rwTools.loadImageFromPath(getActivity().getApplicationContext(),
                    bitmapImageViewTarget, recipe.getPicture(),
                    R.drawable.default_dish, recipe.getTimestamp());
        }

        //Set the author
        String sAuthor = getResources().getString(R.string.default_author);
        if (recipe.getAuthor().equals(sAuthor) || recipe.getLink() == null || recipe.getLink().isEmpty()){
            author.setText(getString(R.string.author).concat(" ").concat(recipe.getAuthor()));
        }else {
            String link = getString(R.string.original_link)
                    .concat(" <a href=\"")
                    .concat(recipe.getLink())
                    .concat("\">")
                    .concat(recipe.getAuthor())
                    .concat("</a> ");
            Spanned linkFormatted;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                linkFormatted = Html.fromHtml(link, Html.FROM_HTML_MODE_LEGACY);
            } else {
                linkFormatted = Html.fromHtml(link);
            }
            author.setText(linkFormatted);
            author.setMovementMethod(LinkMovementMethod.getInstance());
        }

        //set ingredients and steps
        ingredientsList.removeAllViews();
        for(String ingredient : recipe.getIngredients()){
            LayoutInflater inflater;
            inflater = (LayoutInflater) getActivity()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View ingredientItem = inflater.inflate(R.layout.recipe_description_item, null);
            TextView textView = (TextView) ingredientItem.findViewById(R.id.recipe_description_item_description);
            textView.setText(ingredient);
            ImageView icon = (ImageView) ingredientItem.findViewById(R.id.recipe_description_item_icon);
            icon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bone));
            ingredientsList.addView(ingredientItem);
        }
        stepsList.removeAllViews();
        for(String step : recipe.getSteps()){
            LayoutInflater inflater;
            inflater = (LayoutInflater) getActivity()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View stepItem = inflater.inflate(R.layout.recipe_description_item, null);
            TextView textView = (TextView) stepItem.findViewById(R.id.recipe_description_item_description);
            textView.setText(step);
            ImageView icon = (ImageView) stepItem.findViewById(R.id.recipe_description_item_icon);
            icon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dog_foot));
            stepsList.addView(stepItem);
        }

        //set tip
        if (recipe.getTip() != null && !recipe.getTip().isEmpty()) {
            cardTip.setVisibility(View.VISIBLE);
            tip.setText(recipe.getTip());
        }else{
            cardTip.setVisibility(View.GONE);
        }

    }

    private void applyPalette(Bitmap bitmap){
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                if(palette == null)
                    return;
                try {
                    int mVibrantColor = palette.getVibrantColor(ContextCompat.getColor(getActivity(), R.color.ColorPrimary));
                    //int mVibrantDarkColor = palette.getDarkVibrantColor(mVibrantColor);
                    int mMutedColor = palette.getMutedColor(ContextCompat.getColor(getActivity(), R.color.ColorAccent));
                    int mMutedDarkColor = palette.getDarkMutedColor(mMutedColor);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(mMutedDarkColor);
                    }
                    if (collapsingToolbarLayout != null) {
                        collapsingToolbarLayout.setContentScrim(new ColorDrawable(mMutedColor));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        recipeDescriptionFAB.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{mVibrantColor}));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if(getActivity() != null) {
                        getActivity().supportStartPostponedEnterTransition();
                    }
                }

            }
        });

    }

    private void requestSignInForNewRecipe(){
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(getActivity());

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


}
