package com.termtacker.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.termtacker.R;
import com.termtacker.utilities.Utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder>
{
    private NoteAdapter.onItemClickListener listener;


    public NoteAdapter()
    {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem)
        {
            return oldItem.getCourseNoteId() == newItem.getCourseNoteId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem)
        {
            return oldItem.getNoteDate() == newItem.getNoteDate() &&
                    oldItem.getNote() == newItem.getNote();
        }
    };


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position)
    {
        Note currentNote = getItem(position);
        holder.textViewNoteDate.setText(currentNote.getNoteDate().format(Utils.dateFormatter_MMMddyyyy));
        holder.textViewNoteText.setText(currentNote.getNote());
    }

    class NoteHolder extends RecyclerView.ViewHolder
    {
        private TextView textViewNoteDate;
        private TextView textViewNoteText;

        public NoteHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewNoteDate = itemView.findViewById(R.id.note_item_date);
            textViewNoteText = itemView.findViewById(R.id.note_item_note_text);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION)
                    listener.onItemClick(getItem(position));

            });
        }
    }

    public interface onItemClickListener
    {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(NoteAdapter.onItemClickListener listener)
    {
        this.listener = listener;
    }
}
