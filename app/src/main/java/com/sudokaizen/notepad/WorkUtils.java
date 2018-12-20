package com.sudokaizen.notepad;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class WorkUtils {

    public static void scheduleNoteSyncWork(Data inputDataSaveNoteWorker){
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType
                (NetworkType.CONNECTED).build();

        OneTimeWorkRequest saveNoteWork =
                new OneTimeWorkRequest.Builder(NoteSyncWorker.class)
                        .setConstraints(constraints)
                        .setInputData(inputDataSaveNoteWorker)
                        .build();
        WorkManager.getInstance().enqueue(saveNoteWork);
    }
}
