package com.termtacker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.termtacker.R;
import com.termtacker.data.MentorRepository;
import com.termtacker.models.Mentor;
import com.termtacker.models.MentorAdapter;

public class SelectMentorActivity extends AppCompatActivity
{
    public static String EXTRA_MENTOR_ID = "com.termtracker.SelectMentorActivity.EXTRA_MENTOR_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mentor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);

        RecyclerView recyclerView = findViewById(R.id.select_mentor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final MentorAdapter adapter = new MentorAdapter();
        recyclerView.setAdapter(adapter);

        MentorRepository repository = new MentorRepository(getApplication());
        adapter.submitList(repository.getAllMentors());

        adapter.setOnItemClickListener(new MentorAdapter.onItemClickListener()
        {
            @Override
            public void onItemClick(Mentor mentor)
            {
                Intent data = new Intent();
                data.putExtra(EXTRA_MENTOR_ID,mentor.getMentorId());

                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
