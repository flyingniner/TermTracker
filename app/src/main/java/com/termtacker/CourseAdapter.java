package com.termtacker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class CourseAdapter extends ListAdapter<Course, CourseAdapter.CourseHolder>
{
    private CourseAdapter.onItemClickListener listener;
    int courseTermIntentID = 7;

    public CourseAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Course> DIFF_CALLBACK = new DiffUtil.ItemCallback<Course>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem)
        {
            return oldItem.getCourseId() == newItem.getCourseId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem)
        {
            return oldItem.getTitle() == newItem.getTitle() &&
                    oldItem.getStartDate() == newItem.getStartDate() &&
                    oldItem.getEndDate() == newItem.getEndDate() &&
                    oldItem.getStatus() == newItem.getStatus() &&
                    oldItem.getCourseMentorId() == newItem.getCourseMentorId() &&
                    oldItem.getTermId() == newItem.getTermId();
        }
    };

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int position)
    {
        Course currentCourse = getItem(position);
        holder.textViewCourseName.setText(currentCourse.getTitle());
        holder.textViewCourseStart.setText(currentCourse.getStartDate().format(Utils.dateFormatter_MMMddyyyy));
        holder.textViewCourseEnd.setText(currentCourse.getEndDate().format(Utils.dateFormatter_MMMddyyyy));

        switch (currentCourse.getStatus())
        {
            case Status.COMPLETED:
                holder.imageViewCourseStatus.setImageResource(R.drawable.ic_completed_72dp);
                holder.textViewCourseEndLabel.setText(R.string.endLabel);
                break;
            case Status.PENDING:
                holder.imageViewCourseStatus.setImageResource(R.drawable.ic_pending_72dp);
                holder.textViewCourseEndLabel.setText(R.string.goalLabel);
                break;
            case Status.IN_PROGRESS:
                holder.imageViewCourseStatus.setImageResource(R.drawable.ic_inprogress_72dp);
                holder.textViewCourseEndLabel.setText(R.string.goalLabel);
                break;
            case Status.INCOMPLETE:
                holder.imageViewCourseStatus.setImageResource(R.drawable.ic_incomplete_72dp);
                holder.textViewCourseEndLabel.setText(R.string.endLabel);
                break;
        }



    }

    public Course getCourseAt(int position) { return getItem(position); };

    class CourseHolder extends RecyclerView.ViewHolder
    {
        private TextView textViewCourseName;
        private TextView textViewCourseStart;
        private TextView textViewCourseEnd;
        private TextView textViewCourseEndLabel; //TODO: set label as gaol unless its marked completed
        private ImageView imageViewCourseStatus;

        public CourseHolder(@NonNull View itemView)
        {
            super(itemView);
            this.textViewCourseName = itemView.findViewById(R.id.course_item_course_title);
            this.textViewCourseStart = itemView.findViewById(R.id.course_item_course_start);
            this.textViewCourseEnd = itemView.findViewById(R.id.course_item_course_end);
            this.textViewCourseEndLabel = itemView.findViewById(R.id.course_item_end_label);
            this.imageViewCourseStatus = itemView.findViewById(R.id.course_item_progress_icon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(listener != null && position != RecyclerView.NO_POSITION)
                    listener.onItemClick(getItem(position));
            });
        }


    }

    public interface onItemClickListener
    {
        void onItemClick(Course course);
    }

    public void setOnItemClickListener(CourseAdapter.onItemClickListener listener)
    {
        this.listener = listener;
    }
}
