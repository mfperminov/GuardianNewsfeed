package com.example.mperminov.guardiannewsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
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
    private static final String GUARD_URL = "http://content.guardianapis.com/search";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.news_recycler_view);
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter
        // is the default value for this preference.
        String topic = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default));
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String newsFrom = sharedPrefs.getString(getString(R.string.settings_date_from_key),
                setDefaultDate());
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARD_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Append query parameter and its value. Keys for settings and parameters
        // in query are the same
        uriBuilder.appendQueryParameter(getString(R.string.settings_api_key_key), API_KEY);
        uriBuilder.appendQueryParameter(getString(R.string.settings_topic_key), topic);
        uriBuilder.appendQueryParameter(getString(R.string.settings_order_by_key), orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.settings_tags_key),
                getString(R.string.settings_tags_value));
        uriBuilder.appendQueryParameter(getString(R.string.settings_date_from_key), newsFrom);
        // Return the completed uri e.g. http://content.guardianapis.com/search?
        // api-key=accc44ab-55e4-4a73-9006-45b77fb32516&q=usa&order-by=newest&
        // show-tags=contributor&from-date=2018-05-11
        return new StoryLoader(this, uriBuilder.toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //method for setting default value of date in app
    //use boolean key switcher for different data picker
    public String setDefaultDate() {
        String dateString;
        //how many days from show news
        //minus for subtraction in method
        final int DAYS_AGO = -3;
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, DAYS_AGO);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dateString = String.format("%d-%02d-%02d", year, month + 1, day);
        return dateString;
    }
}
