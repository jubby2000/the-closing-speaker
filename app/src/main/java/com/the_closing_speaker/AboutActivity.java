package com.the_closing_speaker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AboutActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int QUOTES_LOADER = 0;
    private TextView mTotalAuthors;
    private TextView mTotalTopics;
    private TextView mTotalQuotes;
    private Button mDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTotalAuthors = (TextView) findViewById(R.id.total_authors_text_view);
        mTotalTopics = (TextView) findViewById(R.id.total_topics_text_view);
        mTotalQuotes = (TextView) findViewById(R.id.total_quotes_text_view);
        mDeleteButton = (Button) findViewById(R.id.delete_account_button);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle quoteArgs = new Bundle();
        quoteArgs.putString("uri", ExternalDbContract.QuoteEntry.CONTENT_URI.toString());
        getSupportLoaderManager().initLoader(QUOTES_LOADER, quoteArgs, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = Uri.parse(args.getString("uri"));

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this);
        dbOpenHelper.openDataBase();

        //TODO fix this. I think returning all columns is slowing things down.
        switch (id) {
            case QUOTES_LOADER:
                return new CursorLoader(
                        this,   // Parent activity context
                        uri,        // Uri to query
                        null,     // Projection to return (all columns)
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case QUOTES_LOADER:
                if (cursor != null) {
                    String quoteTotal = "Total number of quotes: "
                            + NumberFormat.getNumberInstance(Locale.US).format(cursor.getCount());
                    mTotalQuotes.setText(quoteTotal);

                    List<String> authors = new ArrayList<>();
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        if (cursor.getString(3) == null) {
                            String fullName = cursor.getString(1) + " " + cursor.getString(2);
                            if (!authors.contains(fullName)) {
                                authors.add(fullName);
                            }
                        } else {
                            if (!authors.contains(cursor.getString(3))) {
                                authors.add(cursor.getString(3));
                            }
                        }
                    }

                    String authorTotal = "Total number of authors: "
                            + NumberFormat.getNumberInstance(Locale.US).format(authors.size());
                    mTotalAuthors.setText(authorTotal);

                    List<String> topics = new ArrayList<>();
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        if (cursor.getString(5) != null) {
                            String topic = cursor.getString(5);
                            if (!topics.contains(topic)) {
                                topics.add(topic);
                            }
                        }
                    }
                    String topicTotal = "Total number of topics: "
                            + NumberFormat.getNumberInstance(Locale.US).format(topics.size());
                    mTotalTopics.setText(topicTotal);

                }
                break;
        }

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CharSequence deleteIt = "Nuke it";
                CharSequence dontDeleteIt = "Cancel";
                CharSequence deleteMessage = "You're about to delete your account and all associated activity - this CANNOT be recovered. Proceed?";
                CharSequence deleteMessageTitle = "Account Deletion";


                DialogInterface.OnClickListener deleteItListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        v.setId(R.id.delete_account_button);
                        onDeleteClick(v);
                    }
                };


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setPositiveButton(deleteIt, deleteItListener);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setMessage(deleteMessage);
                alertDialogBuilder.setTitle(deleteMessageTitle);
                alertDialogBuilder.setNegativeButton(dontDeleteIt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onDeleteClick(final View v) {
        if(v.getId() == R.id.delete_account_button) {
            FirebaseAuth.getInstance()
                    .getCurrentUser().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Deletion succeeded
                                startActivity(new Intent(AboutActivity.this, FirebaseLoginActivity.class));
                                finish();
                            } else {
                                Snackbar.make(v, "Unable to delete your account - please try again", Snackbar.LENGTH_LONG).show();
                                // Deletion failed
                            }
                        }
                    });
        }
    }
}
