package com.example.mperminov.guardiannewsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Story>> {
    private ArrayList<Story> myStories;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private TextView mEmptyStateTextView;
    //developer api key for access Guardian API
    private static final String API_KEY = "accc44ab-55e4-4a73-9006-45b77fb32516";
    /*
    Query params:
    order - newest first
    show references - author (from project rubric - "If available, author name and date published
     should be included. Please note not all responses will contain these pieces of data,
      but it is required to include them if they are present.")
    search topic - Russia
     */
    private static final String GUARD_URL = "http://content.guardianapis.com/search?order-by=newest" +
            "&show-references=author&q=Russia&api-key=" + API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler);
        // improve performance because changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //find the progress bar and empty view
        mProgressBar = findViewById(R.id.loading_spinner);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        myStories = new ArrayList<>();
        mAdapter = new StoryAdapter(myStories);
        mRecyclerView.setAdapter(mAdapter);
        //add custom on item click listener for Intent purpose
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Story curStory = myStories.get(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(curStory.getmURL()));
                startActivity(browserIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                onItemClick(view, position);
            }
        }));
        if (isNetworkAvailable()) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            //show user that there is a problem with Internet connection
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        return new StoryLoader(this, GUARD_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        //hide progress bar
        mProgressBar.setVisibility(View.GONE);
        //clear old data and notify adapter about it
        myStories.clear();
        mAdapter.notifyDataSetChanged();
        // If there is a valid list of {@link Storie}s, then add them to the adapter's
        // data set.
        if (data != null && !data.isEmpty()) {
            //add new data and notify adapter about it
            myStories.addAll(data);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.GONE);
        } else {
            //if no data - show empty view
            mRecyclerView.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_stories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        myStories.clear();
        mAdapter.notifyDataSetChanged();
    }

    //check whether network is available on phone
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
