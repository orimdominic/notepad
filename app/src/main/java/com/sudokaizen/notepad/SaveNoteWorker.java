package com.sudokaizen.notepad;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.database.NoteRepository;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SaveNoteWorker extends Worker {



    public SaveNoteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        FirebaseDatabase remoteDb = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = remoteDb.getReference();

        String noteId = getInputData().getString(NoteRepository.NOTE_ID_KEY);
        String noteContent = getInputData().getString(NoteRepository.NOTE_CONTENT_KEY);
        long timeStamp = getInputData().getLong(NoteRepository.TIMESTAMP_KEY, 0);
        String userId = getInputData().getString(NoteRepository.USER_ID_KEY);
        NoteEntry noteEntry;
        if (noteId != null && userId!=null) {
            noteEntry = new NoteEntry(noteId, noteContent, timeStamp);
            rootRef.child(userId).child(noteId).setValue(noteEntry);
        }
        return Worker.Result.success();
    }
}
