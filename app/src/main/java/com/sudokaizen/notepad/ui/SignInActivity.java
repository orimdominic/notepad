package com.sudokaizen.notepad.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.NoteRepository;
import com.sudokaizen.notepad.database.UserEntity;
import com.sudokaizen.notepad.database.UserRepository;
import com.sudokaizen.notepad.viewmodel.CreateNoteViewModel;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private UserRepository mUserRepository;
    private NoteRepository mNoteRepository;
    private CreateNoteViewModel mCreateNoteViewModel;
    private UserEntity formerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mUserRepository = UserRepository.getInstance(this);
        mNoteRepository = NoteRepository.getInstance(this);
        System.out.println("Check created");

        mCreateNoteViewModel = ViewModelProviders.of(this).get(CreateNoteViewModel.class);
        mCreateNoteViewModel.mLiveUser.observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(@Nullable UserEntity userEntity) {
                formerUser = userEntity;
            }
        });


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Check signInButton clicked");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Check onActivityResult running");
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            System.out.println("Check onActivityResult requestCode == RC_SIGN_IN");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            System.out.println("account "+account.getEmail());
            UserEntity currentUser = new UserEntity(account.getEmail(), account.getDisplayName());

            if (formerUser == null){
                mUserRepository.deleteAllUsers();
                mUserRepository.insertUser(currentUser);
            }else if (!formerUser.getId().equals(currentUser.getId())){
                // diff person
                mUserRepository.deleteAllUsers();
                mNoteRepository.deleteAllNotes();
                mUserRepository.insertUser(currentUser);
            }

            System.out.println("Check onActivityResult after if");
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();

            } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignInActivity", "signInResult:failed code=" + e.getMessage());

                Toast.makeText(this, "Error! Sign in failed", Toast.LENGTH_SHORT)
                        .show();

        }
    }

}
