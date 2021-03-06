package com.rukiasoft.androidapps.cocinaconroll.ui;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rukiasoft.androidapps.cocinaconroll.CocinaConRollApplication;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.dragandswipehelper.OnStartDragListener;
import com.rukiasoft.androidapps.cocinaconroll.dragandswipehelper.SimpleItemTouchHelperCallback;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EditRecipeIngredientsFragment extends Fragment implements OnStartDragListener {

    private Boolean showSwipe = true;
    @BindView(R.id.edit_recipe_add_item)EditText addItem;
    @BindView(R.id.edit_recipe_add_fab)FloatingActionButton fab;
    @BindView(R.id.edit_recipe_recycler_view) RecyclerView recyclerView;
    private Unbinder unbinder;

    private EditRecipeRecyclerViewAdapter mAdapter;

    private ItemTouchHelper mItemTouchHelper;
    private Tools mTools;

    public EditRecipeIngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTools = new Tools();
        setRetainInstance(true);
        showSwipe = showSwipeDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_recipe_items_ingredients, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mAdapter == null) {
            ContentValues recipeCV = ((EditRecipeActivity) getActivity()).getRecipeCV();
            List<String> ingredients = new ArrayList<>();
            int i=0;
            while(recipeCV.containsKey(RecetasCookeoConstants.RECIPE_COMPLETE_INGREDIENT + i)){
                ingredients.add(recipeCV.getAsString(RecetasCookeoConstants.RECIPE_COMPLETE_INGREDIENT + i));
                i++;
            }
            mAdapter = new EditRecipeRecyclerViewAdapter(ingredients, this);

        }
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }



    public ContentValues saveData(){
        List<String> ingredients = mAdapter.getItems();
        ContentValues recipeCV = ((EditRecipeActivity)getActivity()).getRecipeCV();
        for(int i=0; i<ingredients.size(); i++){
            recipeCV.put(RecetasCookeoConstants.RECIPE_COMPLETE_INGREDIENT + i, ingredients.get(i));
        }
        return recipeCV;
    }


    @Override
    public void onResume(){
        super.onResume();
        if(showSwipe) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();


            final View viewSwipe = inflater.inflate(R.layout.dialog_swipe, null);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(viewSwipe)
                    // Add action buttons
                    .setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            CheckBox swipe = (CheckBox) viewSwipe.findViewById(R.id.checkbox_swipe);
                            hideSwipeDialog(swipe.isChecked());
                            showSwipe = false;
                        }
                    });

            builder.show();
        }
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

    private Boolean showSwipeDialog(){
        Tools mTools = new Tools();
        return !mTools.getBooleanFromPreferences(getActivity(), RecetasCookeoConstants.PROPERTY_HIDE_SWIPE_DIALOG);
    }

    private void hideSwipeDialog(Boolean state){
        Tools mTools = new Tools();
        mTools.savePreferences(getActivity(), RecetasCookeoConstants.PROPERTY_HIDE_SWIPE_DIALOG, state);
    }
}

