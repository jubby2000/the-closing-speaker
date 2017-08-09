package com.the_closing_speaker;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jacob on 3/17/16.
 */
public class DetailCardViewAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private final String LOG_TAG = DetailCardViewAdapter.class.getSimpleName();

    List<Quote> quotes;
    Context context;
    String mBold;
    String mItalics;
    String mUnderline;
    String mQuotationMark;
    int AD_TYPE = 0;
    int CONTENT_TYPE = 1;

    ContentValues values = new ContentValues();

    DetailCardViewAdapter(Context context, List<Quote> quotes) {

        this.context = context;
        this.quotes = quotes;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        mBold = context.getResources().getString(R.string.format_text_bold);
        mItalics = context.getResources().getString(R.string.format_text_italics);
        mUnderline = context.getResources().getString(R.string.format_text_underline);
        mQuotationMark = context.getResources().getString(R.string.format_text_quotation_mark);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {

        Log.v(LOG_TAG, "onBind is called.");
        String authorConcat = quotes.get(holder.getAdapterPosition()).firstName
                + " " + quotes.get(holder.getAdapterPosition()).lastName;
        String sourceConcat;

        //Check if a date exists, and if it does append it to the reference, if not, don't.
        if (quotes.get(holder.getAdapterPosition()).date != null){
            if (quotes.get(holder.getAdapterPosition()).pageNumber != null) {
                sourceConcat = quotes.get(holder.getAdapterPosition()).reference
                        + ", " + quotes.get(holder.getAdapterPosition()).date
                        + ", " + quotes.get(holder.getAdapterPosition()).pageNumber;
            } else {
                sourceConcat = quotes.get(holder.getAdapterPosition()).reference
                        + ", " + quotes.get(holder.getAdapterPosition()).date;
            }
        } else {
            if (quotes.get(holder.getAdapterPosition()).pageNumber != null) {
                sourceConcat = quotes.get(holder.getAdapterPosition()).reference
                        + ", " + quotes.get(holder.getAdapterPosition()).pageNumber;
            } else {
                sourceConcat = quotes.get(holder.getAdapterPosition()).reference;
            }

        }

        //Check if a group author name exists, it it does use it, otherwise, use the concatenated author first and last name.
        if (quotes.get(holder.getAdapterPosition()).groupName != null) {
            holder.mAuthorView.setText(quotes.get(holder.getAdapterPosition()).groupName);
        } else {
            holder.mAuthorView.setText(authorConcat);
        }

        // To remove special characters in the quoted text that were inserted to format the text at this stage.
        CharSequence text = quotes.get(holder.getAdapterPosition()).quote.replace(mQuotationMark, "\"");
        sourceConcat = sourceConcat.replace(mQuotationMark, "\"");


        text = setSpanBetweenTokens(text, mBold);
        text = setSpanBetweenTokens(text, mItalics);
        text = setSpanBetweenTokens(text, mUnderline);

        holder.mQuoteView.setText(text);
        holder.mReferenceView.setText(sourceConcat);
        long id = quotes.get(holder.getAdapterPosition()).id;
        boolean isFavorite = quotes.get(holder.getAdapterPosition()).favorite;
        holder.mCardView.setTag(R.id.databaseId, id);
        holder.mFavorite.setTag(isFavorite);
        holder.mCardView.setTag(R.id.adapterPosition, position);

        if (DetailActivity.getIsFavorite() != null && DetailActivity.getFavoritePosition() != -1) {
            if (holder.getAdapterPosition() == DetailActivity.getFavoritePosition()) {
                quotes.get(holder.getAdapterPosition()).favorite = DetailActivity.getIsFavorite();
            }
        }

        //Set image as Favorite if already exists in the database
        if (quotes.get(holder.getAdapterPosition()).favorite
//                && (holder.getAdapterPosition() != DetailActivity.getFavoritePosition())
                ) {
            holder.mFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
            holder.mFavorite.getDrawable()
                    .mutate()
                    .setColorFilter(ContextCompat.getColor(context,
                            R.color.favorite), PorterDuff.Mode.SRC_ATOP);
        } else if (!quotes.get(holder.getAdapterPosition()).favorite
//                || (holder.getAdapterPosition() != DetailActivity.getFavoritePosition())
                ){
            holder.mFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.mFavorite.getDrawable()
                    .mutate()
                    .setColorFilter(ContextCompat.getColor(context,
                            R.color.gray), PorterDuff.Mode.SRC_ATOP);
        }

//        if (DetailActivity.getIsFavorite() != null && DetailActivity.getFavoritePosition() != -1) {
//            if (holder.getAdapterPosition() == DetailActivity.getFavoritePosition()) {
//                if (DetailActivity.getIsFavorite()) {
//                    holder.mFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
//                    holder.mFavorite.getDrawable()
//                            .mutate()
//                            .setColorFilter(ContextCompat.getColor(context,
//                                    R.color.favorite), PorterDuff.Mode.SRC_ATOP);
//                    holder.mFavorite.setTag(DetailActivity.getIsFavorite());
//                } else if (!DetailActivity.getIsFavorite()) {
//                    holder.mFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
//                    holder.mFavorite.getDrawable()
//                            .mutate()
//                            .setColorFilter(ContextCompat.getColor(context,
//                                    R.color.gray), PorterDuff.Mode.SRC_ATOP);
//                    holder.mFavorite.setTag(DetailActivity.getIsFavorite());
//                }
//            }
//        }



        //Switch image on click with a Snackbar to explain what's happening
        holder.mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!quotes.get(holder.getAdapterPosition()).favorite) {
                    holder.mFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    holder.mFavorite.getDrawable()
                            .mutate()
                            .setColorFilter(ContextCompat.getColor(v.getContext(),
                                    R.color.favorite), PorterDuff.Mode.SRC_ATOP);

                    values.put(ExternalDbContract.QuoteEntry.FAVORITE, "true");

                    UpdateDataTask updateTask =
                            new UpdateDataTask(context);
                    updateTask.execute(ExternalDbContract.QuoteEntry.TABLE_NAME, values, "_id="
                            + quotes.get(holder.getAdapterPosition()).id, null);

//                    db.update(ExternalDbContract.QuoteEntry.TABLE_NAME, values, "_id="
//                            + quotes.get(position).id, null);


                    Snackbar snackbar = Snackbar.make(v,
                            v.getContext().getString(R.string.added_to_favorites_snackbar),
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    quotes.get(holder.getAdapterPosition()).favorite = true;
//                    addItem(position);
                    notifyDataSetChanged();
                } else {
                    holder.mFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    holder.mFavorite.getDrawable()
                            .mutate()
                            .setColorFilter(ContextCompat.getColor(v.getContext(),
                                    R.color.gray), PorterDuff.Mode.SRC_ATOP);

                    values.put(ExternalDbContract.QuoteEntry.FAVORITE, "false");

                    UpdateDataTask updateTask =
                            new UpdateDataTask(v.getContext());
                    updateTask.execute(ExternalDbContract.QuoteEntry.TABLE_NAME, values, "_id="
                            + quotes.get(holder.getAdapterPosition()).id, null);

//                    db.update(ExternalDbContract.QuoteEntry.TABLE_NAME, values, "_id="
//                            + quotes.get(position).id, null);

                    Snackbar snackbar = Snackbar.make(v,
                            v.getContext().getString(R.string.removed_from_favorites_snackbar),
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    quotes.get(holder.getAdapterPosition()).favorite = false;
//                    removeItem(position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void addItem(int position) {
        if (position > getItemCount()) return;

        quotes.add(position, new Quote(quotes.get(position).id,
                quotes.get(position).firstName,
                quotes.get(position).lastName,
                quotes.get(position).groupName,
                quotes.get(position).topic,
                quotes.get(position).quote,
                quotes.get(position).reference,
                quotes.get(position).date,
                quotes.get(position).pageNumber,
                quotes.get(position).favorite));
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position >= getItemCount()) return;

        quotes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 4 == 0) {
            return AD_TYPE;
        } else {
            return CONTENT_TYPE;
        }
    }

    @Override
    public int getItemCount() {

        return quotes.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.v(LOG_TAG, "OnAttached just ran.");
    }

    public static CharSequence setSpanBetweenTokens(CharSequence text, String token)
    {
        int tokenLen = token.length();
        int start = text.toString().indexOf(token) + tokenLen;
        int end = text.toString().indexOf(token, start);


        //Ensure that multiple tokens within text will be formatted, not just one instance or the last.
        while (start > -1 && end > -1)
        {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

            switch(token){
                case "###":
                    spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
                    break;
                case "***":
                    spannableStringBuilder.setSpan(new StyleSpan(Typeface.ITALIC), start, end, 0);
                    break;
                case "___":
                    spannableStringBuilder.setSpan(new UnderlineSpan(), start, end, 0);
            }

            // Delete the tokens before and after the span
            spannableStringBuilder.delete(end, end + tokenLen);
            spannableStringBuilder.delete(start - tokenLen, start);
            text = spannableStringBuilder;

            start = text.toString().indexOf(token, end - tokenLen - tokenLen) + tokenLen;
            end = text.toString().indexOf(token, start);
        }

        return text;
    }

}
