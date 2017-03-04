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
import android.content.Context;
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
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeSearch;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.RecipeViewHolder>
        implements View.OnClickListener{

    private final List<RecipeSearch> mItems;
    private OnItemClickListener onItemClickListener;


    SearchRecyclerViewAdapter(List<RecipeSearch> items) {
        this.mItems = new ArrayList<>(items);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(v);
        v.setOnClickListener(this);
        return recipeViewHolder;
    }



    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        RecipeSearch item = mItems.get(position);
        holder.bindRecipe(item);
        holder.itemView.setTag(item);


    }

    @Override public int getItemCount() {
        return mItems.size();
    }

    @Override public long getItemId(int position){
        return mItems.get(position).hashCode();
    }

    @Override
    public void onClick(final View v) {

        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    onItemClickListener.onItemClick(v, (RecipeSearch) v.getTag());
                }
            }, 200);
        }
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_recipe_icon) ImageView recipeTypeThumbnail;
        @BindView(R.id.search_recipe_name) TextView recipeTitle;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        void bindRecipe(RecipeSearch item) {
            recipeTitle.setText(item.getName());
            recipeTypeThumbnail.setImageDrawable(ContextCompat.getDrawable(recipeTitle.getContext(), (item.getIcon())));
        }

    }

    interface OnItemClickListener {
        void onItemClick(View view, RecipeSearch recipe);
    }


}
