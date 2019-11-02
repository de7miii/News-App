package com.example.newsapp;

public class Article {
    private String mSectionName;
    private String mPublicationDate;
    private String mTitle;
    private String mUrl;
    private String mAuthorName;

    public Article(String sectionName, String publicationDate, String title, String url, String authorName) {
        mSectionName = sectionName;
        mPublicationDate = publicationDate;
        mTitle = title;
        mUrl = url;
        mAuthorName = authorName;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getPublicationDate() {
        return mPublicationDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAuthorName() {
        return mAuthorName;
    }
}
