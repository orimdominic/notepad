package com.sudokaizen.notepad.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.AppRepository;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.viewmodel.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mViewModel;
    private RecyclerView rvNotes;
    LinearLayoutManager rvLayoutManager;
    private NotesAdapter mNotesAdapter;
    private AppRepository mAppRepository;

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
                NoteEntry noteToDelete = mNotesAdapter.getItemAt(notePosition);
                System.out.println("Note content is " + noteToDelete.getContent());
                mAppRepository.deleteNote(noteToDelete);
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
                .get(MainActivityViewModel.class);
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


}
