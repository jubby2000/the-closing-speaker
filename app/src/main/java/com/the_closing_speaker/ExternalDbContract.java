package com.the_closing_speaker;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jacob on 3/24/16.
 */
public final class ExternalDbContract {

    public static final String DB_NAME = "quotes.db";
    public static final int DB_VERSION = 3;

    public static final String CONTENT_AUTHORITY = "com.the_closing_speaker.MyContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    private ExternalDbContract() {}

    public static abstract class QuoteEntry implements BaseColumns {

        public static final String TABLE_NAME = "quotes";

        public static final String QUOTE_ID = "_id";
        public static final String AUTHOR_FIRST_NAME = "Author First Name";
        public static final String AUTHOR_LAST_NAME = "Author Last Name";
        public static final String AUTHOR_GROUP_NAME = "Author Group Name";
        public static final String QUOTE = "Quote";
        public static final String TOPIC = "Topic";
        public static final String REFERENCE = "Reference";
        public static final String DATE = "Date";
        public static final String PAGE_NUMBER = "Page Number";
        public static final String POPULARITY = "Popularity";
        public static final String FAVORITE = "Favorite";
        public static final String USER_SUBMITTED = "User Submitted";
        public static final String FLAGGED = "Flagged";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        ExternalDbContract.BASE_CONTENT_URI,
                        TABLE_NAME);

        //TODO Update this with the real app name
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.com.the_closing_speaker.MyContentProvider." + TABLE_NAME;

        //TODO Update this with the real app name
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.com.the_closing_speaker.MyContentProvider." + TABLE_NAME;
        /**
         * A projection of all columns
         * in the items table.
         */
        public static final String[] PROJECTION_ALL =
                {_ID, TOPIC, AUTHOR_FIRST_NAME, AUTHOR_LAST_NAME, AUTHOR_GROUP_NAME};
        /**
         * The default sort order for
         * queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT =
                TOPIC + " ASC";
    }

}
