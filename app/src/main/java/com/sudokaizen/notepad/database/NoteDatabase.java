package com.sudokaizen.notepad.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {NoteEntry.class}, version = 1, exportSchema = false)

public abstract class NoteDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "Notes.db";
    private static final Object LOCK = new Object();
    private static NoteDatabase sInstance;

    public static NoteDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance =
                        Room.databaseBuilder(context.getApplicationContext(),
                                NoteDatabase.class,
                                DATABASE_NAME)
                                .build();
            }
        }
        return sInstance;
    }

    public abstract NoteDao noteDao();
}
