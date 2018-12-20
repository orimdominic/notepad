package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.sudokaizen.notepad.AppExecutors;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private AppDatabase appDb;
    private AppExecutors mAppExecutors;
    public MutableLiveData<UserEntity> mUser = new MutableLiveData<>();

    public UserRepository(Context context) {
        appDb = AppDatabase.getInstance(context);
        mAppExecutors = new AppExecutors();
    }

    public static UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }

    public void insertUser(final UserEntity user) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.userDao().insertUser(user);
            }
        });
    }

    public UserEntity getUser() {
        return appDb.userDao().getUser();
    }

    public void deleteUser(final UserEntity user) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.userDao().deleteUserById(user.getId());
            }
        });
    }

    public void deleteAllUsers() {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDb.userDao().deleteAllUsers();
            }
        });
    }
}
