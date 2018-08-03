package com.the_closing_speaker;

import android.content.Context;
import android.content.Intent;
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
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
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
public class DetailActivityFragment extends Fragment {

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
    String mUserId;
    String mTopicExtra;
    TickerView mTickerView;

    public DetailActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();
        mQuoteRef = mRootRef.child("quotes");
        backgroundColor = ContextCompat.getColor(getContext(), com.the_closing_speaker.R.color.background);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mTopicExtra = getActivity().getIntent().getStringExtra("Topic");
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
                    String favorite = String.valueOf(data.child("Favorite").child(mUserId).getValue());
                    String favoriteCount = String.valueOf(data.child("Favorite").getChildrenCount());
                    String quoteKey = data.getKey();
                    boolean isFavorite;
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

                    if (favorite.equals("true")) {
                        isFavorite = true;
                    } else {
                        isFavorite = false;
                    }
                    count++;
                    if (topic.equals(mTopicExtra)) {
                        section.add(new HeartCardItem(backgroundColor, count, onClickListener,
                                onFavoriteListener, fullAuth, quote, fullRef, quoteKey,
                                isFavorite, favoriteCount));

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
//        if (counter > 0) {
//            mAdapter.notifyItemChanged(DetailActivity.getFavoritePosition());
//        }
//        counter++;
    }

    private Handler handler = new Handler();
    private HeartCardItem.OnClickListener onClickListener = new HeartCardItem.OnClickListener() {
        @Override
        public void onClick(HeartCardItem item, int position) {
            Intent intent = new Intent(DetailActivityFragment.this.getActivity(), QuoteDetailActivity.class);
            intent.putExtra("key", item.getQuoteKey());
            intent.putExtra("author", item.getAuthor());
            intent.putExtra("quote", item.getQuote());
            intent.putExtra("reference", item.getReference());
            intent.putExtra("position", position);
            intent.putExtra("favorite", item.getIsFavorite());
            startActivity(intent);
        }
    };
    private HeartCardItem.OnFavoriteListener onFavoriteListener = new HeartCardItem.OnFavoriteListener() {
        @Override
        public void onFavorite(final HeartCardItem item, final boolean favorite) {

            final DatabaseReference favoriteRef = mQuoteRef.child(item.getQuoteKey() + "/Favorite/");
            final DatabaseReference userRef = favoriteRef.child(mUserId);
            final Map<String, String> favoriteMap = new HashMap<>();
            favoriteMap.put(mUserId, "true");

            Log.v("LOG_TAG", "favorite is " + favorite);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    item.setFavorite(favorite);
                    item.notifyChanged(HeartCardItem.FAVORITE);

                    favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mUserId)) {
                                userRef.removeValue();
//                                item.setIsFavorite(false);
//                                item.setIsFavorite(false);
                            } else {
                                favoriteRef.setValue(favoriteMap);
//                                item.setIsFavorite(true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


//                    item.setFavorite(favorite);
//                    item.notifyChanged(HeartCardItem.FAVORITE);
                }
            }, 0);
        }
    };

}
