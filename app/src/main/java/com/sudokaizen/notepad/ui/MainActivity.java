package com.sudokaizen.notepad.ui;

// Client id  292181038809-l49bdspb7au9n8690ubpmoetn1uvf79e.apps.googleusercontent.com
// Client secret  AuQdzCsklaaswshLSsGwo0tH

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.AppRepository;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.viewmodel.MainViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    private RecyclerView rvNotes;
    LinearLayoutManager rvLayoutManager;
    private NotesAdapter mNotesAdapter;
    private AppRepository mAppRepository;

    public static final String NOTE_ID = "com.sudokaizen.notepad.ui.MainActivity.NOTE_ID_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppRepository = AppRepository.getInstance(MainActivity.this);
        FloatingActionButton fab = findViewById(R.id.fab);
        rvNotes = findViewById(R.id.rv_main_notes);
        rvLayoutManager =
                new LinearLayoutManager(MainActivity.this,
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));
            }
        });

        setupViewModel();
    }

    private void setupViewModel() {

        mViewModel = ViewModelProviders.of(MainActivity.this)
                .get(MainViewModel.class);
        mViewModel.getNotes().observe(MainActivity.this, new Observer<List<NoteEntry>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntry> noteEntries) {
                if (noteEntries.size() == 0) {
                    // TODO: 08-Dec-18 Display empty view for RecyclerView  and hide RecyclerView
                } else {
                    mNotesAdapter = new NotesAdapter(MainActivity.this, noteEntries);
                    mNotesAdapter.notifyDataSetChanged();
                    rvNotes.setAdapter(mNotesAdapter);
                }
            }
        });
    }


    private void showDeleteConfirmationDialog(final int notePosition) {

        String message = "Do you want to delete this note?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFragrances(notePosition);
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

    private void deleteFragrances(int notePosition) {
        NoteEntry noteToDelete = mNotesAdapter.getItemAt(notePosition);
        System.out.println("Note content is " + noteToDelete.getContent());
        mAppRepository.deleteNote(noteToDelete);
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
    }

}
