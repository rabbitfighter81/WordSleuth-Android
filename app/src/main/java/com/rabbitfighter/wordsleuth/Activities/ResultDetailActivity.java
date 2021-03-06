package com.rabbitfighter.wordsleuth.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.rabbitfighter.wordsleuth.Entries.Result;
import com.rabbitfighter.wordsleuth.R;
import com.rabbitfighter.wordsleuth.Services.BoundSearchService;
import com.rabbitfighter.wordsleuth.Utils.RobotoFontsHelper;

/**
 * App instructions. Five fragments
 *
 * @author Joshua Michael Waggoner <rabbitfighter@cryptolab.net>
 * @author Stephen Chavez <stephen.chavez12@gmail.com>
 * @version 0.2 (pre-beta)
 * @link https://github.com/rabbitfighter81/SwipeNavExample (Temporary)
 * @see 'http://developer.android.com/design/patterns/swipe-views.html'
 * @since 0.1 2015-05-23.
 */
public class ResultDetailActivity extends ActionBarActivity {

    public final static String TAG = "ResultDetailActivity";

    // Vars
    Bundle bundle;
    Result result;
    String query;
    String resultType;
    int length;
    int score_scrabble;
    int score_words;

    // TextViews
    TextView tv_title,
             tv_result_title,
             tv_result,
             tv_length_title,
             tv_length,
             tv_scrabble_title,
             tv_scrabble_points,
             tv_words_title,
             tv_words_points;


    // Service connection class.
    BoundSearchService searchService;

    // Search intent for service connection
    Intent searchIntent;

    // Whether the service is bound or not...
    boolean isBound;

    /**
     * Service Connection
     */
    public ServiceConnection connection = new ServiceConnection() {
        // when we connect to the service.
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected...");
            // Reference to our binder class
            BoundSearchService.MyLocalBinder binder = (BoundSearchService.MyLocalBinder) service;
            // Once we have access, we get the class container IBinder with cool methods.
            searchService = binder.getService();
            // Set bound to true, because we are now bound to a service
            isBound = true;
        }

        // When we disconnect from a service
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service disconnected...");
            isBound = false;
        }
    };

    /**
     * On creation of activity. Called first.
     * @param savedInstanceState - the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);

        isBound = false;

        /*
         Orientation change (TODO for other devs: for now, don't worry about orientation stuff.)
         -- It's just here in case we need it.
         -- If you wanted to do something when the orientation is set to landscape, do it here -->
        */
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            return;
        }

        // Handle the incoming intent.
        handleIntent();

        /* --- View stuff --- */
        // Title
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_result_title = (TextView) findViewById(R.id.tv_result_title);
        tv_result = (TextView) findViewById(R.id.tv_result);
        //tv_query_title = (TextView) findViewById(R.id.tv_query_title);
        //tv_query = (TextView) findViewById(R.id.tv_query);
        tv_length_title = (TextView) findViewById(R.id.tv_length_title);
        tv_length = (TextView) findViewById(R.id.tv_length);
        tv_scrabble_title = (TextView) findViewById(R.id.tv_scrabble_title);
        tv_scrabble_points = (TextView) findViewById(R.id.tv_scrabble_points);
        tv_words_title = (TextView) findViewById(R.id.tv_words_title);
        tv_words_points = (TextView) findViewById(R.id.tv_words_points);
        // Fonts
        tv_title.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_black));
        tv_result_title.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));
        tv_result.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));
        tv_length_title.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));
        tv_length.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));
        tv_scrabble_title.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));
        tv_scrabble_points.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));
        tv_words_title.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));
        tv_words_points.setTypeface(RobotoFontsHelper.getTypeface(getApplicationContext(), RobotoFontsHelper.roboto_regular));

        /* Set text */
        switch (resultType) {
            case "anagram":
                tv_title.setText(getString(R.string.txt_anagram_details));
                break;
            case "subword":
                tv_title.setText(getString(R.string.txt_subword_details));
                break;
            case "combo":
                tv_title.setText(getString(R.string.txt_combo_details));
                break;
            default:
                tv_title.setText("Result Details:");
                break;
        }

        tv_result.setText(result.getWord());
        //tv_query.setText("\"" + query + "\"");
        tv_length.setText(String.valueOf(result.getNumLetters() + " letters"));
        if (resultType.compareToIgnoreCase("combo")!=0) {
            tv_scrabble_points.setText(String.valueOf(score_scrabble) + " points");
            tv_words_points.setText(String.valueOf(score_words) + " points");
            tv_length.setText(String.valueOf(result.getNumLetters() + " letters"));
        } else {
            tv_scrabble_points.setText("N/A");
            tv_words_points.setText("N/A");
            tv_length.setText(String.valueOf(result.getNumLetters() + " letters"));
        }

        /*
        Keyboard popping up bug fix
        @see 'http://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up'
         */
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * Handle the incoming intent and assign values to vars
     */
    private void handleIntent() {
        bundle = getIntent().getExtras();
        query =  bundle.getString("query").toString();
        result = new Result(bundle.getString("result"));
        resultType = bundle.getString("resultType").toString();
        length = result.getNumLetters();
        score_scrabble = result.getPointsScrabble();
        score_words = result.getPointsWordsWithFriends();

        // Log (temporary)
        Log.i(TAG, query);
        Log.i(TAG, result.getWord());
        Log.i(TAG, resultType);
        Log.i(TAG, "Length: " + length);
        Log.i(TAG, "Scrabble(TM) score: " + score_scrabble);
        Log.i(TAG, "Words(TM) score: " + score_words);
    }

    /**
     * When the menu is created...
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu() triggered");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_result_detail, menu);

        // Note: Returns true if the menu properly inflates
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Menu items selected
     * @param item - The menu item passed in.
     * @return false to allow normal menu processing to
     *         proceed, true to consume it here.
     *         <- From the notes in superclass' docs.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected(MenuItem" + item + ")");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle item selection
        switch (id) {
            case R.id.help:
                Intent startInstructions = new Intent(this, InstructionActivity.class);
                startActivity(startInstructions);

                return true;
            case R.id.share:
                showShareOptions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showShareOptions() {
        String userResult = "";

        Intent shareResultIntent = new Intent(Intent.ACTION_SEND);
        shareResultIntent.setType("text/plain");
        switch (resultType.toString()) {
            case "anagram":
                userResult = getString(R.string.txt_share_I_Found) +
                        " " + '"' + result.getWord() + '"' + " " +
                        getString(R.string.txt_share_is_an_anagram) + " " +
                        '"' + query.toString() + '"' + " " +
                        getString(R.string.txt_with_word_sleuth);

                shareResultIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        userResult + " " + getString(R.string.google_play_store_http_uri));

                startActivity(Intent.createChooser(
                        shareResultIntent,
                        getString(R.string.abc_shareactionprovider_share_with)));
                break;
            case "subword":

                userResult = getString(R.string.txt_share_I_Found) +
                        " " + getString(R.string.txt_share_the_subword) +
                        " " + '"' + result.getWord() + '"' + " " +
                        getString(R.string.txt_share_from) + " " +
                        '"' + query.toString() + '"' + " " +
                        getString(R.string.txt_with_word_sleuth);

                shareResultIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        userResult + getString(R.string.google_play_store_http_uri));

                startActivity(Intent.createChooser(
                        shareResultIntent,
                        getString(R.string.abc_shareactionprovider_share_with)));
                break;
            case "combo":

                userResult = getString(R.string.txt_share_I_Found) +
                        " " + getString(R.string.txt_share_combo) + " " +
                        '"' + result.getWord() + '"' + " " +
                        getString(R.string.txt_share_from) + " " +
                        '"' + query.toString() + '"' + " " +
                        getString(R.string.txt_with_word_sleuth);

                shareResultIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        userResult + getString(R.string.google_play_store_http_uri));

                startActivity(Intent.createChooser(
                        shareResultIntent,
                        getString(R.string.abc_shareactionprovider_share_with)));
                break;
        }

    }

    /* ------------------ */
    /* ----- States ----- */
    /* ------------------ */

    /**
     * Resume
     */
    @Override
    protected void onResume() {
        Log.i(TAG, "onResume() called");

        if (!isBound) {
            // Bound service intent, different that received intent
            searchIntent = new Intent(this, BoundSearchService.class);
            // We want to bind to this. Params: (Intent, ServiceConnection, How to bind it)
            bindService(searchIntent, connection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "Service is bound from on create");
        } else {
            Log.i(TAG, "Service was already bound. Nothing to do...");
        }
        super.onResume();
    }

    /**
     * On pause.
     * TODO: Decide what to do with resources.
     */
    @Override
    protected void onPause() {
        Log.i(TAG, "onPause() called");

        if (isBound) {
            unbindService(connection);
            Log.i(TAG, "Service was unbound from onDestroy");
            isBound = false;
        } else {
            Log.i(TAG, "Service is bound");
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop() called");

        if (isBound) {
            unbindService(connection);
            Log.i(TAG, "Service was unbound from onDestroy");
            isBound = false;
        } else {
            Log.i(TAG, "Service is bound");
        }
        super.onStop();
    }

    // Unbind service if activity is destroyed. Hopefully fixes stephen's UN-TICKETED BUG!
    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy called");
        if (isBound) {
            unbindService(connection);
            Log.i(TAG, "Service was unbound from onDestroy");
            isBound = false;
        } else {
            Log.i(TAG, "Service is bound");
        }
        super.onDestroy();
    }

}
