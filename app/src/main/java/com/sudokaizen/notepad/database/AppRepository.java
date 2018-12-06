package com.sudokaizen.notepad.database;

public class AppRepository {

    private static final AppRepository repoInstance = new AppRepository();

    public static AppRepository getRepoInstance() {
        return repoInstance;
    }

    private AppRepository() {
    }
}
