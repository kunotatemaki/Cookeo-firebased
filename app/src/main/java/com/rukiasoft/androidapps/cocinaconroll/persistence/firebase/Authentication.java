package com.rukiasoft.androidapps.cocinaconroll.persistence.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ruler on 09/03/2017.
 */

public class Authentication {

    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
