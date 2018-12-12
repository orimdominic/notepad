package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    LiveData<List<NoteEntry>> getAllNotes();

    @Query("SELECT * FROM notes WHERE id = :id")
    NoteEntry getNoteById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(NoteEntry note);

    @Delete
    void deleteNote(NoteEntry note);

    @Query("DELETE FROM notes")
    void deleteAllNotes();
}
