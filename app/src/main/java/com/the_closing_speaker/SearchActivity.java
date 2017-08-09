package com.the_closing_speaker;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jacob on 4/23/16.
 */
public class SearchActivity extends ListActivity {

    private final String LOG_TAG = SearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);



//        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
//        setSupportActionBar(toolbar);
//
//        if (topicName != null && getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(topicName);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }

        handleIntent(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        String topic = listView.getItemAtPosition(position).toString();
        intent.putExtra("Topic", topic);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            String[] projection = new String[]{
                    ExternalDbContract.QuoteEntry._ID,
                    "\"" + ExternalDbContract.QuoteEntry.AUTHOR_FIRST_NAME + "\"",
                    "\"" + ExternalDbContract.QuoteEntry.AUTHOR_LAST_NAME + "\"",
                    "\"" + ExternalDbContract.QuoteEntry.AUTHOR_GROUP_NAME + "\"",
                    ExternalDbContract.QuoteEntry.TOPIC};

            ContentResolver resolver = getContentResolver();

            //Didn't realize that spaces in table column names are bad news.
            //Employing laziness by escaping quotes instead of fixing spaces.
            Cursor cursor = resolver.query(
                    ExternalDbContract.QuoteEntry.CONTENT_URI,
                    projection,
                    "LOWER(" + ExternalDbContract.QuoteEntry.TOPIC + ") LIKE LOWER(?)"
                            + " OR LOWER(\"" + ExternalDbContract.QuoteEntry.AUTHOR_FIRST_NAME + "\") LIKE LOWER(?)"
                            + " OR LOWER(\"" + ExternalDbContract.QuoteEntry.AUTHOR_LAST_NAME + "\") LIKE LOWER(?)"
                            + " OR LOWER(\"" + ExternalDbContract.QuoteEntry.AUTHOR_GROUP_NAME + "\") LIKE LOWER(?)",
                    new String[] {"%" + query + "%"},
                    null);

            List<String> searchResults = new ArrayList<String>();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    long id = cursor.getLong(0);
                    String first = cursor.getString(1);
                    String last = cursor.getString(2);
                    String group = cursor.getString(3);
                    String topic = cursor.getString(4);
                    // do something meaningful


//                    Log.v(LOG_TAG, "Group is: " + group.toLowerCase() +". And query is: " + query);
                    if (group != null && group.toLowerCase().contains(query.toLowerCase())
                            && !searchResults.contains(group)) {
                        searchResults.add(group);
                    }
                    if (first != null && first.toLowerCase().contains(query.toLowerCase())
                            && !searchResults.contains(first + " " + last)) {
                        searchResults.add(first + " " + last);
                    }

                    if (last != null && last.toLowerCase().contains(query.toLowerCase())
                            && !searchResults.contains(first + " " + last)) {
                        searchResults.add(first + " " + last);
                    }

                    if (topic != null && topic.toLowerCase().contains(query.toLowerCase())
                            && !searchResults.contains(topic)) {
                        searchResults.add(topic);
                    }

                } while (cursor.moveToNext());
            }

            Collections.sort(searchResults);

            setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults));
//
////            Intent detailIntent = new Intent(this, DetailActivity.class);
////            detailIntent.putExtra("Topic", query);
////            startActivity(detailIntent);
////            finish();
//            //TODO perform a search on this string query
            if (cursor != null) {

                //Save the previous query to be provided as a suggestion later.
                SearchRecentSuggestions suggestions =
                        new SearchRecentSuggestions(this,
                                RecentSuggestionsProvider.AUTHORITY,
                                RecentSuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);

                cursor.close();
            }
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri detailUri = intent.getData();
            String id = detailUri.getLastPathSegment();
            Intent detailsIntent = new Intent(getApplicationContext(), DetailActivity.class);
            detailsIntent.putExtra("Topic", id);
            startActivity(detailsIntent);
            finish();
        }
    }

    //Might need this later if I decide to include the search feature as a fragment.
//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }
}
