package com.sudokaizen.notepad;

import com.sudokaizen.notepad.database.NoteEntry;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class WorkUtils {

    public static final String NOTE_CONTENT_KEY = "NOTE_CONTENT_KEY";
    public static final String NOTE_ID_KEY = "NOTE_ID_KEY";
    public static final String TIMESTAMP_KEY = "TIMESTAMP_KEY";
    public static final String USER_ID_KEY = "USER_ID_KEY";

    public static void scheduleNoteSyncWork(NoteEntry noteEntry, String userId) {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType
                (NetworkType.CONNECTED).build();

        OneTimeWorkRequest saveNoteWork =
                new OneTimeWorkRequest.Builder(NoteSyncWorker.class)
                        .setConstraints(constraints)
                        .setInputData(setNoteWorkerInputData(noteEntry, userId))
                        .build();
        WorkManager.getInstance().enqueue(saveNoteWork);
    }

    public static void scheduleNoteDeleteWork(NoteEntry noteEntry, String userId) {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType
                (NetworkType.CONNECTED).build();

        OneTimeWorkRequest saveNoteWork =
                new OneTimeWorkRequest.Builder(NoteSyncWorker.class)
                        .setConstraints(constraints)
                        .setInputData(setNoteWorkerInputData(noteEntry, userId))
                        .build();
        WorkManager.getInstance().enqueue(saveNoteWork);
    }

    private static Data setNoteWorkerInputData(NoteEntry noteEntry, String userId) {
        Data.Builder builder = new Data.Builder();
        builder.putString(NOTE_ID_KEY, noteEntry.getId())
                .putString(NOTE_CONTENT_KEY, noteEntry.getContent())
                .putString(USER_ID_KEY, userId)
                .putLong(TIMESTAMP_KEY, noteEntry.getTimestamp());
        return builder.build();
    }
}
