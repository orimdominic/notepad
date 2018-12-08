package com.sudokaizen.notepad.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.AppRepository;
import com.sudokaizen.notepad.database.NoteEntry;

public class CreateNoteActivity extends AppCompatActivity {

    private AppRepository mAppRepository;
    private TextInputEditText etNote;


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        TextInputLayout til = findViewById(R.id.til_create_note);

        etNote = findViewById(R.id.et_create_note);
        mAppRepository = AppRepository.getInstance(CreateNoteActivity.this);

        // Lint says you are erroneous, but app works with you and I need you
        // because you hide stroke color
        // Who is deceiving me now? App or Lint? Or you?
        til.setBoxStrokeColor(android.R.color.transparent);
    }

    // Note-To-Self: save note on back button pressed.. Easier, more intuitive
    // for the user than button


    @Override
    public void onBackPressed() {

        if (etNote.getText().length() == 0) {
            Toast.makeText(CreateNoteActivity.this, "You didn't add any note", Toast.LENGTH_SHORT)
                    .show();
        }else {
            Toast.makeText(CreateNoteActivity.this, etNote.getText(), Toast.LENGTH_SHORT)
                    .show();
            NoteEntry noteEntry  = new NoteEntry();
            noteEntry.setContent(etNote.getText().toString());
            noteEntry.setTimestamp(System.currentTimeMillis());
            mAppRepository.insertNote(noteEntry);
        }
        finish();

    }
}
