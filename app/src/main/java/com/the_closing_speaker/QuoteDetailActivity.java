package com.the_closing_speaker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

public class QuoteDetailActivity extends AppCompatActivity {

    public static final String LOG_TAG = QuoteDetailActivity.class.getSimpleName();
    boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Transition fade = new Fade();
//            fade.excludeTarget(android.R.id.statusBarBackground, true);
//            fade.excludeTarget(android.R.id.navigationBarBackground, true);
//            getWindow().setEnterTransition(fade);
//            getWindow().setExitTransition(fade);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            final View decor = getWindow().getDecorView();
            decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override public boolean onPreDraw() {
                    decor.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startPostponedEnterTransition();
                    }
                    return true;
                }
            });
        }

        final Context context = this;
        String author = getIntent().getStringExtra("author");
        CharSequence quote = getIntent().getCharSequenceExtra("quote");
        String reference = getIntent().getStringExtra("reference");
        final int position = getIntent().getIntExtra("position", 0);
        final long id = getIntent().getLongExtra("id", 0);
        isFavorite = getIntent().getBooleanExtra("favorite", false);
        Log.v(LOG_TAG, String.valueOf(id));

        final TextView authorText = (TextView) findViewById(R.id.detail_author_view);
        final TextView quoteText = (TextView) findViewById(R.id.detail_quote_view);
        final TextView referenceText = (TextView) findViewById(R.id.detail_reference_view);
        final ImageButton favoriteButton = (ImageButton) findViewById(R.id.detail_favorite_button);

        Log.v(LOG_TAG, Boolean.toString(isFavorite));
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            favoriteButton.getDrawable()
                    .mutate()
                    .setColorFilter(ContextCompat.getColor(this,
                            R.color.favorite), PorterDuff.Mode.SRC_ATOP);
        }

        if (!isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            favoriteButton.getDrawable().mutate();
        }

        ImageButton copyButton = (ImageButton) findViewById(R.id.detail_copy_button);
        ImageButton shareButton = (ImageButton) findViewById(R.id.detail_share_button);

        authorText.setText(author);
        quoteText.setText(quote);
        referenceText.setText(reference);

        //Assuming no clicks on the favorites button, we'll assume there's no change in the
        //favorites buttons and pass back 'canceled', otherwise the click listeners will
        //override this
        final Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String copyFullQuote = quoteText.getText().toString()
                        + "\n- " + authorText.getText().toString()
                        + ", " + referenceText.getText().toString();
                ClipboardManager clipboard = (ClipboardManager)
                        view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Quote", copyFullQuote);
                clipboard.setPrimaryClip(clip);

                final Snackbar snackbar = Snackbar.make(quoteText, "Copied to clipboard", Snackbar.LENGTH_SHORT);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String copyFullQuote = quoteText.getText().toString()
                        + "\n- " + authorText.getText().toString()
                        + ", " + referenceText.getText().toString();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, copyFullQuote);
                startActivity(Intent.createChooser(sharingIntent, "Share this quote using"));
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentValues values = new ContentValues();
                if (!isFavorite) {
                    values.put(ExternalDbContract.QuoteEntry.FAVORITE, "true");
                    favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                    favoriteButton.getDrawable()
                            .mutate()
                            .setColorFilter(ContextCompat.getColor(context,
                                    R.color.favorite), PorterDuff.Mode.SRC_ATOP);
                    isFavorite = true;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", isFavorite);
                    returnIntent.putExtra("position", position);
                    setResult(Activity.RESULT_OK, returnIntent);
                } else {
                    values.put(ExternalDbContract.QuoteEntry.FAVORITE, "false");
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    favoriteButton.getDrawable().mutate();
                    isFavorite = false;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", isFavorite);
                    returnIntent.putExtra("position", position);
                    setResult(Activity.RESULT_OK, returnIntent);
                }

                UpdateDataTask updateTask =
                        new UpdateDataTask(getApplicationContext());
                updateTask.execute(ExternalDbContract.QuoteEntry.TABLE_NAME, values, "_id="
                        + id, null);


            }
        });



    }

}
