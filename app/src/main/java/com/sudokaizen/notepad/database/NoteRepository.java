package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.sudokaizen.notepad.AppExecutors;

import java.util.List;

public class NoteRepository {

    private static NoteRepository instance;
    private AppDatabase appDb;

    private AppExecutors mAppExecutors;

    public static NoteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoteRepository(context);
        }

        return instance;
    }

    private NoteRepository(Context context) {
        appDb = AppDatabase.getInstance(context);
        mAppExecutors = new AppExecutors();
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

    public NoteEntry getNoteById(final int noteId) {
        return appDb.noteDao().getNoteById(noteId);
    }
}