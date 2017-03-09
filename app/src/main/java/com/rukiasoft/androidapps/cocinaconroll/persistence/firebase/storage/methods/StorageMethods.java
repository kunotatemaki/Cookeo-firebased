package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.storage.methods;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.Authentication;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.FirebaseDbMethods;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;

import java.io.File;
import java.util.List;

/**
 * Created by iRoll on 7/2/17.
 */

public class StorageMethods {
    private final String TAG = LogHelper.makeLogTag(FirebaseDbMethods.class);
    private static Boolean uploadinOldPic = false;
    public void uploadOldPicturesToPersonalStorage(List<String> pictureNames){
        if(uploadinOldPic){
            //descargando ya
            return;
        }
        FirebaseUser user = Authentication.getCurrentUser();
        if(user == null || user.isAnonymous()){
            uploadinOldPic = false;
            return;
        }

        if(pictureNames.isEmpty()){
            Logger.d("descargadas todas");
            uploadinOldPic = false;
            return;
        }
        uploadOldPictureToPersonalStorage(pictureNames.get(0));
    }

    public void uploadOldPictureToPersonalStorage(final String name) {

        FirebaseUser user = Authentication.getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("personal/" + user.getUid() + "/" + name);
        final ReadWriteTools rwTools = new ReadWriteTools();
        File sFile = new File(rwTools.getEditedStorageDir() + name);
        if(!sFile.exists()){
            downloadNextPic();
            return;
        }
        Uri file = Uri.fromFile(sFile);

        UploadTask uploadTask = imageRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "ha fallado la subida");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                rwTools.deleteFile(rwTools.getEditedStorageDir() + name);
                downloadNextPic();
            }
        });
    }

    private void updatePictureToPersonalStorage(Context context, final RecipeDb recipe) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null || user.isAnonymous()){
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("personal/" + user.getUid() + "/" + recipe.getPicture());
        final ReadWriteTools rwTools = new ReadWriteTools();

        File sFile = new File(rwTools.getOriginalStorageDir(context) + "/" + recipe.getPicture());
        if(!sFile.exists()){
            return;
        }
        Uri file = Uri.fromFile(sFile);

        UploadTask uploadTask = imageRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "ha fallado la subida");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                rwTools.deleteFile(rwTools.getEditedStorageDir() + recipe.getPathPicture());
            }
        });

    }


}
