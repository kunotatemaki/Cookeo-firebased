/*
 * Copyright (C) 2015 Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.app.Application;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.LikeButtonView;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeReduced;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

class RecipeListRecyclerViewAdapter extends RecyclerView.Adapter<RecipeListRecyclerViewAdapter.RecipeViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private OnCardClickListener onCardClickListener;
    private View frontCard = null;
    private View backCard = null;
    private Cursor mCursor;
    private Application application;


    RecipeListRecyclerViewAdapter(Application _application) {
        this.application = _application;
    }

    void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_recycler_item, parent, false);
        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(v);
        recipeViewHolder.cardView.setOnClickListener(this);
        recipeViewHolder.cardView.setOnLongClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if(recipeViewHolder.backCardView != null){
                recipeViewHolder.backCardView.setRotationY(180);
            }
        }

        return recipeViewHolder;
    }



    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        RecipeReduced item = getItem(position);
        if(item == null){
            return;
        }
        holder.bindRecipe(item);
        holder.itemView.setTag(item.getId());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (holder.cardView.getRotationY() != 0) {
                setFrontAndBack(holder.cardView);
                flipCard(holder.cardView);
            }
        }

    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    private RecipeReduced getItem(int position){
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid item position requested");
        }
        return RecipeReduced.getFromCursor(application, mCursor);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public void onClick(final View v) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setFrontAndBack(v);
            if (v.getRotationY() != 0) {
                return;
            }
        }

        // Give some time to the ripple to finish the effect
        if (onCardClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    onCardClickListener.onCardClick(v, (Long) v.getTag());
                }
            }, 200);
        }
    }

    @Override
    public boolean onLongClick(View v) {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setFrontAndBack(v);
            if (frontCard != null && backCard != null) {
                flipCard(v);
            }
        }

        //setLeftIn.start();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setFrontAndBack(View v){
        View faceA = null;
        View faceB = null;
        for(int i=0; i<((ViewGroup)v).getChildCount(); ++i) {
            View nextChild = ((ViewGroup)v).getChildAt(i);
            int id = nextChild.getId();
            if(id == R.id.back_cardview_recipe_item){
                faceB = nextChild;
            }else if(id == R.id.front_cardview_recipe_item) {
                faceA = nextChild;
            }
        }
       if (v.getRotationY() != 0) {
            frontCard = faceB;
            backCard = faceA;
        } else {
            frontCard = faceA;
            backCard = faceB;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void flipCard(final View card){
        final View front = frontCard;
        final View back = backCard;
        final AnimatorSet flipCard = (card.getRotationY() == 0)?
                (AnimatorSet) AnimatorInflater.loadAnimator(card.getContext(), R.animator.card_flip_rotate_half) :
                (AnimatorSet) AnimatorInflater.loadAnimator(card.getContext(), R.animator.card_flip_rotate_full);
        final AnimatorSet disappear = (AnimatorSet) AnimatorInflater.loadAnimator(card.getContext(), R.animator.view_disappear);
        //final AnimatorSet flipPositive = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.card_flip_rotate_full);
        final AnimatorSet appear = (AnimatorSet) AnimatorInflater.loadAnimator(card.getContext(), R.animator.view_appear);
        flipCard.setTarget(card);
        disappear.setTarget(front);
        //flipPositive.setTarget(card);
        appear.setTarget(back);
        //setLeftIn.setTarget(backView);
        flipCard.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                disappear.start();
                appear.start();
                back.setVisibility(View.VISIBLE);
                back.setAlpha(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                front.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        flipCard.start();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_pic_cardview) ImageView recipeThumbnail;
        @BindView(R.id.recipe_title_cardview) TextView recipeTitle;
        @BindView(R.id.recipe_pic_protection_cardview) ImageView backgroundProtection;
        @BindView(R.id.recipe_item_favorite_icon) ImageView favoriteIcon;
        @BindView(R.id.recipe_item_own_recipe_icon) ImageView ownRecipeIcon;
        @BindView(R.id.recipe_item_type_icon) ImageView typeIcon;
        @BindView(R.id.recipe_item_vegetarian_recipe_icon) ImageView vegetarianIcon;
        @BindView(R.id.cardview_recipe_item)
        CardView cardView;
        @BindView(R.id.front_cardview_recipe_item)
        LinearLayout frontCardView;
        @Nullable @BindView(R.id.back_cardview_recipe_item)
        RelativeLayout backCardView;
        @Nullable @BindView(R.id.recipe_item_favorite_button)
        LikeButtonView favoriteButton;
        ReadWriteTools rwTools;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        void bindRecipe(RecipeReduced item) {
            if(rwTools == null) rwTools = new ReadWriteTools();
            recipeTitle.setText(item.getName());
            int visibilityProtection = View.GONE;
            if(favoriteButton != null) {
                favoriteButton.init(item, favoriteIcon);
            }
            if(item.getFavourite()){
                visibilityProtection = View.VISIBLE;
                favoriteIcon.setVisibility(View.VISIBLE);
            }else{
                favoriteIcon.setVisibility(View.GONE);
            }
            if(item.getOwner().equals(RecetasCookeoConstants.FLAG_PERSONAL_RECIPE) ||
                    item.getEdited()){
                visibilityProtection = View.VISIBLE;
                ownRecipeIcon.setVisibility(View.VISIBLE);
            }else{
                ownRecipeIcon.setVisibility(View.GONE);
            }
            if(item.getVegetarian()){
                visibilityProtection = View.VISIBLE;
                vegetarianIcon.setVisibility(View.VISIBLE);
            }else{
                vegetarianIcon.setVisibility(View.GONE);
            }
            backgroundProtection.setVisibility(visibilityProtection);

            typeIcon.setImageDrawable(ContextCompat.getDrawable(typeIcon.getContext(), (item.getIcon())));

            rwTools.loadImageFromPath(recipeThumbnail.getContext(), recipeThumbnail, item.getPicture(),
                    R.drawable.default_dish_thumb, item.getTimestamp());
        }

    }

    interface OnCardClickListener {
        void onCardClick(View view, Long id);
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
