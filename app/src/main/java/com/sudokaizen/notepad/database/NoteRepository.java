package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sudokaizen.notepad.AppExecutors;

import java.util.List;

public class NoteRepository {

    private static NoteRepository instance;
    private AppDatabase appDb;
    private FirebaseDatabase mRemoteDb;
    private DatabaseReference mRootRef;

    public static NoteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoteRepository(context);
        }
        return instance;
    }

    private NoteRepository(Context context) {
        appDb = AppDatabase.getInstance(context);
        mRemoteDb = FirebaseDatabase.getInstance();
        mRootRef = mRemoteDb.getReference();
    }

    public LiveData<List<NoteEntry>> getAllNotes() {
        return appDb.noteDao().getAllNotes();
    }

    public void insertNoteOnLocal(final NoteEntry note) {
        appDb.noteDao().insertNote(note);
    }

    public void deleteNote(final NoteEntry note) {
        appDb.noteDao().deleteNote(note);
    }

    public void deleteAllNotes() {
        appDb.noteDao().deleteAllNotes();
    }

    public NoteEntry getNoteById(final String noteId) {
        return appDb.noteDao().getNoteById(noteId);
    }

    public void updateRemoteNotes(String currentUserId, List<NoteEntry> noteEntries) {
        mRootRef.child(currentUserId).setValue(noteEntries);
    }

    public void updateNote(String userId, NoteEntry noteEntry){
        // TODO: 20-Dec-18 Push this to a work manager
        mRootRef.child(userId).child(noteEntry.getId()).setValue(noteEntry);

        insertNoteOnLocal(noteEntry);

    }

    public void insertNote(String userId, NoteEntry noteEntry){

        // TODO: 20-Dec-18 Push this to a work manager
        String noteId = mRootRef.push().getKey();
        noteEntry.setId(noteId);
        mRootRef.child(userId).child(noteId).setValue(noteEntry);


        insertNoteOnLocal(noteEntry);
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
}