package com.rukiasoft.androidapps.cocinaconroll.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeComplete;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.dragandswipehelper.OnStartDragListener;
import com.rukiasoft.androidapps.cocinaconroll.dragandswipehelper.SimpleItemTouchHelperCallback;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EditRecipeStepsFragment extends Fragment implements OnStartDragListener {

    private static final String KEY_ITEM_TO_ADD = RecetasCookeoConstants.PACKAGE_NAME + ".itemtoadd";
    @BindView(R.id.edit_recipe_add_item)EditText addItem;
    @BindView(R.id.edit_recipe_add_fab)FloatingActionButton fab;
    @BindView(R.id.edit_recipe_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.edit_recipe_tip_text) EditText tip;
    private Unbinder unbinder;

    private EditRecipeRecyclerViewAdapter mAdapter;

    private ItemTouchHelper mItemTouchHelper;
    private Tools mTools;


    public EditRecipeStepsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTools = new Tools();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_recipe_items_steps, container, false);
        unbinder = ButterKnife.bind(this, view);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!addItem.getText().toString().isEmpty()) {
                    mAdapter.addItem(addItem.getText().toString());
                    mTools.hideSoftKeyboard(getActivity());
                    addItem.setText("");
                }
            }
        });


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = CocinaConRollApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecipeComplete recipe = ((EditRecipeActivity)getActivity()).getRecipe();
        if(tip.getText().toString().isEmpty() && recipe != null) {
            tip.setText(recipe.getTip());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mAdapter == null) {
            RecipeComplete recipe = ((EditRecipeActivity) getActivity()).getRecipe();
            List<String> steps = new ArrayList<>();
            if (recipe.getSteps() != null) {
                steps = recipe.getSteps();
            }
            mAdapter = new EditRecipeRecyclerViewAdapter(steps, this);
        }
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }



    public RecipeComplete saveData(){
        RecipeComplete recipe = ((EditRecipeActivity)getActivity()).getRecipe();
        String sTip = tip.getText() != null? tip.getText().toString() : "";
        return RecipeComplete.getRecipeFrom3Screen(recipe, mAdapter.getItems(), sTip);
    }

}
