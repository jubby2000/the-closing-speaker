package com.the_closing_speaker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by jacob on 5/18/16.
 */
public class UpdateDataTask extends AsyncTask<Object, Void, Void> {
    private Context mContext;

    public UpdateDataTask(Context context) {
        this.mContext = context;
    }


//    private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
//
//    // can use UI thread here
//    protected void onPreExecute() {
//        this.dialog.setMessage("Inserting data...");
//        this.dialog.show();
//    }

    @Override
    protected Void doInBackground(Object[] params) {

        ExternalDbOpenHelper mDbHelper = new ExternalDbOpenHelper(mContext);;
        SQLiteDatabase db;

        try {
            mDbHelper.createDataBase();
        } catch (Exception e) {
            throw new Error("Unable to create database");
        }

        try {
            mDbHelper.openDataBase();
        }catch(android.database.SQLException sqle) {
            throw sqle;
        }

        db = mDbHelper.getWritableDatabase();

        String table =  (String) params[0];
        ContentValues values =  (ContentValues) params[1];
        String position =  (String) params[2];
        String[] args =  (String[]) params[3];

        db.update(table, values, position, args);


        return null;
    }
}
