package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

public class SelectMentorActivity extends AppCompatActivity
{
    public static String EXTRA_MENTOR_ID = "com.termtracker.SelectMentorActivity.EXTRA_MENTOR_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mentor);

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
