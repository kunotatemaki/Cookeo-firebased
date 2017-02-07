package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.storage.methods;

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
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItem;
import com.rukiasoft.androidapps.cocinaconroll.persistence.firebase.database.methods.DatabaseMethods;
import com.rukiasoft.androidapps.cocinaconroll.utilities.LogHelper;
import com.rukiasoft.androidapps.cocinaconroll.utilities.ReadWriteTools;

import java.io.File;

/**
 * Created by iRoll on 7/2/17.
 */

public class StorageMethods {
    private final String TAG = LogHelper.makeLogTag(DatabaseMethods.class);

    public void updatePictureToPersonalStorage(final RecipeItem recipe) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null || user.isAnonymous()){
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("personal/" + user.getUid() + "/" + recipe.getPicture());
        final ReadWriteTools rwTools = new ReadWriteTools();

        File sFile = new File(rwTools.getEditedStorageDir() + recipe.getPicture());
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
                // TODO: 8/2/17 eliminar la foto cuando compruebe que todo funciona ok
                //rwTools.deleteFile(rwTools.getEditedStorageDir() + recipe.getPathPicture());
            }
        });

    }
}
