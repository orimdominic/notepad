package com.sudokaizen.notepad.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.sudokaizen.notepad.AppExecutors;
import com.sudokaizen.notepad.database.NoteRepository;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.database.UserEntity;
import com.sudokaizen.notepad.database.UserRepository;

public class CreateNoteViewModel extends AndroidViewModel {

    private NoteRepository noteRepo;
    private AppExecutors mAppExecutors;
    public MutableLiveData<NoteEntry> mLiveNote =
            new MutableLiveData<>();

    public CreateNoteViewModel(@NonNull Application application) {
        super(application);
        noteRepo = NoteRepository.getInstance(application.getApplicationContext());
        mAppExecutors = new AppExecutors();
    }


    public void getNoteById(final int noteId) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                NoteEntry note = noteRepo.getNoteById(noteId);
                mLiveNote.postValue(note);
            }
        });
    }

}
