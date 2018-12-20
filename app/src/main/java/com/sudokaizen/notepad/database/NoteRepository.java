package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sudokaizen.notepad.AppExecutors;
import com.sudokaizen.notepad.workers.WorkUtils;

import java.util.List;

public class NoteRepository {

    private static NoteRepository instance;
    private AppExecutors mAppExecutors;
    private AppDatabase appDb;
    private DatabaseReference mRootRef;

    public static NoteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoteRepository(context);
        }
        return instance;
    }

    private NoteRepository(Context context) {
        appDb = AppDatabase.getInstance(context);
        FirebaseDatabase remoteDb = FirebaseDatabase.getInstance();
        mRootRef = remoteDb.getReference();
        mAppExecutors = new AppExecutors();
    }

    public NoteEntry getNoteById(final String noteId) {
        return appDb.noteDao().getNoteById(noteId);
    }

    public LiveData<List<NoteEntry>> getAllNotes() {
        return appDb.noteDao().getAllNotes();
    }

    public void insertNote(String userId, NoteEntry noteEntry) {
        String noteId = mRootRef.push().getKey();
        System.out.println("NoteRepo noteId: " + noteId);
        noteEntry.setId(noteId);
        insertNoteOnLocal(noteEntry);
        WorkUtils.scheduleNoteSyncWork(noteEntry, userId);
    }

    private void insertNoteOnLocal(final NoteEntry note) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.noteDao().insertNote(note);
            }
        });
    }

    public void updateNote(String userId, NoteEntry noteEntry) {
        insertNoteOnLocal(noteEntry);
        WorkUtils.scheduleNoteSyncWork(noteEntry, userId);
    }

    public void updateLocalNotes(String userId) {
        mRootRef.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteEntry noteEntry = dataSnapshot.getValue(NoteEntry.class);
                insertNoteOnLocal(noteEntry);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteEntry noteEntry = dataSnapshot.getValue(NoteEntry.class);
                insertNoteOnLocal(noteEntry);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteNote(final NoteEntry note, String userId) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.noteDao().deleteNote(note);
            }
        });
        WorkUtils.scheduleNoteDeleteWork(note, userId);
    }

    public void deleteAllLocalNotes() {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.noteDao().deleteAllNotes();
            }
        });

    }
}