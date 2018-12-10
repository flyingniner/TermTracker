package com.termtacker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class AssessmentAdapter extends ListAdapter<Assessment, AssessmentAdapter.AssessmentHolder>
{


    private onItemClickListener listener;

    public AssessmentAdapter()
    {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public AssessmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_item,parent,false);
        return new AssessmentHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(AssessmentHolder holder, int position)
    {
        Assessment currentAssessment = getItem(position);
        holder.assessmentType.setText(currentAssessment.getAssessmentType());
        holder.assessmentCode.setText(currentAssessment.getAssessmentCode());
        holder.assessmentScheduledDate.setText(currentAssessment.getAssessmentDate().format(Utils.dateFormatter_MMMddyyyy));

        String result = currentAssessment.getResult();
        if (result.equals(Status.COMPLETED))
            holder.assessmentProgressIcon.setImageResource(R.drawable.ic_completed_72dp);
        else if (result.equals(Status.PENDING))
            holder.assessmentProgressIcon.setImageResource(R.drawable.ic_pending_72dp);
        else
            holder.assessmentProgressIcon.setImageResource(R.drawable.ic_incomplete_72dp);



    }

    private static final DiffUtil.ItemCallback<Assessment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Assessment>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull Assessment oldItem, @NonNull Assessment newItem)
        {
            return oldItem.getAssessmentId() == newItem.getAssessmentId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Assessment oldItem, @NonNull Assessment newItem)
        {
            return oldItem.getAssessmentType() == newItem.getAssessmentType() &&
                    oldItem.getAssessmentDate() == newItem.getAssessmentDate() &&
                    oldItem.getAssessmentCode() == newItem.getAssessmentCode() &&
                    oldItem.getCourseId() == newItem.getCourseId() &&
                    oldItem.getResult() == newItem.getResult();
        }
    };


    private Assessment getAssessmentAt(int position)
    {
        return getItem(position);
    }

    class AssessmentHolder extends RecyclerView.ViewHolder
    {
        private TextView assessmentType;
        private TextView assessmentCode;
        private ImageView assessmentProgressIcon;
        private TextView assessmentScheduledDate;

        public AssessmentHolder(@NonNull View itemView)
        {
            super(itemView);

            assessmentType = itemView.findViewById(R.id.assessment_item_assessment_type);
            assessmentCode = itemView.findViewById(R.id.assessment_item_assessment_code);
            assessmentProgressIcon = itemView.findViewById(R.id.assessment_item_progressIcon);
            assessmentScheduledDate = itemView.findViewById(R.id.assessment_item_scheduled);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION)
                    listener.onItemClick(getItem(position));
            });
        }
    }

    public interface onItemClickListener
    {
        void onItemClick(Assessment assessment);
    }

    public void setOnItemClickListener(AssessmentAdapter.onItemClickListener listener)
    {
        this.listener = listener;
    }
}
