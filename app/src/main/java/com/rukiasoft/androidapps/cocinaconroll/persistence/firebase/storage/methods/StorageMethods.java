package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.storage.methods;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.persistence.controllers.RecipeController;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.Authentication;
import com.rukiasoft.androidapps.cocinaconroll.persistence.model.RecipeDb;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import java.io.File;
import java.util.List;

/**
 * Created by iRoll on 7/2/17.
 */

public class StorageMethods {
    private static Boolean uploadingOldPics = false;
    private static Boolean uploadingPics = false;
    private static List<String> mOldPics;
    private static List<RecipeDb> mPics;

    public void uploadOldPicturesToPersonalStorage(List<String> pictureNames){
        if(uploadingOldPics){
            //descargando ya
            return;
        }
        FirebaseUser user = Authentication.getCurrentUser();
        if(user == null || user.isAnonymous()){
            uploadingOldPics = false;
            return;
        }


        mOldPics = pictureNames;
        if(mOldPics.size()>0){
            uploadOldPictureToPersonalStorage(mOldPics.get(0));
        }
    }

    private void uploadOldPictureToPersonalStorage(final String name) {

        FirebaseUser user = Authentication.getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(RecetasCookeoConstants.STORAGE_PERSONAL_NODE + "/" +
                user.getUid() + "/" + name);
        final ReadWriteTools rwTools = new ReadWriteTools();
        File sFile = new File(rwTools.getEditedStorageDir() + name);
        if(!sFile.exists()){
            uploadNextOldPic();
            return;
        }
        Uri file = Uri.fromFile(sFile);

        UploadTask uploadTask = imageRef.putFile(file);

        //Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Logger.d("ha fallado la subida");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                rwTools.deleteFile(rwTools.getEditedStorageDir() + name);
                uploadNextOldPic();
            }
        });
    }

    private void uploadNextOldPic(){
        if(mOldPics != null  && !mOldPics.isEmpty()) {
            mOldPics.remove(0);
        }
        if(mOldPics == null || mOldPics.isEmpty()){
            Logger.d("descargadas todas");
            uploadingOldPics = false;
            return;
        }
        uploadOldPictureToPersonalStorage(mOldPics.get(0));
    }

    public void uploadPendingPicturesToPersonalStorage(Context context) {

        if (uploadingPics) {
            //descargando ya
            return;
        }
        FirebaseUser user = Authentication.getCurrentUser();
        if (user == null || user.isAnonymous()) {
            return;
        }

        RecipeController recipeController = new RecipeController();
        mPics = recipeController.getListOnlyPicturesToUpdate((Application) context.getApplicationContext(),
                RecetasCookeoConstants.FLAG_UPLOAD_PICTURE);

        uploadNextPic(context);
    }

    private void uploadPictureToPersonalStorage(final Context context, final RecipeDb recipe){
        FirebaseUser user = Authentication.getCurrentUser();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(RecetasCookeoConstants.STORAGE_PERSONAL_NODE + "/" +
                user.getUid() + "/" + mPics.get(0).getPicture());
        final ReadWriteTools rwTools = new ReadWriteTools();

        File sFile = new File(rwTools.getOriginalStorageDir(context) + "/" + recipe.getPicture());
        if(!sFile.exists()){
            return;
        }
        Uri file = Uri.fromFile(sFile);

        UploadTask uploadTask = imageRef.putFile(file);

        //Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Logger.d("ha fallado la subida");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                RecipeController recipeController = new RecipeController();
                recipeController.updateDownloadPictureFlag((Application)context.getApplicationContext(),
                        recipe.getId(), RecetasCookeoConstants.FLAG_NOT_UPDATE_PICTURE);
                uploadNextPic(context);
            }
        });

    }

    private void uploadNextPic(Context context){
        if(mPics == null || mPics.isEmpty()){
            Logger.d("subidas todas");
            uploadingPics = false;
            return;
        }
        uploadPictureToPersonalStorage(context, mPics.get(0));
    }


    public void deletePicture(String pictureName){
        FirebaseUser user = Authentication.getCurrentUser();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(RecetasCookeoConstants.STORAGE_PERSONAL_NODE + "/" +
                user.getUid() + "/" + pictureName);

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.d("borrado");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Logger.d("No borrado");
            }
        });

    }

    public void sharePic(final Context context, String pictureName) {
        FirebaseUser user = Authentication.getCurrentUser();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(RecetasCookeoConstants.STORAGE_PENDING_NODE + "/" +
                user.getUid() + "/" + pictureName);
        final ReadWriteTools rwTools = new ReadWriteTools();

        File sFile = new File(rwTools.getOriginalStorageDir(context) + "/" + pictureName);
        if(!sFile.exists()){
            return;
        }
        Uri file = Uri.fromFile(sFile);

        UploadTask uploadTask = imageRef.putFile(file);

        //Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Logger.d("ha fallado la subida");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

            }
        });
    }
}
