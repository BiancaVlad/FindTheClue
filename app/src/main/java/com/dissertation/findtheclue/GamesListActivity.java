package com.dissertation.findtheclue;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.GamesAdapter;
import model.GamesContent;
import utils.RestClient;
import utils.TokenSaver;

public class GamesListActivity extends SideMenuActivity {

    private static String url = "http://findthecluebe.azurewebsites.net/api/games";

    // JSON Node names
    private static final String TAG_ID = "Id";
    private static final String TAG_NAME = "Name";
    private static final String TAG_COUNTRY = "Country";
    private static final String TAG_DESCRIPTION = "Description";
    private static final String TAG_CITY = "City";
    private static final String TAG_DIFFICULTY = "Difficulty";
    private static final String TAG_RATING = "Rating";
    private static final String TAG_RATING_COUNTER = "RatingCounter";
    private static final String TAG_PICTURE = "PictureUrl";
    private static final String TAG_DURATION = "Duration";

    private GamesContent.GameItem gamesItems[];

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GamesAdapter mAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_games_list);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_games_list, null, false);
        drawer.addView(contentView, 0);

        mProgressBar = (ProgressBar) findViewById(R.id.games_progress);
        //mProgressBar.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.gamesRecyclerView);
        mAdapter = new GamesAdapter(GamesContent.ITEMS);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouch.RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouch.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                GamesContent.GameItem game = GamesContent.ITEMS.get(position);
                Intent intent = new Intent(view.getContext(), PlayGameActivity.class);
                intent.putExtra("gameId", game.getId_game());
                intent.putExtra("gameName", game.getName());
                intent.putExtra("gameCity", game.getCity());
                intent.putExtra("gameCountry", game.getCountry());
                intent.putExtra("gameDescription", game.getDescription());
                intent.putExtra("gameDifficulty", GamesAdapter.GetDifficulty(game.getDifficulty()));
                intent.putExtra("gameDuration", GamesAdapter.GetDuration(game.getDuration()));
                intent.putExtra("gamePicture", game.getPicture());
                intent.putExtra("gameRating", game.getRating());
                intent.putExtra("gameRatingCounter", game.getRatingCounter());
                view.getContext().startActivity(intent);
                //startActivity(intent);
                //finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        try {
            getGameListCall(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void refreshItems() {
        try {
            if(GamesContent.ITEMS.size() > 0)
            {
                GamesContent.ITEMS.clear();
            }
            getGameListCall(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
       // mAdapter.notifyDataSetChanged();
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void getGameListCall(final boolean refresh) throws JSONException {
        String currentToken = TokenSaver.getToken(getApplicationContext());

        RestClient.get("games", null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                if(!refresh && GamesContent.ITEMS.size() == 0) {
                    showProgress(true);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray games) {
                if(GamesContent.ITEMS.size() == 0) {
                    try {
                        for (int i = 0; i < games.length(); i++) {
                            JSONObject g = games.getJSONObject(i);

                            String id = g.getString(TAG_ID);
                            String name = g.getString(TAG_NAME);
                            String country = g.getString(TAG_COUNTRY);
                            String city = g.getString(TAG_CITY);
                            String description = g.getString(TAG_DESCRIPTION);
                            String difficulty = g.getString(TAG_DIFFICULTY);
                            String rating = g.getString(TAG_RATING);
                            String ratingCounter = g.getString(TAG_RATING_COUNTER);
                            String picture = g.getString(TAG_PICTURE);
                            String duration = g.getString(TAG_DURATION);

                            GamesContent.ITEMS.add(new GamesContent.GameItem(Integer.parseInt(id), name, country, city, description, Integer.parseInt(difficulty), Double.parseDouble(rating), Integer.parseInt(ratingCounter), picture, Integer.parseInt(duration)));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mAdapter.notifyDataSetChanged();

                    if(!refresh) {
                        showProgress(false);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, currentToken);
    }

    @Override
    public View onCreateView(View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View result = super.onCreateView(parent, name, context, attrs);

        gamesItems = new GamesContent.GameItem[GamesContent.ITEMS.size()];
        gamesItems = GamesContent.ITEMS.toArray(gamesItems);

        return result;
    }
}
