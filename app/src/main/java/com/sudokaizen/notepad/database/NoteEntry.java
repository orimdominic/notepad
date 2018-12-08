package com.sudokaizen.notepad.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "notes")
public class NoteEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String content;
    private long timestamp;

    @Ignore
    public NoteEntry() {
    }

    public NoteEntry(int id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateString(){
        SimpleDateFormat stringFormat = new SimpleDateFormat("EEE, MMM d, h:mm a");
        return stringFormat.format(new Date(this.timestamp));
    }
}
