package com.rabbitfighter.wordsleuth.ResultsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rabbitfighter.wordsleuth.Activities.ResultDetailActivity;
import com.rabbitfighter.wordsleuth.Database.ResultsDbAdapter;
import com.rabbitfighter.wordsleuth.ListItems.ResultItem;
import com.rabbitfighter.wordsleuth.Entries.Result;
import com.rabbitfighter.wordsleuth.R;
import com.rabbitfighter.wordsleuth.Utils.RobotoFontsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SQLite Database. There are many like it but this one is mine...
 *
 * @author Joshua Michael Waggoner <rabbitfighter@cryptolab.net>
 * @author Stephen Chavez <stephen.chavez12@gmail.com>
 * @version 0.2 (pre-beta)
 * @link https://github.com/rabbitfighter81/SwipeNavExample (Temporary)
 * @see 'http://developer.android.com/reference/android/database/sqlite/package-summary.html'
 * @since 0.1 2015-06-20.
 */
public class BlankTileResultFragment extends Fragment {
    public final static String TAG = "ResultBlankTileFragment";

    // Vars
    String query;
    ResultsDbAdapter dbAdapter;
    ArrayList<Result> results;
    ArrayList<ResultItem> resultItemList;
    Bundle bundle;
    int numResults;


    private String resultType;
    private int sortType;

    /**
     * On create
     * @param savedInstanceState -  the bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Get query from bundle
        if (savedInstanceState == null) {
            bundle = getArguments();
            resultItemList = new ArrayList<>();
            dbAdapter = new ResultsDbAdapter(getActivity());
            query = bundle.getString("query").toString();
            resultType = bundle.getString("resultType").toString();
            sortType = bundle.getInt("sortType");
            numResults = 0;
        } else {
            bundle = savedInstanceState;
            resultItemList = new ArrayList<>();
            dbAdapter = new ResultsDbAdapter(getActivity());
            query = bundle.getString("query").toString();
            resultType = bundle.getString("resultType").toString();
            sortType = bundle.getInt("sortType");
            numResults = 0;
        }
        super.onCreate(bundle);
    }


    /**
     * When the view gets created
     * @param inflater - the LayoutInflater object
     * @param container - The Vihttp://developer.android.com/reference/android/widget/ArrayAdapter.htmlewGroup object
     * @param savedInstanceState - The Bundle
     * @return rootView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout
        View rootView = inflater.inflate(R.layout.fragment_results_items, container, false);
        // Views
        TextView tv_title,  tv_query, tv_results;
        // Populate the result type list from database
        populateResultItemList(this.getResultType(), this.getSortType());
        // Populate the list view from resultType list
        populateListView(rootView);
        // Control the callbacks from item clicks
        registerClickCallback(rootView);
        // Set components
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_query = (TextView) rootView.findViewById(R.id.tv_query);
        tv_results = (TextView) rootView.findViewById(R.id.tv_numResults);
        // Set text
        tv_query.setText("\"" + query + "\"");
        tv_results.setText(String.valueOf(numResults));
        // Set typefaces
        tv_title.setTypeface(RobotoFontsHelper.getTypeface(rootView.getContext(), RobotoFontsHelper.roboto_black)); // Bold

        // Return the root view
        return rootView;
    }

    /**
     * Populate the result item lists.
     * @param sortType - the sortType
     */
    private void populateResultItemList(String resultType, int sortType) {
        Log.i(TAG, "populateResultTypeList() called");
        results = new ArrayList<>();
        switch (resultType.toString()) {
            case "anagram":
                results = dbAdapter.getAnagrams(sortType);
                for (int i = 0; i < results.size(); i++) {
                    resultItemList.add(new ResultItem(results.get(i).getWord(), results.get(i).getNumLetters(), R.mipmap.ic_action_good, R.mipmap.ic_action_new));
                }
                numResults = resultItemList.size();
                break;
            case "subword":
                results = dbAdapter.getSubwords(sortType);
                for (int i = 0; i < results.size(); i++) {
                    resultItemList.add(new ResultItem(results.get(i).getWord(), results.get(i).getNumLetters(), R.mipmap.ic_action_good, R.mipmap.ic_action_new));
                }
                numResults = resultItemList.size();
                break;

        }

    }

    /**
     * Populate the list view.
     * @param rootView - The view passed in
     */
    private void populateListView(View rootView) {
        Log.i(TAG, "populateListView() called");
        ArrayAdapter<ResultItem> adapter = new ResultTypeListAdapter();
        ListView list = (ListView) rootView.findViewById(R.id.lv_results);
        list.setAdapter(adapter);
    }

    /**
     * List adapter.
     * @see 'http://developer.android.com/reference/android/widget/ArrayAdapter.html'
     */
    public class ResultTypeListAdapter extends ArrayAdapter<ResultItem> {

        public ResultTypeListAdapter() {
            super(getActivity(), R.layout.item_result, resultItemList);
            Log.i(TAG, "ResultTypeListAdapter constructor called super");
        }

        /**
         * This is where the view gets set, components, et all...
         * @param position -  the list item position
         * @param convertView -  ???
         * @param parent - ???
         * @return the item view. There is one for each list item
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "getView() called");
            // Get a handle for the view that we can change without changing the convertView
            View itemView = convertView;
            // The view passed in may be null, just an F.Y.I.
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.item_result, parent, false);
            }
            // Find Result Type
            ResultItem r = resultItemList.get(position);
            // Set components
            ImageView imageView1 = (ImageView) itemView.findViewById(R.id.iv_ic_success);
            TextView tv_numLetters = (TextView) itemView.findViewById(R.id.tv_numLetters);
            TextView tv_result = (TextView) itemView.findViewById(R.id.tv_result);
            // Set resources
            imageView1.setImageResource(r.getIconID());
            // Set text
            tv_result.setText(String.valueOf(r.getResult()));
            tv_numLetters.setText(String.valueOf(r.getLength() + " letters"));
            // Set fonts
            tv_result.setTypeface(RobotoFontsHelper.getTypeface(getContext(), RobotoFontsHelper.roboto_regular));
            tv_numLetters.setTypeface(RobotoFontsHelper.getTypeface(getContext(), RobotoFontsHelper.roboto_light)); // Light
            // Return the view
            return itemView;
        }
    }


    /**
     * Callback for item clicks.
     * @param rootView - the root view passed in.
     */
    private void registerClickCallback(final View rootView) {
        Log.i(TAG, "registerClickCallback() called");
        ListView list = (ListView) rootView.findViewById(R.id.lv_results);
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    /**
                     * When an item is clicked
                     * @param parent
                     * @param viewClicked
                     * @param position
                     * @param id
                     */
                    @Override
                    public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                        // Get the position
                        ResultItem clickedItem = resultItemList.get(position);
                        openResultDetailFragment(clickedItem);
                    }

                    /**
                     * Opens the results list activity with query and result type bundled in intent
                     * @param item - the result item to lookup
                     */
                    private void openResultDetailFragment(ResultItem item) {
                        Intent intent = new Intent(getActivity(), ResultDetailActivity.class);
                        Bundle b = new Bundle();
                        b.putString("query", query);
                        b.putString("resultType", getResultType());
                        b.putString("result", item.getResult().toString());
                        intent.putExtras(b);
                        startActivity(intent);
                    }

                }
        );
    }

    public String getResultType() {

        return resultType;
    }

    public void setResultType(String resultType) {

        this.resultType = resultType;
    }

    public int getSortType() {

        return sortType;
    }

    public void setSortType(int sortType) {

        this.sortType = sortType;
    }
}
