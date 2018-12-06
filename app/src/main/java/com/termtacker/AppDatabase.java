package com.termtacker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {
        Assessment.class, Course.class,
        Note.class, Mentor.class,
        Term.class},
        version = 8)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase
{
    private static AppDatabase INSTANCE;
    private static final String DB_NAME = "TERM_TRACKER.DB";

    public abstract AssessmentDao assessmentDao();
    public abstract CourseDao courseDao();
    public abstract NoteDao noteDao();
    public abstract MentorDao mentorDao();
//    public abstract StatusDao statusDao();
    public abstract TermDao termDao();


    public static synchronized AppDatabase getInstance(final Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = buildDatabaseInstance(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            super.onCreate(db);
//            new PopulateStatusTableAsync(INSTANCE).execute();
            new PopuplateMentorTableAsync(INSTANCE).execute();
            new PopulateTermTableAsync(INSTANCE).execute();
        }
    };


    private static AppDatabase buildDatabaseInstance(Context context)
    {
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class,DB_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(roomCallBack)
                .allowMainThreadQueries()
                .build();
        return db;
    }


    public static void destroyInstance()
    {
        INSTANCE = null;
    }

//    private static class PopulateStatusTableAsync extends AsyncTask<Void, Void, Void>
//    {
//        private StatusDao statusDao;
//
//        private PopulateStatusTableAsync(AppDatabase db)
//        {
//            statusDao = db.statusDao();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids)
//        {
//            statusDao.insertStatus(new com.termtacker.Status("Completed"));
//            statusDao.insertStatus(new com.termtacker.Status("Pending"));
//            statusDao.insertStatus(new com.termtacker.Status("InComplete"));
//            statusDao.insertStatus(new com.termtacker.Status("InProgress"));
//            return null;
//        }
//    }

    private static class PopuplateMentorTableAsync extends AsyncTask<Void, Void, Void>
    {
        private static final String TAG = "PopulateMentorTableAsysnc";
        private MentorDao mentorDao;

        public PopuplateMentorTableAsync(AppDatabase db)
        {
            mentorDao = db.mentorDao();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.d(TAG,"inserting mentors");
            mentorDao.insertMentor(new Mentor("John Smith",8774357948L,"John.Smith@wgu.edu"));
            mentorDao.insertMentor(new Mentor("Sally Joe",8774357948L,"Sally.Joe@wgu.edu"));
            Mentor mentor = mentorDao.getMentorByName("John Smith");
            Log.d(TAG, "Mentor Id Created for John Smith" + mentor.getMentorId());
            return null;
        }
    }

    private static class PopulateTermTableAsync extends AsyncTask<Void, Void, Void>
    {
        private static final String TAG = "PopulateTermTableAsysnc";
        private TermDao termDao;

        public PopulateTermTableAsync(AppDatabase db)
        {
            termDao = db.termDao();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.d(TAG,"inserting mentors");
            termDao.insertTerm(new Term("Term 1", LocalDate.of(2018,1,1), LocalDate.of(2018,6,30), com.termtacker.Status.COMPLETED));
            termDao.insertTerm(new Term("Term 2", LocalDate.of(2018,7,1), LocalDate.of(2018,12,31), com.termtacker.Status.IN_PROGRESS));
            LiveData<List<Term>> terms = termDao.getTerms();
            Log.d(TAG, "Term 1 created with end date of: " + terms.getValue().get(0).getEndDate());
            return null;
        }
    }
}
