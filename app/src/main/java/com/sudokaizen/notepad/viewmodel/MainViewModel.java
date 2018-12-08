package com.sudokaizen.notepad.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sudokaizen.notepad.database.AppRepository;
import com.sudokaizen.notepad.database.NoteEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private AppRepository mAppRepo;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mAppRepo = AppRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<NoteEntry>> getNotes() {
        return mAppRepo.getAllNotes();
    }
}
