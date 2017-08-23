package com.the_closing_speaker;

/**
 * Created by jacob on 8/22/17.
 */

public class User {
    public String fullName;
    public boolean admin;
    public int flaggedCount;
    public int submissionCount;

    public User(String fullName, boolean admin, int flaggedCount, int submissionCount) {
        fullName = this.fullName;
        admin = this.admin;
        flaggedCount = this.flaggedCount;
        submissionCount = this.submissionCount;
    }
}