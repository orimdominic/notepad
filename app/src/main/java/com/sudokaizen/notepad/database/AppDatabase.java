package com.sudokaizen.notepad.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {NoteEntry.class, UserEntity.class}, version = 2, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "Notes.db";
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance =
                        Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class,
                                DATABASE_NAME)
//                                .addMigrations(FROM_1_TO_2)
                                .build();
            }
        }
        return sInstance;
    }

    public abstract NoteDao noteDao();
    public abstract UserDao userDao();

    static final Migration FROM_1_TO_2 = new Migration(1,2) {
        //  Add new table (users) to database
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };
}
