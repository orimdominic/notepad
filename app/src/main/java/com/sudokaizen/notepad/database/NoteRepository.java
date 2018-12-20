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
    private AppExecutors mAppExecutors;
    private FirebaseDatabase mRemoteDb;
    private DatabaseReference mRootRef;
    private Context mContext;

    public static NoteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoteRepository(context);
        }
        return instance;
    }

    private NoteRepository(Context context) {
        appDb = AppDatabase.getInstance(context);
        mAppExecutors = new AppExecutors();
        mRemoteDb = FirebaseDatabase.getInstance();
        mRootRef = mRemoteDb.getReference();
        mContext = context;
    }

    public LiveData<List<NoteEntry>> getAllNotes() {
        return appDb.noteDao().getAllNotes();
    }

    public void insertNote(final NoteEntry note) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.noteDao().insertNote(note);
            }
        });
    }

    public void deleteNote(final NoteEntry note) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.noteDao().deleteNote(note);
            }
        });
    }

    public void deleteAllNotes() {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.noteDao().deleteAllNotes();
            }
        });
    }

    public NoteEntry getNoteById(final int noteId) {
        return appDb.noteDao().getNoteById(noteId);
    }

    public void updateRemoteNotes(String currentUserId, List<NoteEntry> noteEntries) {
        mRootRef.child(currentUserId).setValue(noteEntries);
    }

    public void updateLocalNotes(String userId) {
        mRootRef.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteEntry noteEntry = dataSnapshot.getValue(NoteEntry.class);
                insertNote(noteEntry);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteEntry noteEntry = dataSnapshot.getValue(NoteEntry.class);
                insertNote(noteEntry);
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