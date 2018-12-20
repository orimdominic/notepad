package com.sudokaizen.notepad.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.viewmodel.CreateNoteViewModel;

import java.util.List;
import java.util.Random;

import static com.sudokaizen.notepad.ui.MainActivity.NOTE_ID;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<NoteEntry> mNoteEntries;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public NotesAdapter(Context context, List<NoteEntry> noteEntries) {
        mContext =context;
        mLayoutInflater = LayoutInflater.from(context);
        mNoteEntries = noteEntries;
    }

    @NonNull
    @Override
    public NotesAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mLayoutInflater.inflate(R.layout.item_note, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesAdapter.NoteViewHolder noteViewHolder, int i) {
        final NoteEntry note = mNoteEntries.get(i);

        noteViewHolder.tvContent.setText(note.getContent());
        noteViewHolder.tvDateTime.setText(note.getId() + " " + note.getDateString());

        noteViewHolder.cvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int notePosition =  noteViewHolder.getAdapterPosition();
                NoteEntry note  = getItemAt(notePosition);
                Intent intent = new Intent(mContext, CreateNoteActivity.class);
                intent.putExtra(NOTE_ID, note.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteEntries != null ? mNoteEntries.size() : 0;
    }


    public NoteEntry getItemAt(int position) {
        return mNoteEntries.get(position);
    }


    public class NoteViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView cvNote;
        TextView tvContent;
        TextView tvDateTime;

        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            cvNote = itemView.findViewById(R.id.card_view_note);
            tvContent = itemView.findViewById(R.id.tv_note_content);
            tvDateTime = itemView.findViewById(R.id.tv_note_date_time);
        }

    }

}
