package com.sudokaizen.notepad.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.button.MaterialButton;
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
import com.sudokaizen.notepad.AppExecutors;
import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.NoteRepository;
import com.sudokaizen.notepad.database.UserEntity;
import com.sudokaizen.notepad.database.UserRepository;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private AppExecutors mAppExecutors;
    private UserRepository mUserRepository;
    private NoteRepository mNoteRepository;
    private UserEntity formerUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAppExecutors = new AppExecutors();
        mUserRepository = UserRepository.getInstance(this);
        mNoteRepository = NoteRepository.getInstance(this);
        MaterialButton btnViewStory = findViewById(R.id.btn_view_story);
        btnViewStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String MY_STORY_LINK =
                            "https://medium.com/@sudo_kaizen/my-dsc20daysofcode-experience-3d0818733067";
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MY_STORY_LINK));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(SignInActivity.this,
                            "No application can handle this request. Please install a web browser",
                            Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
            }
        });
        initSignInButton();
    }

    private void checkForUser() {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                formerUser = mUserRepository.getUser();
                if (formerUser != null) {
                    System.out.println("The user: " + formerUser.toString());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForUser();
    }

    private void initSignInButton() {
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
            if ((formerUser == null) || (isNotFormerUser(account))) {
                mUserRepository.deleteAllUsers();
                mNoteRepository.deleteAllLocalNotes();
                persistNewUser(account);
            }
            startActivity(new Intent(SignInActivity.this, NotesListActivity.class));
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignInActivity", "signInResult:failed code=" + e.getMessage());
            Toast.makeText(this, "Error! Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNotFormerUser(GoogleSignInAccount account) {
        return !account.getEmail().equals(formerUser.getEmailAddress());
    }

    private void persistNewUser(GoogleSignInAccount account) {
        UserEntity newUser = new UserEntity(account.getEmail(), account.getDisplayName());
        mUserRepository.insertUser(newUser);
    }
}
