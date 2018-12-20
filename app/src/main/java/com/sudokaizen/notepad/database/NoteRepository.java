package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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
import com.sudokaizen.notepad.SaveNoteWorker;
import com.sudokaizen.notepad.ui.MainActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class NoteRepository {

    private static NoteRepository instance;
    private AppExecutors mAppExecutors;
    private AppDatabase appDb;
    private Context mContext;
    private DatabaseReference mRootRef;
    public static final String NOTE_CONTENT_KEY = "NOTE_CONTENT_KEY";
    public static final String NOTE_ID_KEY = "NOTE_ID_KEY";
    public static final String TIMESTAMP_KEY = "TIMESTAMP_KEY";
    public static final String USER_ID_KEY = "USER_ID_KEY";


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
        mContext = context;
    }

    public LiveData<List<NoteEntry>> getAllNotes() {
        return appDb.noteDao().getAllNotes();
    }

    private void insertNoteOnLocal(final NoteEntry note) {
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

    public NoteEntry getNoteById(final String noteId) {
        return appDb.noteDao().getNoteById(noteId);
    }

    public void updateNote(String userId, NoteEntry noteEntry) {
        insertNoteOnLocal(noteEntry);
        setSaveNoteWork(userId, noteEntry, noteEntry.getId());
    }

    public void insertNote(String userId, NoteEntry noteEntry) {
        String noteId = mRootRef.push().getKey();
        System.out.println("NoteRepo noteId: " + noteId);
        noteEntry.setId(noteId);
        insertNoteOnLocal(noteEntry);
        setSaveNoteWork(userId, noteEntry, noteId);
    }

    private void setSaveNoteWork(String userId, NoteEntry noteEntry, String noteId) {
        String[] strings = {noteId, noteEntry.getContent(), userId};
        long timeStamp = noteEntry.getTimestamp();

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType
                (NetworkType.CONNECTED).build();

        PeriodicWorkRequest saveNoteWork = new PeriodicWorkRequest.Builder(SaveNoteWorker
                .class, 5, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(createInputDataSaveNoteWorker(strings, timeStamp))
                .build();
        WorkManager.getInstance().enqueue(saveNoteWork);
    }

    private Data createInputDataSaveNoteWorker(String[] values, long timeStamp) {
        Data.Builder builder = new Data.Builder();
        builder.putString(NOTE_ID_KEY, values[0])
                .putString(NOTE_CONTENT_KEY, values[1])
                .putString(USER_ID_KEY, values[2])
                .putLong(TIMESTAMP_KEY, timeStamp);
        return builder.build();
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