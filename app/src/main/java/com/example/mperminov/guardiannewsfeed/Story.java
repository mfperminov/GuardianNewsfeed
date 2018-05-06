package com.example.mperminov.guardiannewsfeed;

/**
 * Custom class for article from Guardian news site
 */

public class Story {
    private String mArticleTitle;
    private String mSection;
    private String mAuthor = null;
    private String mDate;
    private String mURL;

    public Story(String articleTitle, String section, String url, String date) {
        mArticleTitle = articleTitle;
        mSection = section;
        mDate = date;
        mURL = url;
    }

    public Story(String articleTitle, String section, String url, String date, String author) {
        mArticleTitle = articleTitle;
        mSection = section;
        mAuthor = author;
        mDate = date;
        mURL = url;
    }

    public String getmArticleTitle() {
        return mArticleTitle;
    }

    public String getmSection() {
        return mSection;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmURL() {
        return mURL;
    }

    public String getmDate() {
        return mDate;
    }
}
