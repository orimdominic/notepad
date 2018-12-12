package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface UserDao {

    @Insert
    void insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<UserEntity> getUser(String id);

    @Query("DELETE FROM users WHERE id = :id")
    void deleteUser(String id);
}
