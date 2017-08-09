package com.the_closing_speaker;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by jacob on 5/4/16.
 */
public class RecentSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = ExternalDbContract.CONTENT_AUTHORITY;

    public static final int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}