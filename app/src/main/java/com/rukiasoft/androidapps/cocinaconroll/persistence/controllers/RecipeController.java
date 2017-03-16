

        package com.rukiasoft.androidapps.cocinaconroll.persistence.controllers;


        import android.app.Application;
        import android.database.Cursor;

        import com.google.firebase.database.DataSnapshot;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.daoqueries.RecipeQueries;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.model.DaoSession;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.model.IngredientDb;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDbDao;
        import com.rukiasoft.androidapps.cocinaconroll.persistence.model.StepDb;
        import com.rukiasoft.androidapps.cocinaconroll.ui.model.RecipeReduced;
        import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

        import org.greenrobot.greendao.query.Query;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by iRoll on 9/2/17.
 */

public class RecipeController {

    public RecipeController(){

    }

    // STATIC ZONE

    public static List<RecipeDb> getRecipesFromCursor(Application application, Cursor cursor) {
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        RecipeDbDao recipeDao = session.getRecipeDbDao();
        List<RecipeDb> recipeDbList = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst()){
            do {
                RecipeDb recipeDb = recipeDao.readEntity(cursor, 0);
                recipeDbList.add(recipeDb);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return recipeDbList;
    }

    // GET ZONE

    public List<RecipeReduced> getAllRecipes(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        List<RecipeDb> listDb = RecipeQueries.getQueryAllRecipes(session).list();
        return RecipeController.getListReducedFromDb(listDb);
    }

    public Cursor getRecipesByTypeInCursorFormat(Application application, String type){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getCursorRecipesByType(session, type);
    }

    public Cursor getVegetarianRecipesInCursorFormat(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getCursorVegetarianRecipes(session);
    }

    public Cursor getFavouriteRecipesInCursorFormat(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getCursorFavouriteRecipes(session);
    }

    public Cursor getOwnRecipesInCursorFormat(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getCursorOwnRecipes(session);
    }

    public Cursor getLatestRecipesInCursorFormat(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getCursorLatestRecipes(session);
    }

    private static List<RecipeReduced> getListReducedFromDb(List<RecipeDb> listDb){
        List<RecipeReduced> listReduced = new ArrayList<>();
        if(listDb != null) {
            for (RecipeDb recipeDb : listDb) {
                RecipeReduced recipeReduced = RecipeReduced.getRecipeFromDatabase(recipeDb);
                listReduced.add(recipeReduced);
            }
        }
        return listReduced;
    }

    public Cursor getRecipesInCursorFormat(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getCursorAllRecipes(session);
    }

    public RecipeDb getRecipeByKey(Application application, String key){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        Query query = RecipeQueries.getQueryRecipeByKey(session);
        query.setParameter(0, key);
        RecipeDb recipeDb = (RecipeDb) query.unique();
        if(recipeDb != null) {
            recipeDb.getIngredients();
            recipeDb.getSteps();
        }
        return recipeDb;
    }

    public RecipeDb getRecipeById(Application application, Long id){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        Query query = RecipeQueries.getQueryRecipeById(session);
        query.setParameter(0, id);
        RecipeDb recipeDb = (RecipeDb) query.unique();
        if(recipeDb != null) {
            recipeDb.getIngredients();
            recipeDb.getSteps();
        }
        return recipeDb;
    }

    public List<RecipeDb> getListBothRecipeAndPicturesToDownload(Application application){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getQueryBothRecipesAndPicturesToDownload(session).list();
    }

    public List<RecipeDb> getListOnlyRecipeToUpdate(Application application, int action){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getQueryOnlyRecipesToUpdate(session, action).list();
    }

    public List<RecipeDb> getListOnlyPicturesToUpdate(Application application, int action){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getQueryOnlyPicturesToUpdate(session, action).list();
    }

    public void updateDownloadPictureFlag(Application application, long id, int downloadFlag) {
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        Query query = RecipeQueries.getQueryRecipeById(session);
        query.setParameter(0, id);
        RecipeDb recipeDbFromDatabase = (RecipeDb) query.unique();
        if(recipeDbFromDatabase != null) {
            recipeDbFromDatabase.setUpdatePicture(downloadFlag);
            recipeDbFromDatabase.update();
        }
    }


    public RecipeDb getRecipeByExactName(Application application, String name) {
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        Query queryRecipe = RecipeQueries.getQueryRecipesByName(session);
        queryRecipe.setParameter(0, name);
        List<RecipeDb> recipeDbList = queryRecipe.list();
        RecipeDb recipeDb = null;
        if(recipeDbList != null && recipeDbList.size()>0){
            recipeDb = recipeDbList.get(0);
            recipeDb.getIngredients();
            recipeDb.getSteps();
        }
        return recipeDb;
    }

    public Cursor getRecipesByName(Application application, String name) {

        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        return RecipeQueries.getCursorRecipesByName(session, name);


    }


    // INSERT ZONE

    public RecipeDb insertOrReplaceRecipe(Application application, RecipeDb recipeDb){
        DaoSession session = CommonController.getDaosessionFromApplication(application, "RecipeDb");
        RecipeDbDao recipeDao = session.getRecipeDbDao();
        recipeDao.detachAll();
        recipeDao.insertOrReplace(recipeDb);

        //Guardo los ingredientes si los hay
        if(recipeDb.getIngredients() != null){
            insertOrReplaceRecipeIngredients(application, recipeDb);
        }
        if(recipeDb.getSteps() != null){
            insertOrReplaceRecipeSteps(application, recipeDb);
        }

        return getRecipeById(application, recipeDb.getId());


    }

    public RecipeDb insertRecipeFromFirebase(Application application, DataSnapshot dataSnapshot, RecipeFirebase recipeFromFirebase) {
        Integer flag = FirebaseDbMethods.getRecipeFlagFromNodeName(dataSnapshot.getRef().getParent().getParent().getKey());
        RecipeDb recipeDb = new RecipeDb(recipeFromFirebase, dataSnapshot.getKey(), flag);

        //grabo la receta y los ingredientes/procedimientos
        insertOrReplaceRecipe(application, recipeDb);

        //devuelvo la receta que he grabado
        return getRecipeByKey(application, dataSnapshot.getKey());
    }

    private void insertOrReplaceRecipeIngredients(Application application, RecipeDb recipeDb){
        List<IngredientDb> ingredientDbs = recipeDb.getIngredients();
        recipeDb.resetIngredients();
        //grabo los ingredientes
        IngredientController ingredientController = new IngredientController();
        ingredientController.saveIngredientsToDatabase(application, ingredientDbs, recipeDb.getKey());

    }

    private void insertOrReplaceRecipeSteps(Application application, RecipeDb recipeDb){
        List<StepDb> stepDbs = recipeDb.getSteps();
        recipeDb.resetSteps();
        //grabo los pasos
        StepController stepController = new StepController();
        stepController.saveStepsToDatabase(application, stepDbs, recipeDb.getKey());

    }

    //  CHANGE PROPERTY FAVORITE
    public RecipeDb switchFavourite(Application application, Long id){
        RecipeDb recipeDb = getRecipeById(application, id);
        recipeDb.setFavourite(!recipeDb.getFavourite());
        return insertOrReplaceRecipe(application, recipeDb);
    }

    public void deleteRecipe(Application application, Long id){
        RecipeDb recipeDb = getRecipeById(application, id);
        if(recipeDb != null){
            recipeDb.delete();
        }
    }


    public void setRecipeForDeleting(Application application, Long id) {
        RecipeDb recipe = getRecipeById(application, id);
        recipe.setUpdatePicture(RecetasCookeoConstants.FLAG_DELETE_PICTURE);
        recipe.setUpdateRecipe(RecetasCookeoConstants.FLAG_DELETE_RECIPE);
        insertOrReplaceRecipe(application, recipe);
    }
}


