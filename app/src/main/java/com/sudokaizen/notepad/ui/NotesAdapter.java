package com.sudokaizen.notepad.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.NoteEntry;

import java.util.List;
import java.util.Random;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<NoteEntry> mNoteEntries;
    private LayoutInflater mLayoutInflater;

    public NotesAdapter(Context context, List<NoteEntry> noteEntries) {
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
    public void onBindViewHolder(@NonNull NotesAdapter.NoteViewHolder noteViewHolder, int i) {
        NoteEntry note = mNoteEntries.get(i);

        noteViewHolder.tvContent.setText(note.getContent());
        noteViewHolder.tvDateTime.setText(note.getDateString());
//        noteViewHolder.cvNote.setStrokeColor(getRandomColor())
    }

    @Override
    public int getItemCount() {
        return mNoteEntries != null ? mNoteEntries.size() : 0;
    }


    public NoteEntry getItemAt(int position) {
        return mNoteEntries.get(position);
    }

//    private int getRandomColor(){
//        Random random = new Random();
//        int[] colors = {
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray,
//                android.R.color.darker_gray
//        };
//        return random.nextInt(colors.length-1);
//    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cvNote;
        TextView tvContent;
        TextView tvDateTime;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cvNote = itemView.findViewById(R.id.card_view_note);
            tvContent = itemView.findViewById(R.id.tv_note_content);
            tvDateTime = itemView.findViewById(R.id.tv_note_date_time);
        }
    }

}
