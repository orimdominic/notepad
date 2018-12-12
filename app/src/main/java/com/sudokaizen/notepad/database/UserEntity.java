package com.sudokaizen.notepad.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {

    @NonNull
    @PrimaryKey
    private String id; // id is email address
    private String username;

    public UserEntity(@NonNull String id, String username) {
        this.id = id;
        this.username = username;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
