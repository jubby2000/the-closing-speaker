package com.the_closing_speaker;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

/**
 * Created by jacob on 6/22/17.
 */

public class GetQuoteInfo {

    private String mFirstName;

    private String mLastName;


    private String mGroupName;

    private String mTopic;


    private String mDate;

    private String mPageNumber;

    private String mReference;

    private String mQuote;

    public GetQuoteInfo() {
        // empty default constructor, necessary for Firebase to be able to deserialize quotes
    }

//SETTERS
    @PropertyName("Author First Name")
    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    @PropertyName("Author Last Name")
    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    @PropertyName("Author Group Name")
    public void setGroupName(String groupName) {
        this.mGroupName = groupName;
    }

    @PropertyName("Topic")
    public void setTopic(String topic) {
        this.mTopic = topic;
    }

    @PropertyName("Date")
    public void setDate(String date) {
        this.mDate = date;
    }

    @PropertyName("Page Number")
    public void setPageNumber(String pageNumber) {
        this.mPageNumber = pageNumber;
    }

    @PropertyName("Reference")
    public void setReference(String reference) {
        this.mReference = reference;
    }

    @PropertyName("Quote")
    public void setQuote(String quote) {
        this.mQuote = quote;
    }

//GETTERS
    @PropertyName("Author First Name")
    public String getFirstName() {
        return mFirstName;
    }

    @PropertyName("Author Last Name")
    public String getLastName() {
        return mLastName;
    }

    @PropertyName("Author Group Name")
    public String getGroupName() {
        return mGroupName;
    }

    @PropertyName("Topic")
    public String getTopic() {
        return mTopic;
    }

    @PropertyName("Date")
    public String getDate() {
        return mDate;
    }

    @PropertyName("Page Number")
    public String getPageNumber() {
        return mPageNumber;
    }

    @PropertyName("Reference")
    public String getReference() {
        return mReference;
    }

    @PropertyName("Quote")
    public String getQuote() {
        return mQuote;
    }
}
