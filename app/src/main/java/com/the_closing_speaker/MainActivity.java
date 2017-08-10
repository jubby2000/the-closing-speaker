package com.the_closing_speaker;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener
//        , NavigationView.OnNavigationItemSelectedListener
{

    final String LOG_TAG = MainActivity.class.getSimpleName();
    private BottomBar mBottomBar;
    public static String PACKAGE_NAME;
    int REQUEST_INVITE = 0;
    public static Boolean mIsFavorite;
    public static int mFavoritePosition;
    DetailCardViewAdapter mAdapter;
    MainActivityFragment mAuthorFragment;
    MainActivityFragment mTopicFragment;
    FavoritesFragment mFavoriteFragment;
    CoordinatorLayout mCoordinatorLayout;
    ProgressBar mProgressBar;
    FragmentManager mFragmentManager = getSupportFragmentManager();
    Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        mAuthorFragment = new MainActivityFragment();
        mTopicFragment = new MainActivityFragment();
        mFavoriteFragment = new FavoritesFragment();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            Fade fade = new Fade();
            Slide startSlide;
            Configuration config = getResources().getConfiguration();
            if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                startSlide = new Slide(Gravity.START);
            } else {
                startSlide = new Slide(Gravity.LEFT);
            }

            startSlide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
//
            startSlide.setDuration(300);
            startSlide.excludeTarget(android.R.id.statusBarBackground, true);
            startSlide.excludeTarget(android.R.id.navigationBarBackground, true);
//            fade.excludeTarget(android.R.id.statusBarBackground, true);
//            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(startSlide);
//            getWindow().setExitTransition(fade);
        }

        setContentView(R.layout.content_main);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBarMain);
//        mProgressBar.setVisibility(View.VISIBLE);
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.topic_bar_title));
        }

        //Add all three fragments to the same view, but hide the ones you don't want to see until click
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mTopicFragment)
                .add(R.id.fragment_container, mAuthorFragment)
                .add(R.id.fragment_container, mFavoriteFragment)
                .hide(mAuthorFragment)
                .hide(mFavoriteFragment)
                .commit();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        
        //To make this hide on scroll, adjust attach to attachShy with a CoordinatorLayout
//        mBottomBar = BottomBar.attachShy(mCoordinatorLayout,
//                findViewById(R.id.fragment_container), savedInstanceState);
//        mBottomBar.noTopOffset();
//        mBottomBar.noNavBarGoodness();
        mBottomBar.setDefaultTabPosition(1);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_authors) {

//                    mProgressBar.setVisibility(View.VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(getString(R.string.author_bar_title));
                    }

                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    Bundle args = new Bundle();
                    args.putString("Author", "Author");
                    mAuthorFragment = new MainActivityFragment();
                    mAuthorFragment.setArguments(args);
                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
//                    ft.setTransition(ft.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.fragment_container, mAuthorFragment);
//                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                    ft.commit();
                    // The user selected item number one.
                }
                if (tabId == R.id.tab_topics) {
                    // The user selected item number two.

//                    mProgressBar.setVisibility(View.VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(getString(R.string.topic_bar_title));
                    }
                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
//                    ft.setTransition(ft.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.fragment_container, mTopicFragment);
//                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                    ft.commit();
                }
                if (tabId == R.id.tab_favorites) {

//                    mProgressBar.setVisibility(View.VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(getString(R.string.favorite_bar_title));
                    }

                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
//                    ft.setTransition(ft.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.fragment_container, mFavoriteFragment);
//                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                    ft.commit();
                    // The user selected item number three.
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    mFavoriteFragment.scrollToTop();
                }
                if (tabId == R.id.tab_topics) {
                    mTopicFragment.mAdapter.collapseAllParents();
                    mTopicFragment.scrollToTop();

                }
                if (tabId == R.id.tab_authors) {
                    mAuthorFragment.mAdapter.collapseAllParents();
                    mAuthorFragment.scrollToTop();
                }
            }
        });



    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, MainActivity.class);
        return in;
    }

    public void click(View view, Intent intent) {

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(root);
        }

        Bundle bundle = options.toBundle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.startActivity(intent, bundle);
        } else {
            this.startActivity(intent);
        }

    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem shareMenuItem = menu.findItem(R.id.menu_share);
        shareMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onInviteClicked();
                return true;
            }
        });

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(
                            new ComponentName(getApplicationContext(), SearchActivity.class)));
        }

        MenuItem signOut = menu.findItem(R.id.menu_sign_out);
        signOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                View currentView = findViewById(android.R.id.content);
                currentView.setId(R.id.menu_sign_out);
                onSignOutClick(currentView);
                return true;
            }
        });


        return true;
    }

    public void onSignOutClick(View v) {
        if (v.getId() == R.id.menu_sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, FirebaseLoginActivity.class));
                            finish();
                        }
                    });
        }
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
//                .setEmailSubject(getString(R.string.invitation_email_subject))
                .setMessage(getString(R.string.invitation_message))
//                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))

                .build();

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            startActivityForResult(intent, REQUEST_INVITE);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Google Play Services Error");
            alertDialog.setMessage("Oops, it looks like you need to install or update Google Play Services to be able to share this app.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + ", resultok=" + RESULT_OK);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.v(LOG_TAG, "onActivityResult: sent invitation " + id);
                }
//                Log.v(LOG_TAG, "Hey, you just sent " + ids.length + " invites! Thank you!");
            } else {
                // Sending failed or it was canceled, show failure message to the user
//                showMessage(getString(R.string.send_failed));
                Log.v(LOG_TAG, "Sending failed.");
            }
        }

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

    public static void setIsFavorite(Boolean isFavorite) {
        mIsFavorite = isFavorite;
    }

    public static void setFavoritePosition(int favoritePosition) {
        mFavoritePosition = favoritePosition;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mBottomBar.onSaveInstanceState();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
