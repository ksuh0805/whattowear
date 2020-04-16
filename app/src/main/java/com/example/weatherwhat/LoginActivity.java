package com.example.weatherwhat;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "STARTLOGIN");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.signInButton);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
/*
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();

        }
*/
    }

    private void signIn() {
        Log.d("TAG", "STARTLOGIN2");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d("TAG", "STARTLOGIN3");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG","END5");
        Log.d("TAG",String.valueOf(requestCode));

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("TAG","rc");
            try {
                Log.d("TAG","try");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG","END2");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d("TAG","catch");
            }
        }else{
            Log.d("TAG","END");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Snackbar.make(findViewById(R.id.layout_login), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.layout_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Log.d("TAG","END3");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
