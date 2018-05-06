package com.example.mperminov.guardiannewsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Custom Loader used for download data in background
 */
public class StoryLoader extends AsyncTaskLoader<List<Story>> {
    private String mUrl;

    public StoryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the HTTP request for stories data and process the response.
        return QueryUtils.fetchStoriesData(mUrl);
    }
}
