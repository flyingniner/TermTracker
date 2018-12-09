package com.termtacker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.time.LocalDate;
import java.time.Month;
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
        version = 11)
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


        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db)
        {
//            super.onOpen(db);
//
//            Mentor[] mentors = getMentorsForPrePop();
//            Course[] courses = getCoursesForPrePop();
//            Term[] terms = getTermsForPrePop();
//
//            new PopuplateMentorTableAsync(INSTANCE.mentorDao()).execute(mentors);
//            new PopulateTermTableAsync(INSTANCE.termDao()).execute(terms);
//            new PopulateCourseTableAsync(INSTANCE.courseDao()).execute(courses);
        }
    };

    private static Mentor[] getMentorsForPrePop()
    {
        return new Mentor[]{
                new Mentor("John Smith", 8774357948L, "cmsoftware@wgu.edu"),
                new Mentor("Sammy Doe", 8774357948L, "cmsoftware@wgu.edu"),
                new Mentor("Mia Joe", 8774357948L, "cmsoftware@wgu.edu"),
        };
    }

    private static Course[] getCoursesForPrePop()
    {
        return new Course[] {
                new Course("Math 101",
                           LocalDate.of(2018, Month.JANUARY, 1),
                           LocalDate.of(2018, Month.FEBRUARY, 28),
                           Status.COMPLETED,
                           1,
                           1),
                new Course("Intro to Programming",
                           LocalDate.of(2018, Month.MARCH, 1),
                           LocalDate.of(2018, Month.APRIL, 30),
                           Status.COMPLETED,
                           2,
                           1),
                new Course("Intro to Databases",
                           LocalDate.of(2018, Month.MAY, 1),
                           LocalDate.of(2018, Month.JUNE, 30),
                           Status.COMPLETED,
                           3,
                           1),
                new Course("Programming for the Web",
                           LocalDate.of(2018, Month.JULY, 1),
                           LocalDate.of(2018, Month.JULY, 31),
                           Status.COMPLETED,
                           1,
                           2),
                new Course("Intermediate Programming I",
                           LocalDate.of(2018, Month.AUGUST, 1),
                           LocalDate.of(2018, Month.OCTOBER, 31),
                           Status.IN_PROGRESS,
                           2,
                           2),
                new Course("Networking Fundamentals",
                           LocalDate.of(2018, Month.NOVEMBER, 1),
                           LocalDate.of(2018, Month.DECEMBER, 31),
                           Status.PENDING,
                           3,
                           2),
        };
    }

    private static Term[] getTermsForPrePop()
    {
        return new Term[] {
        new Term("Spring 2018",
                 LocalDate.of(2018,Month.JANUARY,1),
                 LocalDate.of(2018,Month.JUNE,30),
                 Status.COMPLETED),
        new Term("Spring 2018",
                 LocalDate.of(2018,Month.JULY,1),
                 LocalDate.of(2018,Month.DECEMBER,31),
                 Status.IN_PROGRESS),
        new Term("Spring 2018",
                 LocalDate.of(2019,Month.JANUARY,1),
                 LocalDate.of(2019,Month.JUNE,30),
                 Status.PENDING)
        };
    }


    private static AppDatabase buildDatabaseInstance(Context context)
    {
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class,DB_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(roomCallBack)
                /*.allowMainThreadQueries()*/
                .build();
        return db;
    }


    public static void destroyInstance()
    {
        INSTANCE = null;
    }

    private static class PopuplateMentorTableAsync extends AsyncTask<Mentor, Void, Void>
    {
        private static final String TAG = "PopulateMentorTableAsysnc";
        private MentorDao mentorDao;

        public PopuplateMentorTableAsync(MentorDao mentorDao)
        {
            this.mentorDao = mentorDao;
        }

        @Override
        protected Void doInBackground(Mentor... mentors)
        {
            for (int i = 0; i < mentors.length; i++)
            {
                try
                {
                    Log.d(TAG, "Inserting mentor: " + mentors[i].getName());
                    mentorDao.insertMentor(mentors[i]);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Error inserting the mentor, see stack trace: " + e.getMessage() + e.getStackTrace());
                }
            }
            return null;
        }
    }

    private static class PopulateTermTableAsync extends AsyncTask<Term, Void, Void>
    {
        private static final String TAG = "PopulateTermTableAsysnc";
        private TermDao termDao;

        public PopulateTermTableAsync(TermDao termDao)
        {
            this.termDao = termDao;
        }

        @Override
        protected Void doInBackground(Term... terms)
        {
            for (int i = 0; i < terms.length; i++)
            {
                try
                {
                    Log.d(TAG, "Inserting mentor: " + terms[i].getTitle());
                    termDao.insertTerm(terms[i]);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Error inserting the term, see stack trace: " + e.getMessage() + e.getStackTrace());
                }
            }
            return null;
        }
    }

    private static class PopulateCourseTableAsync extends AsyncTask<Course, Void, Void>
    {
        private static final String TAG = "PopulateCourseTableAsysnc";
        private CourseDao courseDao;

        public PopulateCourseTableAsync(CourseDao courseDao)
        {
            this.courseDao = courseDao;
        }

        @Override
        protected Void doInBackground(Course... courses)
        {
            try
            {
                for (int i = 0; i < courses.length; i++)
                {
                    Log.d(TAG,"Inserting course: " + courses[i].getTitle());
                    courseDao.insertCourse(courses[i]);
                }
            }
            catch (Exception e)
            {
                Log.d(TAG, "Error inserting the course, see stack trace: " + e.getMessage() + e.getStackTrace());
            }


            return null;
        }
    }
}
