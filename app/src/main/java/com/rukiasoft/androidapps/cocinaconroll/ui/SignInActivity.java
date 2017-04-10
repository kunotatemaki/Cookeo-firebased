/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rukiasoft.androidapps.cocinaconroll.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.utilities.RecetasCookeoConstants;
import com.rukiasoft.androidapps.cocinaconroll.utilities.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SignInActivity extends ToolbarAndProgressActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    /**
     * Google API client.
     */

    /* View to display current status (signed-in, signed-out, disconnected, etc) */
    @BindView(R.id.sign_in_status) TextView mStatus;
    @BindView(R.id.sign_in_button)SignInButton signInButton;
    @BindView(R.id.sign_in_anonymous_button)Button anonymousButton;

    private GoogleApiClient mGoogleApiClient;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);
        unbinder = ButterKnife.bind(this);

        initializeConnection();

        // Set up button click listeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        anonymousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAnnonimously();
            }
        });

        // Large sign-in
        signInButton.setSize(SignInButton.SIZE_WIDE);

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

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        Tools tools = new Tools();
        if (requestCode == RecetasCookeoConstants.REQUEST_CODE_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                hideProgressDialog();
                tools.savePreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_SIGNED_IN, false);
                Toast.makeText(getApplicationContext(), getString(R.string.signed_in_err), Toast.LENGTH_LONG);
                revokeAccess();
                enableButtons();
            }
        }
    }

    // [START signIn]
    protected void signIn() {
        disableButtons();
        showProgressDialog(getString(R.string.signing_in));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RecetasCookeoConstants.REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    protected void signInAnnonimously(){
        //Quito el acceso por si lo tenía
        disableButtons();
        showProgressDialog(getString(R.string.signing_in));
        revokeAccess();
        //Autentico anónimamente
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Logger.d("signInWithCredential:onComplete:" + task.isSuccessful());
                        //Para bien o para mal, pulsando aquí no está signed in (estará en fallo o anónimo)
                        Tools tools = new Tools();
                        tools.savePreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_SIGNED_IN, false);

                        hideProgressDialog();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Logger.w("signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, getString(R.string.signed_in_err),
                                    Toast.LENGTH_SHORT).show();
                            revokeAccess();
                            enableButtons();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            handleSignInResult(user);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        Logger.d("no hace nada en anónimo");
                    }
                });
    }


    protected void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Logger.d( "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog(getString(R.string.signing_in));
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Logger.d( "signInWithCredential:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        Tools tools = new Tools();
                        if (!task.isSuccessful()) {
                            Logger.w("signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, getString(R.string.signed_in_err),
                                    Toast.LENGTH_SHORT).show();
                            revokeAccess();
                            tools.savePreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_SIGNED_IN, false);
                            enableButtons();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            handleSignInResult(user);
                            tools.savePreferences(getApplicationContext(), RecetasCookeoConstants.PROPERTY_SIGNED_IN, true);
                            finish();
                        }

                    }
                });
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleSignInResult(FirebaseUser user) {
        Tools mTools = new Tools();
        if (user != null && !user.isAnonymous()) {
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_NAME, user.getDisplayName());
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_EMAIL, user.getEmail());
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_FIREBASE_ID, user.getUid());
        } else {
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_NAME, "");
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_DEVICE_OWNER_EMAIL, "");
            mTools.savePreferences(this, RecetasCookeoConstants.PROPERTY_FIREBASE_ID, "");
        }
    }


    protected void revokeAccess() {

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google revoke access
        try {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
        }catch(IllegalStateException e){
            Logger.e(e.getMessage());
        }
    }

    private void disableButtons(){
        if(signInButton != null){
            signInButton.setEnabled(false);
        }
        if(anonymousButton != null){
            anonymousButton.setEnabled(false);
        }
    }

    private void enableButtons(){
        if(signInButton != null){
            signInButton.setEnabled(true);
        }
        if(anonymousButton != null){
            anonymousButton.setEnabled(true);
        }
    }

}
