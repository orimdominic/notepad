package com.sudokaizen.notepad.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Query("SELECT * FROM users")
    UserEntity getUser();

    @Query("DELETE FROM users WHERE id = :id")
    void deleteUserById(String id);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
