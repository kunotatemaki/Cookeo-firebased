package com.rukiasoft.androidapps.cocinaconroll.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

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
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.RecipeFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.model.TimestampFirebase;
import com.rukiasoft.androidapps.cocinaconroll.persistence.local.ObjectQeue;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AlarmService extends IntentService {
    private static final String ACTION_DOWNLOAD_RECIPES = "com.rukiasoft.androidapps.cocinaconroll.alarm.action.ACTION_DOWNLOAD_RECIPES";

    private static Boolean mDownloading = false;
    private static final String EXTRA_SIGNED = "com.rukiasoft.androidapps.cocinaconroll.alarm.extra.EXTRA_SIGNED";
    private ObjectQeue pullPictures;
    private Boolean isDownloadingPics = false;
    private int contador;

    public AlarmService() {
        super("AlarmService");
    }


    public static void startActionDownloadRecipes(Context context, Boolean signed) {
        if(mDownloading){
            Logger.d("Ya estaba descargando");
            return;
        }
        mDownloading = true;
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_DOWNLOAD_RECIPES);
        intent.putExtra(EXTRA_SIGNED, signed);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_RECIPES.equals(action)) {
                final Boolean signed = intent.getBooleanExtra(EXTRA_SIGNED, false);
                handleActionDownloadRecipes(signed);
            }
        }
    }

    private void handleActionDownloadRecipes(Boolean signed) {
        connectToFirebaseForNewRecipes(RecetasCookeoConstants.ALLOWED_RECIPES_NODE);
        connectToFirebaseForNewRecipes(RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE);
        if(signed){
            connectToFirebaseForNewRecipes(RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE);
        }
    }

    private void connectToFirebaseForNewRecipes(String node){
        if(node.equals(RecetasCookeoConstants.PERSONAL_RECIPES_NODE)){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                node += "/" + user.getUid();
        }
        DatabaseReference mRecipeTimestamps = FirebaseDatabase.getInstance().getReference(node +
                "/" + RecetasCookeoConstants.TIMESTAMP_RECIPES_NODE);
        ValueEventListener timestampListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                downloadTimestaps(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.d("no descarga los timestamps");
            }
        };
        mRecipeTimestamps.addListenerForSingleValueEvent(timestampListener);
    }

    private void downloadTimestaps(DataSnapshot dataSnapshot){
        Integer recipeOwner = FirebaseDbMethods.getRecipeFlagFromNodeName(dataSnapshot.getRef().getParent().getKey());
        RecipeController recipeController = new RecipeController();
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            TimestampFirebase timestampFirebase = postSnapshot.getValue(TimestampFirebase.class);
            String key = postSnapshot.getKey();
            if(getApplication()==null){
                continue;
            }
            RecipeDb recipeDbFromDatabase = recipeController.getRecipeByKey(getApplication(), key);
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
                recipeController.insertOrReplaceRecipe(getApplication(), recipeDbFromDatabase);
            }
        }
        downloadRecipesFromFirebase();
    }


    public void downloadRecipesFromFirebase(){
        contador = 0;
        //Veo si hay que descargar recetas
        pullPictures = ObjectQeue.create(new ArrayList<String>());
        RecipeController recipeController = new RecipeController();
        List<RecipeDb> recipes = recipeController.getListBothRecipeAndPicturesToDownload(getApplication());
        if(recipes == null || recipes.isEmpty()){
            return;
        }
        List<RecipeDb> onlyRecipes = recipeController.getListOnlyRecipeToDownload(getApplication());
        //inicio el timer
        if(!onlyRecipes.isEmpty()) {
            // TODO: 22/2/17 mandar un broadcast para sacar un mensaje de leyendo recetas???

        }
        //Descargo las recetas de Firebase
        for(RecipeDb recipe : recipes){
            if(recipe.getDownloadRecipe()) {
                String node = FirebaseDbMethods.getNodeNameFromRecipeFlag(recipe.getOwner());

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
                                "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + recipe.getKey());
                mRecipeRefDetailed.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        downloadRecipe(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else if(recipe.getDownloadPicture()){
                //añado la foto al pull de descargas y si no ha empezado a descargar, empiezo
                pullPictures.add(recipe.getPicture());
                if(!isDownloadingPics) {
                    isDownloadingPics = !isDownloadingPics;
                    downloadPictureFromStorage();
                }
            }
        }
    }

    private void downloadRecipe(DataSnapshot dataSnapshot){

        RecipeController recipeController = new RecipeController();
        RecipeFirebase recipeFromFirebase = dataSnapshot.getValue(RecipeFirebase.class);
        if(recipeFromFirebase == null){
            return;
        }
        //String key = dataSnapshot.getRef().getParent().getParent().getKey();
        if(getApplication() == null){
            return;
        }
        RecipeDb recipeDb = recipeController.insertRecipeFromFirebase(getApplication(),
                dataSnapshot, recipeFromFirebase);
        if(recipeDb == null){
            return;
        }
        contador ++;
        sendSignal(contador);
        if(recipeDb.getDownloadPicture()){
            pullPictures.add(recipeDb.getPicture());
            if(!isDownloadingPics){
                isDownloadingPics = !isDownloadingPics;
                downloadPictureFromStorage();
            }

        }
    }

    private void downloadPictureFromStorage(){
        if(pullPictures.isEmpty()){
            isDownloadingPics = !isDownloadingPics;
            return;
        }
        final String name = pullPictures.get(0);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("recipes/" + name);
        final File imageFile;
        ReadWriteTools rwTools = new ReadWriteTools();
        String path = rwTools.getOriginalStorageDir(getApplicationContext());
        imageFile = new File(path + name);

        imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                //Log.d(TAG, "Salvado correctamente: " + name);
                //quito del pull y sigo descargando
                if(getApplication() == null){
                    //hago que lo intente otra vez hasta que sea context!=null
                    downloadPictureFromStorage();
                }
                pullPictures.remove(name);
                RecipeController recipeController = new RecipeController();
                recipeController.updateDownloadRecipeFlag(getApplication(), name, false);
                contador ++;
                sendSignal(contador);
                downloadPictureFromStorage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Si ha fallado, borro el temporal y continuo
                Logger.d("No existe la foto" + name);
                if(imageFile.exists()) {
                    imageFile.delete();
                }
                pullPictures.remove(name);
                downloadPictureFromStorage();
            }
        });

    }

    private void sendSignal(int contador){
        if(contador % 10 != 0){
            return;
        }
        Logger.d("Envio contador " + contador);
        Intent intent = new Intent(RecetasCookeoConstants.NAME_DOWNLOAD_INTENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
