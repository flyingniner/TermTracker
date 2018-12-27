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

public class MentorAdapter extends ListAdapter<Mentor, MentorAdapter.MentorHolder>
{
    private String[] mentorNames;
    private MentorAdapter.onItemClickListener listener;

    public MentorAdapter()
    {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Mentor> DIFF_CALLBACK = new DiffUtil.ItemCallback<Mentor>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull Mentor oldItem, @NonNull Mentor newItem)
        {
            return oldItem.getMentorId() == newItem.getMentorId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Mentor oldItem, @NonNull Mentor newItem)
        {
            return oldItem.getName() == newItem.getName() &&
                    oldItem.getPhoneNumber() == newItem.getPhoneNumber() &&
                    oldItem.getEmail() == newItem.getEmail();
        }
    };



    class MentorHolder extends RecyclerView.ViewHolder
    {
        private TextView textViewMentorName;
        private TextView textViewMentorPhone;
        private TextView textViewMentorEmail;


        public MentorHolder(View viewItem)
        {
            super(viewItem);
            this.textViewMentorName = viewItem.findViewById(R.id.text_view_mentor_item_name);
            this.textViewMentorPhone = viewItem.findViewById(R.id.text_view_mentor_item_phone);
            this.textViewMentorEmail = viewItem.findViewById(R.id.text_view_mentor_item_email);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION)
                {
                    listener.onItemClick(getItem(position));
                }
            });
        }

    }

    public interface onItemClickListener
    {
        void onItemClick(Mentor mentor);
    }

    public void setOnItemClickListener(MentorAdapter.onItemClickListener listener)
    {
        this.listener = listener;
    }


    @NonNull
    @Override
    public MentorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mentor_item,parent,false);

        return new MentorHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MentorHolder holder, int position)
    {
        Mentor currentMentor = getItem(position);
        holder.textViewMentorName.setText(currentMentor.getName());
        holder.textViewMentorPhone.setText(Utils.formatPhoneNumber(currentMentor.getPhoneNumber()));
        holder.textViewMentorEmail.setText(currentMentor.getEmail());

    }
}
