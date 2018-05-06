package com.example.mperminov.guardiannewsfeed;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mArticleTitle;
        public TextView mSection;
        public TextView mAuthor;
        public TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mArticleTitle = itemView.findViewById(R.id.article_title);
            mSection = itemView.findViewById(R.id.section);
            mAuthor = itemView.findViewById(R.id.author);
            mDate = itemView.findViewById(R.id.date);
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
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mArticleTitle.setText(mDataset.get(position).getmArticleTitle());
        holder.mSection.setText(mDataset.get(position).getmSection());
        //author value often will be empty
        if (TextUtils.isEmpty(mDataset.get(position).getmAuthor())) {
            holder.mAuthor.setVisibility(View.INVISIBLE);
        } else {
            holder.mAuthor.setVisibility(View.VISIBLE);
            holder.mAuthor.setText(mDataset.get(position).getmAuthor());
        }
        holder.mDate.setText(mDataset.get(position).getmDate());
    }

    // Return the size of Story list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
