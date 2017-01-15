package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;


public class SplashActivity extends AppCompatActivity {

    private boolean started = false;
    private static final String KEY_STARTED = RecetasCookeoConstants.PACKAGE_NAME + ".started";
    //request constants
    private static final int REQUEST_CODE_ANIMATION = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_STARTED)) {
                started = savedInstanceState.getBoolean(KEY_STARTED);
            }
        }

        //start animation if needed
        if(!started){
            launchAnimation();
        }else {
            launchMainScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ANIMATION:
                launchMainScreen();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putBoolean(KEY_STARTED, true);
    }

    private void launchMainScreen(){
        Intent intent = new Intent(this, RecipeListActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchAnimation(){
        Intent animationIntent = new Intent(this, AnimationActivity.class);
        startActivityForResult(animationIntent, REQUEST_CODE_ANIMATION);
    }
}