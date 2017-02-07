package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeDetailed;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeTimestamp;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iRoll on 7/2/17.
 */

public class DatabaseMethods {

    public void updateRecipeToPersonalStorage(RecipeItem recipe){


        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("/" + RecetasCookeoConstants.PERSONAL_RECIPES_NODE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null || user.isAnonymous()){
            return;
        }

        String key = ref.child(user.getUid()).push().getKey();

        RecipeDetailed recipeDetailed = new RecipeDetailed(recipe);
        RecipeTimestamp recipeTimestamp = new RecipeTimestamp();

        Map<String, Object> postDetailedValues = recipeDetailed.toMap();
        Map<String, Object> postTimestamp = recipeTimestamp.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + key, postDetailedValues);
        childUpdates.put("/" + user.getUid() + "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE + "/" + key, postTimestamp);

        ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                }
            }
        });
    }
}
