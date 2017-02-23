package com.rukiasoft.androidapps.cocinaconroll.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
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
    private static final String ACTION_DOWNLOAD_TIMESTAMPS = "com.rukiasoft.androidapps.cocinaconroll.alarm.action.ACTION_DOWNLOAD_TIMESTAMPS";
    private static final String ACTION_DOWNLOAD_RECIPES = "com.rukiasoft.androidapps.cocinaconroll.alarm.action.ACTION_DOWNLOAD_RECIPES";

    private static final String EXTRA_SIGNED = "com.rukiasoft.androidapps.cocinaconroll.alarm.extra.EXTRA_SIGNED";
    private static final int MAX_ITEMS_IN_QUEUE = 50;
    //private ObjectQeue pullObjects;
    private static Boolean isDownloadingPics = false;
    private static Boolean isDownloadingRecipes = false;
    private int contadorRecipesDownloaded;
    private static int numberPendingRecipes;
    private static int numberPendingPictures;
    private static List<RecipeDb> recipes;
    private static List<RecipeDb> pictures;

    public AlarmService() {
        super("AlarmService");
    }


    public static void startActionDownloadTimestamps(Context context, Boolean signed) {
        if(isDownloading()){
            Logger.d("Ya estaba descargando");
            return;
        }
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_DOWNLOAD_TIMESTAMPS);
        intent.putExtra(EXTRA_SIGNED, signed);
        context.startService(intent);
    }

    public static void startActionDownloadRecipes(Context context) {

        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_DOWNLOAD_RECIPES);
        context.startService(intent);
    }

    private static Boolean isDownloading(){
        return isDownloadingPics | isDownloadingRecipes;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_TIMESTAMPS.equals(action)) {
                final Boolean signed = intent.getBooleanExtra(EXTRA_SIGNED, false);
                handleActionDownloadTimestamps(signed);
            }else if (ACTION_DOWNLOAD_RECIPES.equals(action)) {
                handleActionDownloadRecipes();
            }
        }
    }

    private void handleActionDownloadTimestamps(Boolean signed) {
        Logger.d("estoy en el serviceintent");
        connectToFirebaseForNewRecipes(RecetasCookeoConstants.ALLOWED_RECIPES_NODE);
        connectToFirebaseForNewRecipes(RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE);
        if(signed){
            connectToFirebaseForNewRecipes(RecetasCookeoConstants.FORBIDDEN_RECIPES_NODE);
        }
    }

    private void handleActionDownloadRecipes() {
        downloadRecipesFromFirebase();
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
                DownloadTimestampsTask downloadTimestampsTask = new DownloadTimestampsTask();
                downloadTimestampsTask.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.d("no descarga los timestamps");
            }
        };
        mRecipeTimestamps.addListenerForSingleValueEvent(timestampListener);
    }

    /**
     * Tarea que comprueba los timestamps descargados
     */
    private class DownloadTimestampsTask extends AsyncTask<DataSnapshot, Integer, Void>{

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot dataSnapshot = dataSnapshots[0];
            RecipeController recipeController = new RecipeController();
            Integer recipeOwner = FirebaseDbMethods.getRecipeFlagFromNodeName(dataSnapshot.getRef().getParent().getKey());
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                TimestampFirebase timestampFirebase = postSnapshot.getValue(TimestampFirebase.class);
                String key = postSnapshot.getKey();
                if(getContext()==null){
                    continue;
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
            AlarmService.startActionDownloadRecipes(getApplicationContext());
        }
    }


    public void downloadRecipesFromFirebase(){
        Logger.d("mando descargar desde el intent");
        //reseteo los contadores
        contadorRecipesDownloaded = 0;
        numberPendingRecipes = 0;
        numberPendingPictures = 0;
        //Veo si hay que descargar recetas
        RecipeController recipeController = new RecipeController();
        recipes = recipeController.getListOnlyRecipeToDownload(getApplication());
        pictures = recipeController.getListOnlyPicturesToDownload(getApplication());
        if((recipes == null || recipes.isEmpty()) && (pictures == null || pictures.isEmpty())){
            return;
        }

        //List<RecipeDb> onlyRecipes = recipeController.getListOnlyRecipeToDownload(getApplication());
        /*if(!onlyRecipes.isEmpty()) {
            // TODO: 22/2/17 mandar un broadcast para sacar un mensaje de leyendo recetas???

        }*/
        isDownloadingPics = true;
        isDownloadingRecipes = true;
        downloadRecipesSequentialy();
        downloadPictureFromStorageSequentially();
    }

    private void downloadRecipesSequentialy(){
        //Descargo las recetas de Firebase
        if(recipes.isEmpty()){
            isDownloadingRecipes = !isDownloadingRecipes;
            return;
        }
        for(RecipeDb recipe : recipes) {

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
            while (numberPendingRecipes > MAX_ITEMS_IN_QUEUE) {
                Logger.d("Espero porque la cola es de: " + numberPendingRecipes);
                SystemClock.sleep(5000);
            }
            numberPendingRecipes++;
            DatabaseReference mRecipeRefDetailed = FirebaseDatabase.getInstance()
                    .getReference(node +
                            "/" + RecetasCookeoConstants.DETAILED_RECIPES_NODE + "/" + recipe.getKey());
            mRecipeRefDetailed.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    numberPendingRecipes--;
                    DownloadRecipeTask downloadTask = new DownloadRecipeTask();
                    downloadTask.execute(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    numberPendingRecipes--;
                }
            });
        }
    }

    //ASYNCTASKS
    /**
     * Tarea que almacena las recetas descargadas
     */
    private class DownloadRecipeTask extends AsyncTask<DataSnapshot, Integer, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... snapshot) {
            Logger.d("descargando receta");
            RecipeController recipeController = new RecipeController();
            DataSnapshot dataSnapshot = snapshot[0];
            RecipeFirebase recipeFromFirebase = dataSnapshot.getValue(RecipeFirebase.class);
            if(recipeFromFirebase == null)  return null;
            //String key = dataSnapshot.getRef().getParent().getParent().getKey();
            if(getApplication() == null){
                return null;
            }
            RecipeDb recipeDb = recipeController.insertRecipeFromFirebase(getApplication(),
                    dataSnapshot, recipeFromFirebase);
            contadorRecipesDownloaded++;
            sendSignal(contadorRecipesDownloaded);
            return null;
        }

        @Override
        protected void onPostExecute() {

        }
    }


    private void downloadPictureFromStorageSequentially(){
        Logger.d("descargando imagen");
        if(pictures.isEmpty()){
            isDownloadingPics = !isDownloadingPics;
            return;
        }
        for(RecipeDb picture : pictures) {
            final String name = picture.getPicture();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("recipes/" + name);
            while (numberPendingPictures > MAX_ITEMS_IN_QUEUE) {
                Logger.d("Espero porque la cola de fotos es de: " + numberPendingPictures);
                SystemClock.sleep(5000);
            }
            final File imageFile;
            ReadWriteTools rwTools = new ReadWriteTools();
            String path = rwTools.getOriginalStorageDir(getApplicationContext());
            imageFile = new File(path + name);
            numberPendingPictures++;
            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    //Log.d(TAG, "Salvado correctamente: " + name);
                    //quito del pull y sigo descargando
                    numberPendingPictures--;
                    RecipeController recipeController = new RecipeController();
                    recipeController.updateDownloadRecipeFlag(getApplication(), name, false);
                    contadorRecipesDownloaded++;
                    sendSignal(contadorRecipesDownloaded);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Si ha fallado, borro el temporal y continuo
                    Logger.d("No existe la foto" + name);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                    numberPendingRecipes--;
                }
            });
        }
    }

    private void sendSignal(int contador){
        if(contador % 10 != 0){
            return;
        }
        Logger.d("Envio contadorRecipesDownloaded " + contador);
        Intent intent = new Intent(RecetasCookeoConstants.NAME_DOWNLOAD_INTENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
