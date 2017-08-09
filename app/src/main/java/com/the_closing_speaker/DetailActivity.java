package com.the_closing_speaker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class DetailActivity extends AppCompatActivity implements DetailActivityFragment.OnFragmentInteractionListener{


    public static Boolean mIsFavorite;
    public static int mFavoritePosition;
    // Remove the below line after defining your own ad unit ID.
    //TODO take care of this
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(getApplicationContext(), this.getString(R.string.banner_ad_unit_id_live));

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            Slide slide = new Slide(Gravity.END);
//            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
//                    android.R.interpolator.fast_out_slow_in));
//            slide.setDuration(300);
//            slide.excludeTarget(android.R.id.statusBarBackground, true);
//            slide.excludeTarget(android.R.id.navigationBarBackground, true);
//            getWindow().setEnterTransition(slide);
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setEnterTransition(fade);
//
        }

        setContentView(R.layout.activity_detail);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);

//        AdRequest adRequest = new AdRequest.Builder()
//                .setRequestAgent("android_studio:ad_template")
//                //TODO Remove this later
//                .addTestDevice(getString(R.string.device_id))
//                .build();
//        if (adView != null) {
//            adView.loadAd(adRequest);
//        }

        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
//        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, new DetailActivityFragment(), "detail")
                .commitAllowingStateLoss();

        String topicName = getIntent().getStringExtra("Topic");
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        if (topicName != null && getSupportActionBar() != null) {
            TextView title = (TextView) findViewById(R.id.detail_toolbar_title);
            title.setText(topicName);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.v(LOG_TAG, "I made it to the request code.");
            if(resultCode == Activity.RESULT_OK){
                int position = data.getIntExtra("position", 0);
                mFavoritePosition = position;
                Log.v(LOG_TAG, String.valueOf(position));
                boolean isFavorite = data.getBooleanExtra("result", false);
                mIsFavorite = isFavorite;
                Log.v(LOG_TAG, "Position is: " + position + ". And favorite status is: " + isFavorite);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mIsFavorite = null;
                mFavoritePosition = -1;
            }
        }
    }

    public static Boolean getIsFavorite() {
        return mIsFavorite;
    }

    public static int getFavoritePosition() {
        return mFavoritePosition;
    }

//    @Override
//    public void onRestart() {
//        super.onRestart();
//
//        int p = getFavoritePosition();
//        Boolean b = getIsFavorite();
//        if (p != -1 || b != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.detail_fragment_container, new DetailActivityFragment(), "detail")
//                    .commitAllowingStateLoss();
//        }
//    }
}
