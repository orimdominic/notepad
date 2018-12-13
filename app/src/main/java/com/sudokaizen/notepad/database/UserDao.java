package com.sudokaizen.notepad.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface UserDao {

    @Insert
    void insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :id")
    UserEntity getUserById(String id);

    @Query("DELETE FROM users WHERE id = :id")
    void deleteUserById(String id);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
