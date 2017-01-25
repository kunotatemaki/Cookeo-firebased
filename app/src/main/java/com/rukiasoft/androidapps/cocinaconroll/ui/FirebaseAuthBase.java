package com.rukiasoft.androidapps.cocinaconroll.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.gcm.QuickstartPreferences;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

public class FirebaseAuthBase extends ToolbarAndProgressActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "FirebaseAuthBase";
    protected static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeConnection();
    }

    protected void initializeConnection(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    // [START handleSignInResult]
    private void handleSignInResult(FirebaseUser user) {
        Tools mTools = new Tools();
        if (user != null) {
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_NAME, user.getDisplayName());
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_EMAIL, user.getEmail());
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_FIREBASE_ID, user.getUid());
            //force to send registration token to server again, with this new information
            mTools.savePreferences(this, QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            try {
                Log.d(TAG, user.getUid());
                Log.d(TAG, user.getDisplayName());
                Log.d(TAG, user.getEmail());

            }catch(Exception e){
                Log.d(TAG, "no hay user");
            }
        } else {
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_NAME, "");
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_EMAIL, "");
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_FIREBASE_ID, "");
        }
    }

    // [START signIn]
    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void signInAnnonimously(){
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FirebaseAuthBase.this, getString(R.string.signed_in_err),
                                    Toast.LENGTH_SHORT).show();
                            revokeAccess();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            handleSignInResult(user);
                            finish();
                        }
                    }
                });
    }


    protected void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog(getString(R.string.signing_in));
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FirebaseAuthBase.this, getString(R.string.signed_in_err),
                                    Toast.LENGTH_SHORT).show();
                            revokeAccess();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            handleSignInResult(user);
                            finish();
                        }

                    }
                });
    }

    protected void revokeAccess() {

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Tools mTools = new Tools();
                        mTools.savePreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_EMAIL, "");
                        mTools.savePreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_NAME, "");
                        mTools.savePreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_AVOID_GOOGLE_SIGN_IN, true);
                    }
                });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
