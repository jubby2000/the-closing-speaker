package com.the_closing_speaker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.ColorRes;
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
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.the_closing_speaker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    AToZList A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    private static final int LOADER = 0;

    AToZAdapter mAdapter;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Topic> topics;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mQuoteRef;
    private DatabaseReference mRootRef;
    private int backgroundColor;

    Bundle extras;
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
//    public static MainActivityFragment newInstance() {
//        MainActivityFragment fragment = new MainActivityFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backgroundColor = ContextCompat.getColor(getContext(), com.the_closing_speaker.R.color.background);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(com.the_closing_speaker.R.layout.fragment_main, container, false);
        extras = getArguments();

        mRecyclerView = (RecyclerView) view.findViewById(com.the_closing_speaker.R.id.topic_list);
        mProgressBar = (ProgressBar) getActivity().findViewById(com.the_closing_speaker.R.id.progressBarMain);
        mProgressBar.setVisibility(View.VISIBLE);
//        SimpleDividerItemDecoration dividerItemDecoration =
//                new SimpleDividerItemDecoration(getContext());

        final List<AToZList> mAToZ;

        Bundle args = new Bundle();
        args.putString("uri", ExternalDbContract.QuoteEntry.CONTENT_URI.toString());
//        getLoaderManager().initLoader(LOADER, args, this);

        getData();

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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getData() {
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();
        mQuoteRef = mRootRef.child("quotes");
        Query topicQuery = mQuoteRef.orderByChild("Topic");

        topicQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TreeSet<String> topicSet = new TreeSet<>();
                TreeSet<String> authorSet = new TreeSet<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    topicSet.add(String.valueOf(data.child("Topic").getValue()));

                    if(!data.hasChild("Author Group Name")) {
                        authorSet.add(String.valueOf(data.child("Author Last Name").getValue()
                                + ", " + String.valueOf(data.child("Author First Name").getValue())));
                    } else {
                        authorSet.add(String.valueOf(data.child("Author Group Name").getValue()));
                    }

                }
                setData(topicSet, authorSet);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setData(TreeSet<String> topicSet, TreeSet<String> authorSet) {


        for(String i : authorSet){
            System.out.println(i);
        }

        for(String i : topicSet){
            System.out.println(i);
        }

        topics = new ArrayList<Topic>();
        TreeSet<String> alphaTopicSet = new TreeSet<>();
        TreeSet<String> alphaAuthorSet = new TreeSet<>();
        List<AToZList> aToZ;
        final ArrayList<AToZList> clone;
        A = B = C = D = E = F = G = H = I = J = K = L = M = N = O = P = Q = R = S = T = U = V = W = X = Y = Z = null;

        String firstLetter = "";


        for (String topic : topicSet) {
            firstLetter = topic.substring(0, 1);
            alphaTopicSet.add(firstLetter);
        }

        for (String author : authorSet) {
            firstLetter = author.substring(0, 1);
            alphaAuthorSet.add(firstLetter);
        }

        GroupAdapter adapter = new GroupAdapter();


        for (String a : alphaTopicSet) {
            String subtitle = "";
            int count = -1;
            for (String b : topicSet) {
                if (b.substring(0, 1).equals(a)) {
                    if (count < 0) {
                        subtitle +=b;
                    }
                    count++;
                }
            }
            subtitle+=" and ("+count+") more";
            ExpandableHeaderItem headerItem = new ExpandableHeaderItem(a, subtitle);
            ExpandableGroup group = new ExpandableGroup(headerItem);
            for (String t : topicSet) {
                if (t.substring(0, 1).equals(a)) {
                    group.add(new CardItem(backgroundColor, t));
                }
            }
            adapter.add(group);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        mProgressBar.setVisibility(View.GONE);
    }



    private ArrayList<Topic> newGetTopics(String firstLetter, TreeSet<String> topicSet) {


        ArrayList<Topic> topicsByLetter = new ArrayList<Topic>();
        TreeSet<Topic> alphaTopics = new TreeSet<>();

        for (String topic : topicSet) {
            if (topic.substring(0, 1).equals(firstLetter)) {
                alphaTopics.add(new Topic(topic));
            }
        }

        topicsByLetter.addAll(alphaTopics);

        return topicsByLetter;
    }

    private ArrayList<Topic> newAddAuthors(String firstLetter, TreeSet<String> authorSet) {

        ArrayList<Topic> authors = new ArrayList<Topic>();
//        SortedSet<Topic> tempAuthors = new TreeSet<Topic>();

        TreeSet<Topic> alphaAuthors = new TreeSet<>();

        for (String author : authorSet) {
            if (author.substring(0, 1).equals(firstLetter)) {
                alphaAuthors.add(new Topic(author));
            }
        }

        authors.addAll(alphaAuthors);

        return authors;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = Uri.parse(args.getString("uri"));

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(getContext());
        dbOpenHelper.openDataBase();




        //TODO fix this. I think returning all columns is slowing things down.
        switch (id) {
            case LOADER:
                if(extras == null) {
                // Returns a new CursorLoader
                    return new CursorLoader(
                            getActivity(),   // Parent activity context
                            uri,        // Uri to query
                            new String[]{ExternalDbContract.QuoteEntry.TOPIC},     // Projection to return (topics column)
                            null,            // No selection clause
                            null,            // No selection arguments
                            null             // Default sort order
                    );
                } else {
                    return new CursorLoader(
                            getActivity(),   // Parent activity context
                            uri,        // Uri to query
                            null,     // Projection to return (all columns)
                            null,            // No selection clause
                            null,            // No selection arguments
                            null             // Default sort order
                    );
                }

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "onLoadFinished begin");

        switch (loader.getId()) {
            case LOADER:
                topics = new ArrayList<Topic>();
                List<AToZList> aToZ;
                final ArrayList<AToZList> clone;
//                Cursor cursor;
//                AToZList A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;
                A = B = C = D = E = F = G = H = I = J = K = L = M = N = O = P = Q = R = S = T = U = V = W = X = Y = Z = null;

                String firstLetter = "";


                try {
                    if (cursor != null) {
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            int currentPosition = cursor.getPosition();
                            //In this scenario, Authors was no clicked, thus the assignment will be to topics
                            //The topicCursor will be iterating through the topics column
                            if (extras == null) {
                                if (cursor.getString(0) != null) {
                                    firstLetter = cursor.getString(0).substring(0, 1);
                                }

//                            Log.v(LOG_TAG, "First letter is: " + firstLetter);

                                //Else, getArguments isn't empty, which means we now need to know how to deal with Authors
                                //Check if group name is empty
                            } else if (cursor.getString(3) == null) {
                                if (cursor.getString(2) != null) {
                                    firstLetter = cursor.getString(2).substring(0, 1);
                                }

//                            Log.v(LOG_TAG, cursor.getString(2) + " " + firstLetter);

                                //Check if group name starts with The, and exclude it from firstLetter selection
                            } else if (cursor.getString(3) != null && cursor.getString(3).substring(0, 4).equals("The ")) {
                                firstLetter = cursor.getString(3).substring(4, 5);
//                            Log.v(LOG_TAG, cursor.getString(3) + " " + firstLetter);
                            }

                            setUpArtistAndTopicsByLetter(cursor, currentPosition, firstLetter);
                        }
                    }

                } finally {
                    aToZ = Arrays.asList(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z);

//            aToZ.removeAll(Collections.singleton(null));

                    clone = new ArrayList<AToZList>();
                    clone.addAll(aToZ);
                    clone.removeAll(Collections.<AToZList>singleton(null));

                }

                if (cursor != null) {
                    cursor.close();
                }


                mAdapter = new AToZAdapter(getContext(), clone);

                mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                    @Override
                    public void onListItemExpanded(int position) {
                        AToZList expandedList = clone.get(position);
                    }

                    @Override
                    public void onListItemCollapsed(int position) {
                        AToZList collapsedList = clone.get(position);
                    }
                });


                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mAdapter);

        }
        Log.v(LOG_TAG, "onLoadFinished end");
        mProgressBar.setVisibility(View.GONE);
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

    private void setUpArtistAndTopicsByLetter (Cursor cursor, int currentPosition, String firstLetter) {
        switch (firstLetter) {
            case "A":
                if (A == null) {
                    if (extras == null) {
                        A = new AToZList("A", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
//                                        break;
                    } else {
                        A = new AToZList("A", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
//                                        break;
                    }

                    //subText.setText(A.getChildItemList());
                    //aToZ.add(A);
                }
                break;
            case "B":
                if (B == null) {
                    if (extras == null) {
                        B = new AToZList("B", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        B = new AToZList("B", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                    //aToZ.add(B);
                }
                break;
            case "C":
                if (C == null) {
                    if (extras == null) {
                        C = new AToZList("C", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        C = new AToZList("C", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "D":
                if (D == null) {
                    if (extras == null) {
                        D = new AToZList("D", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        D = new AToZList("D", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "E":
                if (E == null) {
                    if (extras == null) {
                        E = new AToZList("E", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        E = new AToZList("E", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "F":
                if (F == null) {
                    if (extras == null) {
                        F = new AToZList("F", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        F = new AToZList("F", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "G":
                if (G == null) {
                    if (extras == null) {
                        G = new AToZList("G", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        G = new AToZList("G", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "H":
                if (H == null) {
                    if (extras == null) {
                        H = new AToZList("H", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        H = new AToZList("H", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "I":
                if (I == null) {
                    if (extras == null) {
                        I = new AToZList("I", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        I = new AToZList("I", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "J":
                if (J == null) {
                    if (extras == null) {
                        J = new AToZList("J", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        J = new AToZList("J", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "K":
                if (K == null) {
                    if (extras == null) {
                        K = new AToZList("K", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        K = new AToZList("K", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "L":
                if (L == null) {
                    if (extras == null) {
                        L = new AToZList("L", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        L = new AToZList("L", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "M":
                if (M == null) {
                    if (extras == null) {
                        M = new AToZList("M", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        M = new AToZList("M", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "N":
                if (N == null) {
                    if (extras == null) {
                        N = new AToZList("N", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        N = new AToZList("N", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "O":
                if (O == null) {
                    if (extras == null) {
                        O = new AToZList("O", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        O = new AToZList("O", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "P":
                if (P == null) {
                    if (extras == null) {
                        P = new AToZList("P", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        P = new AToZList("P", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "Q":
                if (Q == null) {
                    if (extras == null) {
                        Q = new AToZList("Q", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        Q = new AToZList("Q", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "R":
                if (R == null) {
                    if (extras == null) {
                        R = new AToZList("R", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        R = new AToZList("R", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "S":
                if (S == null) {
                    if (extras == null) {
                        S = new AToZList("S", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        S = new AToZList("S", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "T":
                if (T == null) {
                    if (extras == null) {
                        T = new AToZList("T", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        T = new AToZList("T", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "U":
                if (U == null) {
                    if (extras == null) {
                        U = new AToZList("U", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        U = new AToZList("U", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "V":
                if (V == null) {
                    if (extras == null) {
                        V = new AToZList("V", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        V = new AToZList("V", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "W":
                if (W == null) {
                    if (extras == null) {
                        W = new AToZList("W", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        W = new AToZList("W", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "X":
                if (X == null) {
                    if (extras == null) {
                        X = new AToZList("X", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        X = new AToZList("X", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "Y":
                if (Y == null) {
                    if (extras == null) {
                        Y = new AToZList("Y", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        Y = new AToZList("Y", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;
            case "Z":
                if (Z == null) {
                    if (extras == null) {
                        Z = new AToZList("Z", getTopics(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    } else {
                        Z = new AToZList("Z", addAuthors(cursor, firstLetter));
                        cursor.moveToPosition(currentPosition);
                    }
                }
                break;

        }
    }


    private ArrayList<Topic> getTopics(Cursor childTopicCursor, String firstLetter) {


        ArrayList<Topic> topicsByLetter = new ArrayList<Topic>();
        LinkedHashSet<Topic> tempTopicsByLetter = new LinkedHashSet<Topic>();

        if (childTopicCursor != null) {
            for (childTopicCursor.moveToFirst(); !childTopicCursor.isAfterLast(); childTopicCursor.moveToNext()) {
//                Log.v(LOG_TAG, "Duplicate check: " + childTopicCursor.getString(0));
                if (childTopicCursor.getString(0) != null && childTopicCursor.getString(0)
                        .substring(0, 1).equals(firstLetter)) {
                    topicsByLetter.add(new Topic(childTopicCursor.getString(0)));

                }
            }

            //Necessary to use a linkedhashset (see above) to not allow duplicate topics and also keep them alphabetical
            tempTopicsByLetter.addAll(topicsByLetter);
            topicsByLetter.clear();
            topicsByLetter.addAll(tempTopicsByLetter);

//            childTopicCursor.close();
        }
        return topicsByLetter;
    }

    private ArrayList<Topic> addAuthors(Cursor authorCursor, String firstLetter) {
//        Cursor authorCursor = getContext().getContentResolver()
//                .query(ExternalDbContract.QuoteEntry.CONTENT_URI,
//                        null,
//                        null,
//                        null,
//                        null);

        ArrayList<Topic> authors = new ArrayList<Topic>();
//        SortedSet<Topic> tempAuthors = new TreeSet<Topic>();

        if(authorCursor != null) {
            for (authorCursor.moveToFirst(); !authorCursor.isAfterLast(); authorCursor.moveToNext()) {

                String firstName = authorCursor.getString(1);
                String lastName = authorCursor.getString(2);
                String groupName = authorCursor.getString(3);
                String lastNameFirst = lastName + ", " + firstName;

                //Check to make sure this isn't a group authored quote
                if (groupName == null) {
                    if (lastName != null) {
                        if (lastName.substring(0, 1).equals(firstLetter)) {
                            authors.add(new Topic(lastNameFirst));
                        }
                    }

                } else {
                    //If it is, check that it starts with The and compensate
                    if (groupName.substring(0, 4).equals("The ")) {
                        if (groupName.substring(4, 5).equals(firstLetter)) {
                            authors.add(new Topic(groupName.substring(4)
                                    + ", " + groupName.substring(0, 3)));
                        }

                    } else {
                        //Otherwise just add the group name
                        if (groupName.substring(0, 1).equals(firstLetter)) {
                            authors.add(new Topic(groupName));
                        }
                    }
                }
            }

//            tempAuthors.addAll(authors);

            TreeSet<Topic> sorter = new TreeSet<>();
            sorter.addAll(authors);


            authors.clear();
            authors.addAll(sorter);

//            authorCursor.close();
        }
        return authors;
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

}
