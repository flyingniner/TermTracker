package com.termtacker;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class MentorRepository
{
    private Mentor mentor;
    private MentorDao mentorDao;
    private List<Mentor> mentors;

    public MentorRepository(Application application)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.mentorDao = database.mentorDao();
        this.mentors = mentorDao.getAllMentors();
    }

    public MentorRepository(Application application, int courseId)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.mentorDao = database.mentorDao();
        this.mentor = mentorDao.getMentorByCourseId(courseId);
    }

    public void insertMentor(Mentor mentor)
    {
        new InsertMentorAsync(mentorDao).execute();
    }

    public void updateMentor(Mentor mentor)
    {
        new UpdateMentorAsync(mentorDao).execute();
    }

    public void deleteMentro(Mentor mentor)
    {
        new DeleteMentorAsync(mentorDao).execute();
    }

//    public LiveData<Mentor> getMentor(int courseId)
//    {
//        return mentor;
//    }

    public Mentor getMentor() { return this.mentor; }

    public List<Mentor> getAllMentors() {return this.mentors; }

    public static class InsertMentorAsync extends AsyncTask<Mentor,Void, Void>
    {
        private MentorDao mentorDao;

        public InsertMentorAsync(MentorDao mentorDao)
        {
            this.mentorDao = mentorDao;
        }

        @Override
        protected Void doInBackground(Mentor... mentors)
        {
            mentorDao.insertMentor(mentors[0]);
            return null;
        }
    }

    public static class UpdateMentorAsync extends AsyncTask<Mentor,Void, Void>
    {
        private MentorDao mentorDao;

        public UpdateMentorAsync(MentorDao mentorDao)
        {
            this.mentorDao = mentorDao;
        }

        @Override
        protected Void doInBackground(Mentor... mentors)
        {
            mentorDao.updateMentor(mentors[0]);
            return null;
        }
    }

    public static class DeleteMentorAsync extends AsyncTask<Mentor, Void, Void>
    {
        private MentorDao mentorDao;

        public DeleteMentorAsync(MentorDao mentorDao)
        {
            this.mentorDao = mentorDao;
        }


        @Override
        protected Void doInBackground(Mentor... mentors)
        {
            mentorDao.deleteMentor(mentors[0]);
            return null;
        }
    }

    public static class GetMentorAsync extends AsyncTask<Void, Void, Void>
    {
        private MentorDao mentorDao;
        private int courseId;

        public GetMentorAsync(MentorDao mentorDao, int courseId)
        {
            this.mentorDao = mentorDao;
            this.courseId = courseId;
        }


        @Override
        protected Void doInBackground(Void... voids)
        {
            mentorDao.getMentorByCourseId(courseId);
            return null;
        }
    }

}
