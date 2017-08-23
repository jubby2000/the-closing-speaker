package com.the_closing_speaker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailActivityFragment#} factory method to
 * create an instance of this fragment.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    int counter = 0;
//    private SQLiteDatabase database;
    DetailCardViewAdapter mAdapter;
    private static final int LOADER = 0;
    private int backgroundColor;
    private ArrayList<Quote> quotes;
    RecyclerView mRecyclerView;
    FirebaseDatabase mDatabase;
    DatabaseReference mRootRef;
    DatabaseReference mQuoteRef;
    GroupAdapter groupAdapter;

    public DetailActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();
        mQuoteRef = mRootRef.child("quotes");
        backgroundColor = ContextCompat.getColor(getContext(), com.the_closing_speaker.R.color.background);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_detail, container, false);

//        addQuotes();

        Bundle args = new Bundle();
        args.putString("uri", ExternalDbContract.QuoteEntry.CONTENT_URI.toString());
//        getLoaderManager().initLoader(LOADER, args, this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.quote_list);

        getData();


//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getData() {

        Query topicQuery = mQuoteRef.orderByChild("Topic");
        groupAdapter = new GroupAdapter();

        topicQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Section section = new Section();
                int count = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String topic = String.valueOf(data.child("Topic").getValue());
                    String authorFirst = String.valueOf(data.child("Author First Name").getValue());
                    String authorLast = String.valueOf(data.child("Author Last Name").getValue());
                    String authorGroup = String.valueOf(data.child("Author Group Name").getValue());
                    String quote = String.valueOf(data.child("Quote").getValue());
                    String reference = String.valueOf(data.child("Reference").getValue());
                    String pageNumber = String.valueOf(data.child("Page Number").getValue());
                    String date = String.valueOf(data.child("Date").getValue());
                    String quoteKey = data.getKey();
                    String fullRef;
                    String fullAuth;


                    if (date.equals("null") && !pageNumber.equals("null")){
                        fullRef = reference + ", " + pageNumber;
                    } else if (!date.equals("null") && pageNumber.equals("null")) {
                        fullRef = reference + ", " + date;
                    } else {
                        fullRef = reference + ", " + date + ", " + pageNumber;
                    }

                    if (authorGroup.equals("null")) {
                        fullAuth = authorFirst + " " + authorLast;
                    } else {
                        fullAuth = authorGroup;
                    }

                    count++;
                    if (topic.equals(getActivity().getIntent().getStringExtra("Topic"))) {
                        section.add(new HeartCardItem(backgroundColor, count, onFavoriteListener, fullAuth, quote, fullRef, quoteKey));

                    }

                }
                groupAdapter.add(section);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(groupAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = Uri.parse(args.getString("uri"));

        switch (id) {
            case LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        uri,        // Uri to query
                        new String[]{ExternalDbContract.QuoteEntry.QUOTE_ID,
                                "\"" + ExternalDbContract.QuoteEntry.AUTHOR_FIRST_NAME + "\"",
                                "\"" + ExternalDbContract.QuoteEntry.AUTHOR_LAST_NAME + "\"",
                                "\"" + ExternalDbContract.QuoteEntry.AUTHOR_GROUP_NAME + "\"",
                                ExternalDbContract.QuoteEntry.QUOTE,
                                ExternalDbContract.QuoteEntry.TOPIC,
                                ExternalDbContract.QuoteEntry.REFERENCE,
                                ExternalDbContract.QuoteEntry.DATE,
                                "\"" + ExternalDbContract.QuoteEntry.PAGE_NUMBER + "\"",
                                ExternalDbContract.QuoteEntry.FAVORITE},     // Projection to return (all columns)
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor quoteCursor) {

        quotes = new ArrayList<Quote>();

        String currentTopic = getActivity().getIntent().getStringExtra("Topic");

        if (quoteCursor != null) {
            quoteCursor.moveToFirst();
            if (!quoteCursor.isAfterLast()) {
                do {
                    long id = quoteCursor.getLong(0);
                    String firstName = quoteCursor.getString(1);
                    String lastName = quoteCursor.getString(2);
                    String groupName = quoteCursor.getString(3);
                    String quote = quoteCursor.getString(4);
                    String topic = quoteCursor.getString(5);
                    String reference = quoteCursor.getString(6);
                    String date = quoteCursor.getString(7);
                    String pageNumber = quoteCursor.getString(8);
//                    String popularity = quoteCursor.getString(9);
                    String strFavorite = quoteCursor.getString(9);
//                    String userSubmitted = quoteCursor.getString(11);
//                    String flagged = quoteCursor.getString(12);

                    boolean favorite = false;

                    if (strFavorite.equals("false")) {
                        favorite = false;
                    } else if (strFavorite.equals("true")) {
                        favorite = true;
                    }

                    if (currentTopic == null && favorite) {
                        quotes.add(new Quote(id, firstName, lastName, groupName, topic, quote,
                                reference, date, pageNumber, favorite));
                    }

                    if (currentTopic != null) {
                        if (currentTopic.equals(topic)) {
                            quotes.add(new Quote(id, firstName, lastName, groupName, topic, quote,
                                    reference, date, pageNumber, favorite));
                        } else if (currentTopic.equals(firstName + " " + lastName)) {
                            quotes.add(new Quote(id, firstName, lastName, groupName, topic, quote,
                                    reference, date, pageNumber, favorite));
                        } else if (currentTopic.equals(groupName)) {
                            quotes.add(new Quote(id, firstName, lastName, groupName, topic, quote,
                                    reference, date, pageNumber, favorite));
                        }
                    }


                } while (quoteCursor.moveToNext());

            }
            mAdapter = new DetailCardViewAdapter(this.getContext(), quotes);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "onResume is called.");
        super.onResume();
        if (counter > 0) {
            mAdapter.notifyItemChanged(DetailActivity.getFavoritePosition());
        }
        counter++;
    }

        private Handler handler = new Handler();
        private HeartCardItem.OnFavoriteListener onFavoriteListener = new HeartCardItem.OnFavoriteListener() {
            @Override
            public void onFavorite(final HeartCardItem item, final boolean favorite) {
                // Pretend to make a network request
//                item.setFavorite(favorite);
//                item.notifyChanged(HeartCardItem.FAVORITE);
                String email = "";
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    email = user.getEmail();
                    email = email.replace(".", ",");
                    }

                final DatabaseReference favoriteRef = mRootRef.child("favorites/" + email);
                final DatabaseReference newFavorite = favoriteRef.push();
                final Query queryRef = favoriteRef.orderByValue().equalTo(item.getQuoteKey());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!favorite) {
                            queryRef.getRef().removeValue();
                        } else {
                            newFavorite.setValue(item.getQuoteKey());
                        }
                        // Network request was successful!
                        Log.v(LOG_TAG, String.valueOf(favorite));
                        item.setFavorite(favorite);
                        item.notifyChanged(HeartCardItem.FAVORITE);
                    }
                }, 0);
            }
        };

    }
