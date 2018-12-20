package com.sudokaizen.notepad.workers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sudokaizen.notepad.database.NoteEntry;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NoteDeleteWorker extends Worker {

    public NoteDeleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        FirebaseDatabase remoteDb = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = remoteDb.getReference();

        String noteId = getInputData().getString(WorkUtils.NOTE_ID_KEY);
        String userId = getInputData().getString(WorkUtils.USER_ID_KEY);

        if (noteId != null && userId != null) {
            rootRef.child(userId).child(noteId).removeValue();
        }

        return Worker.Result.success();
    }
}
