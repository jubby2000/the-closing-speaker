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

public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

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
//        mDatabase.setPersistenceEnabled(true);
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


        TreeSet<String> alphaTopicSet = new TreeSet<>();
        TreeSet<String> alphaAuthorSet = new TreeSet<>();


        for (String topic : topicSet) {
            String firstLetter = topic.substring(0, 1);
            alphaTopicSet.add(firstLetter);
        }

        for (String author : authorSet) {
            String firstLetter = author.substring(0, 1);
            alphaAuthorSet.add(firstLetter);
        }

        GroupAdapter adapter = new GroupAdapter();


        if (extras == null) {
            //Run through for topics tab
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
        } else {
            //Run through for authors tab
            for (String a : alphaAuthorSet) {
                String subtitle = "";
                int count = -1;
                for (String b : authorSet) {
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
                for (String t : authorSet) {
                    if (t.substring(0, 1).equals(a)) {
                        group.add(new CardItem(backgroundColor, t));
                    }
                }
                adapter.add(group);
            }
        }


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        mProgressBar.setVisibility(View.GONE);
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

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

}
