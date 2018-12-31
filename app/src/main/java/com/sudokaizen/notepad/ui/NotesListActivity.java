package com.sudokaizen.notepad.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sudokaizen.notepad.AppExecutors;
import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.NoteRepository;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.database.UserEntity;
import com.sudokaizen.notepad.database.UserRepository;
import com.sudokaizen.notepad.viewmodel.MainViewModel;

import java.util.List;

public class NotesListActivity extends AppCompatActivity {

    private RecyclerView rvNotes;
    private View rvEmptyView;
    private NotesAdapter mNotesAdapter;
    private NoteRepository mNoteRepository;
    private UserRepository mUserRepository;
    private UserEntity currentUser;

    public static final String NOTE_ID = "com.sudokaizen.notepad.ui.NotesListActivity.NOTE_ID_KEY";
    private AppExecutors mAppExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            startActivity(new Intent(NotesListActivity.this, SignInActivity.class));
            finish();
        }

        setContentView(R.layout.activity_notes_list);

        Toolbar toolbar = findViewById(R.id.toolbar_notes_list);
        toolbar.setTitle(R.string.notes_list_activity_label);
        setSupportActionBar(toolbar);

        mAppExecutors = new AppExecutors();
        mNoteRepository = NoteRepository.getInstance(NotesListActivity.this);
        mUserRepository = UserRepository.getInstance(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        rvNotes = findViewById(R.id.rv_main_notes);
        rvEmptyView = findViewById(R.id.rv_empty_view);
        LinearLayoutManager rvLayoutManager = new LinearLayoutManager(NotesListActivity.this,
                LinearLayoutManager.VERTICAL, false);
        rvNotes.setLayoutManager(rvLayoutManager);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int notePosition = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(notePosition);
            }

        }).attachToRecyclerView(rvNotes);
        rvNotes.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotesListActivity.this, CreateNoteActivity.class));
            }
        });

        checkForUser();

        setupViewModel();
    }

    private void setupViewModel() {

        MainViewModel viewModel = ViewModelProviders.of(NotesListActivity.this)
                .get(MainViewModel.class);
        viewModel.getNotes().observe(NotesListActivity.this, new Observer<List<NoteEntry>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntry> noteEntries) {
                if (noteEntries.size() == 0) {
                    mNoteRepository.updateLocalNotes(currentUser.getId());
                    rvEmptyView.setVisibility(View.VISIBLE);
                    rvNotes.setVisibility(View.GONE);
                } else {
                    mNotesAdapter = new NotesAdapter(NotesListActivity.this, noteEntries);
                    mNotesAdapter.notifyDataSetChanged();
                    rvEmptyView.setVisibility(View.GONE);
                    rvNotes.setVisibility(View.VISIBLE);
                    rvNotes.setAdapter(mNotesAdapter);
                }
            }
        });
    }

    private void checkForUser() {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                currentUser = mUserRepository.getUser();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForUser();
    }

    private void showDeleteConfirmationDialog(final int notePosition) {

        String message = "Do you want to delete this note?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNote(notePosition);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mNotesAdapter.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteNote(int notePosition) {
        NoteEntry noteToDelete = mNotesAdapter.getItemAt(notePosition);
        mNoteRepository.deleteNote(noteToDelete, currentUser.getId());
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        GoogleSignInClient mGoogleSignInClient = getUser(this);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(NotesListActivity.this, SignInActivity.class));
                        finish();
                    }
                });
    }

    private GoogleSignInClient getUser(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }

}
