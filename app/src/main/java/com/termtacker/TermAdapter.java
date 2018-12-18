package com.termtacker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TermAdapter extends ListAdapter<Term, TermAdapter.TermHolder>
{
    private TermAdapter.onItemClickListener listener;
//    private List<Term> terms;
    int termDetailIntentId = 8;

    public TermAdapter()
    {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Term> DIFF_CALLBACK = new DiffUtil.ItemCallback<Term>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull Term oldItem, @NonNull Term newItem)
        {
            return oldItem.getTermId() == newItem.getTermId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Term oldItem, @NonNull Term newItem)
        {
            return oldItem.getTitle() == newItem.getTitle() &&
                    oldItem.getStartDate() == newItem.getStartDate() &&
                    oldItem.getEndDate() == newItem.getEndDate() &&
                    oldItem.getStatus() == newItem.getStatus();
        }
    };


    @NonNull
    @Override
    public TermHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item,parent,false);
        return new TermHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TermHolder holder, int position)
    {
        Term currentTerm = getItem(position);
        holder.textViewTitle.setText(currentTerm.getTitle());
        holder.texViewStart.setText(currentTerm.getStartDate().format(Utils.dateFormatter_MMMddyyyy));
        holder.textViewEnd.setText(currentTerm.getEndDate().format(Utils.dateFormatter_MMMddyyyy));
        switch (currentTerm.getStatus())
        {
            case Status.COMPLETED:
                holder.imageViewProgressIcon.setImageResource(R.drawable.ic_completed_72dp);
                break;
            case Status.PENDING:
                holder.imageViewProgressIcon.setImageResource(R.drawable.ic_pending_72dp);
                break;
            case Status.IN_PROGRESS:
                holder.imageViewProgressIcon.setImageResource(R.drawable.ic_inprogress_72dp);
                break;
            case Status.INCOMPLETE:
                holder.imageViewProgressIcon.setImageResource(R.drawable.ic_incomplete_72dp);
                break;
        }

    }

    public Term getTermAt(int position)
    {
        return getItem(position);
    }

    class TermHolder extends RecyclerView.ViewHolder
    {
        private TextView textViewTitle;
        private TextView texViewStart;
        private TextView textViewEnd;
        private ImageView imageViewProgressIcon;

        public TermHolder(@NonNull View itemView)
        {
            super(itemView);

            this.textViewTitle = itemView.findViewById(R.id.term_item_Title);
            this.texViewStart = itemView.findViewById(R.id.term_item_Start);
            this.textViewEnd = itemView.findViewById(R.id.term_item_End);
            this.imageViewProgressIcon = itemView.findViewById(R.id.assessment_item_progressIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION)
                    listener.onItemClick(getItem(position));
            });
        }
    }

    public interface onItemClickListener
    {
        void onItemClick(Term term);
    }

    public void setOnItemClickListener(TermAdapter.onItemClickListener listener)
    {
        this.listener = listener;
    }
}
