package com.the_closing_speaker;

import android.database.Cursor;

/**
 * Created by Jacob on 9/21/2016.
 */

public interface QueryDataResponse {
    void processFinish(Cursor response);
}
