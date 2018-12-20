package com.sudokaizen.notepad.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {

    @NonNull
    @PrimaryKey
    private String id;
    private String emailAddress;
    private String username;

    public UserEntity(@NonNull String emailAddress, String username) {
        this.id = formatEmailAddress(emailAddress);
        this.emailAddress = emailAddress;
        this.username = username;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    /**
     * Creates an id from the user's email address by
     * removing the '@' and the '.com' characters.
     * An email like abc@xyz.com becomes abcxyz
     * */
    private String formatEmailAddress(String emailAddress){
        String id = emailAddress.replace("@","");
        id = id.replace(".com", "");
        return id;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
