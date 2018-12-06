package com.sudokaizen.notepad;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sudokaizen.notepad.database.AppRepository;
import com.sudokaizen.notepad.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mViewModel;
    private AppRepository mAppRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));
            }
        });

        mAppRepository = AppRepository.getInstance(MainActivity.this);
        initialiseViewModel();
    }

    private void initialiseViewModel() {
        mViewModel = ViewModelProviders.of(MainActivity.this)
                .get(MainActivityViewModel.class);
    }
}
