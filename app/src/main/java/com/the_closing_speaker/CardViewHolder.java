package com.the_closing_speaker;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by jacob on 3/17/16.
 */
public class CardViewHolder extends RecyclerView.ViewHolder{

    private final String LOG_TAG = CardViewHolder.class.getSimpleName();

    CardView mCardView;
    TextView mAuthorView;
    TextView mQuoteView;
    TextView mReferenceView;

    ImageButton mFavorite;
    ImageButton mShare;


    public CardViewHolder(final View cardView) {
        super(cardView);

        mCardView = (CardView) itemView.findViewById(R.id.card_view);
        mAuthorView = (TextView) itemView.findViewById(R.id.author_view);
        mQuoteView = (TextView) itemView.findViewById(R.id.quote_view);
        mReferenceView = (TextView) itemView.findViewById(R.id.reference_view);
        mFavorite = (ImageButton) itemView.findViewById(R.id.favorite_button);


        mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String copyFullQuote = mQuoteView.getText().toString() + "\n- " +
                        mAuthorView.getText().toString() + ", " + mReferenceView.getText().toString();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Quote", copyFullQuote);
                clipboard.setPrimaryClip(clip);

                final Snackbar snackbar = Snackbar.make(itemView, "Copied to clipboard", Snackbar.LENGTH_SHORT);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
//                view.playSoundEffect(SoundEffectConstants.CLICK);
                return true;
            }
        });
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long id = (long) mCardView.getTag(R.id.databaseId);
                boolean isFavorite = (boolean) mFavorite.getTag();
                int cardPosition = (int) mCardView.getTag(R.id.adapterPosition);

                //This is where the quotedetailactivity will go
                Intent intent = new Intent(v.getContext(), QuoteDetailActivity.class);
                intent.putExtra("author", mAuthorView.getText());
                intent.putExtra("quote", mQuoteView.getText());
                intent.putExtra("reference", mReferenceView.getText());
                intent.putExtra("id", id);
                intent.putExtra("favorite", isFavorite);
                intent.putExtra("position", cardPosition);
                Pair<View, String> author = Pair.create(itemView.findViewById(R.id.author_view), "author");
                Pair<View, String> quote = Pair.create((View)mQuoteView, "quote");
                Pair<View, String> reference = Pair.create((View)mReferenceView, "reference");
                Pair<View, String> favorite = Pair.create((View) mFavorite, "favorite");

                Activity activity = (Activity) v.getContext();

                Pair <View, String> navBar = null;
                Pair <View, String> statBar = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    View navigationBar = activity.findViewById(android.R.id.navigationBarBackground);
                    View statusBar = activity.findViewById(android.R.id.statusBarBackground);
                    navBar = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                    statBar = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                }

                ActivityOptionsCompat options;
                if (activity.findViewById(android.R.id.navigationBarBackground) != null) {
                    options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(activity, statBar,
                                    navBar, author, quote, reference, favorite);
                } else {
                    options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(activity, statBar,
                                    author, quote, reference, favorite);
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    v.getContext().startActivity(intent, options.toBundle());
                    ((Activity) v.getContext())
                            .startActivityForResult(intent, 1, options.toBundle());
                } else {
//                    v.getContext().startActivity(intent);
                    ((Activity) v.getContext()).startActivityForResult(intent, 1);
                }


//                String copyFullQuote = mQuoteView.getText().toString() + "\n- " +
//                        mAuthorView.getText().toString() + ", " + mReferenceView.getText().toString();
//                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
//                        v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("Quote", copyFullQuote);
//                clipboard.setPrimaryClip(clip);
//
//                final Snackbar snackbar = Snackbar.make(itemView, "Copied to clipboard", Snackbar.LENGTH_SHORT);
//                snackbar.setAction("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        snackbar.dismiss();
//                    }
//                });
//                snackbar.show();
            }
        });

        mFavorite.getDrawable()
                .mutate()
                .setColorFilter(ContextCompat.getColor(cardView.getContext(),
                        R.color.lightGray), PorterDuff.Mode.SRC_ATOP);





//        isFavorite = quotes.get(position).favorite;
//        Log.v(LOG_TAG, "Favorite position: " + String.valueOf(cardView..get(position)));


    }

}
