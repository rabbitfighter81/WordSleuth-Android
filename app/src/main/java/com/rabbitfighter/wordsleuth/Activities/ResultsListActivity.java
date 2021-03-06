package com.rabbitfighter.wordsleuth.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.rabbitfighter.wordsleuth.R;
import com.rabbitfighter.wordsleuth.ResultsFragments.BlankTileResultFragment;
import com.rabbitfighter.wordsleuth.ResultsFragments.CrosswordResultFragment;
import com.rabbitfighter.wordsleuth.ResultsFragments.RegularResultFragment;
import com.rabbitfighter.wordsleuth.Services.BoundSearchService;

import java.util.HashMap;
import java.util.Map;

/**
 * Results list activity
 *
 * @author Joshua Michael Waggoner <rabbitfighter@cryptolab.net>
 * @author Stephen Chavez <stephen.chavez12@gmail.com>
 * @version 0.2 (pre-beta)
 * @link https://github.com/rabbitfighter81/SwipeNavExample (Temporary)
 * @see 'Android lists: http://developer.android.com/guide/topics/ui/layout/listview.html'
 * @since 0.1 2015-06-17.
 */
public class ResultsListActivity extends ActionBarActivity {
    // Debugging TAG
    private static final String TAG = "ResultsListActivity";

    // Vars
    Fragment fragment;
    Fragment f;
    Bundle bundle;
    Intent intent;
    String resultType;
    String query;
    String searchType;
    int sortType;
    public static Map<Integer, String> sortMap;
    static {
        sortMap = new HashMap<>();
        sortMap.put(0, "length (low to high)");    // length ASC
        sortMap.put(1, "length (high to low)");    // length DESC
        sortMap.put(2, "Scrabble(TM) points");  // ...
        sortMap.put(3, "Words(TM) points");
    }


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



    /* ------------------------- */
    /* --- @Override methods --- */
    /* ------------------------- */

    /**
     * On creation
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // The new bundle
        if (savedInstanceState == null) {
            intent = getIntent();
            bundle = intent.getExtras();
            query = bundle.get("query").toString();
            searchType = bundle.get("searchType").toString();
            resultType = bundle.get("resultType").toString();
            sortType = Integer.valueOf(bundle.get("sortType").toString());
        } else {
            bundle = savedInstanceState;
            query = bundle.get("query").toString();
            searchType = bundle.get("searchType").toString();
            resultType = bundle.get("resultType").toString();
            sortType = Integer.valueOf(bundle.get("sortType").toString());
        }

        isBound = false;

        // Action bar stuff
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        transitionToResultFragment(bundle);
    }


    /**
     * Transition to the correct results fragment
     */
    private void transitionToResultFragment(Bundle b) {
        String searchType = b.getString("searchType");
        Log.i(TAG, this.searchType + " " + this.resultType + " " + this.sortType);

        // During initial setup, plug in the details fragment.
        if (searchType.compareTo("regularSearch")==0) {
            fragment = new RegularResultFragment();
        } else if (searchType.compareTo("blankTileSearch")==0) {
            fragment = new BlankTileResultFragment();
        } else if (searchType.compareTo("crosswordSearch")==0) {
            fragment = new CrosswordResultFragment();
        } else {
            Log.i(TAG, "Something went wrong with the search type bundle info");
        }

        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().add(R.id.contentFragment, fragment).commit();

    }

    /**
     * When the menu is created...
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu() triggered");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_results, menu);
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
        Log.i(TAG, "onOptionsItemSelected(MenuItem: " + item + ")");
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
            case R.id.sort:
                // Sort the items
                Log.i(TAG, "Sort menu item clicked");

                // Switch to next sort type
                sortType = (sortType+1)%sortMap.size(); // This will let us create a circular array

                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putString("query", query);
                b.putString("searchType", searchType);
                b.putString("resultType", resultType);
                b.putInt("sortType", sortType);
                i.putExtras(b);
                // Transition to results fragment
                transitionToResultFragment(b);

                return true;
            default:
                return super.onOptionsItemSelected(item);
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

}//EOF
