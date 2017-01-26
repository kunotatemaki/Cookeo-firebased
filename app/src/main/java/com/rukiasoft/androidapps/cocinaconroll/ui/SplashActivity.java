package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;

import icepick.State;


public class SplashActivity extends AppCompatActivity {

    @State boolean started = false;
    //request constants
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    launchMainScreen();
                } else {
                    launchSignInScreen();
                }
            }
        };

        //start animation if needed
        if(!started){
            launchAnimation();
        }else {
            launchMainOrSigningScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RecetasCookeoConstants.REQUEST_CODE_ANIMATION:
                launchMainOrSigningScreen();
                break;
            case RecetasCookeoConstants.REQUEST_CODE_SIGNING_FROM_SPLASH:
                launchMainScreen();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void launchMainOrSigningScreen(){
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void launchAnimation(){
        Intent animationIntent = new Intent(this, AnimationActivity.class);
        startActivityForResult(animationIntent, RecetasCookeoConstants.REQUEST_CODE_ANIMATION);
    }

    private void launchMainScreen(){
        Intent intent = new Intent(this, RecipeListActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchSignInScreen(){
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        startActivityForResult(intent, RecetasCookeoConstants.REQUEST_CODE_SIGNING_FROM_SPLASH);
    }
}