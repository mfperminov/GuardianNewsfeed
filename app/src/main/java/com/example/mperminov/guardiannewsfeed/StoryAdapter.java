package com.example.mperminov.guardiannewsfeed;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for RecyclerView. It is  simular to ListView adapter but much more flexible
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private List<Story> mDataset;
    private Boolean showAuthor;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mArticleTitleTextView;
        public TextView mSectionTextView;
        public TextView mAuthorTextView;
        public TextView mDateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mArticleTitleTextView = itemView.findViewById(R.id.article_title);
            mSectionTextView = itemView.findViewById(R.id.section);
            mAuthorTextView = itemView.findViewById(R.id.author);
            mDateTextView = itemView.findViewById(R.id.date);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)

    public StoryAdapter(List<Story> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_item, parent, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        showAuthor = pref.getBoolean("author", false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mArticleTitleTextView.setText(mDataset.get(position).getmArticleTitle());
        holder.mSectionTextView.setText(mDataset.get(position).getmSection());
        //author value can be empty
        if (TextUtils.isEmpty(mDataset.get(position).getmAuthor()) || !showAuthor) {
            holder.mAuthorTextView.setVisibility(View.INVISIBLE);
        } else {
            holder.mAuthorTextView.setVisibility(View.VISIBLE);
            holder.mAuthorTextView.setText(mDataset.get(position).getmAuthor());
        }
        holder.mDateTextView.setText(mDataset.get(position).getmDate());
    }

    // Return the size of Story list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
