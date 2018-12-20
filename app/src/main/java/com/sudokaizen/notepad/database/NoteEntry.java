package com.sudokaizen.notepad.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

@IgnoreExtraProperties
@Entity(tableName = "notes")
public class NoteEntry {

    @PrimaryKey
    @NonNull
    private String id;
    private String content;
    private long timestamp;

    @Ignore
    public NoteEntry() {
        // Firebase required no-arg constructor
    }


    public NoteEntry(@NonNull String id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateString() {
        SimpleDateFormat stringFormat = new SimpleDateFormat("EEE, MMM d, h:mm a");
        return stringFormat.format(new Date(this.timestamp));
    }
}
