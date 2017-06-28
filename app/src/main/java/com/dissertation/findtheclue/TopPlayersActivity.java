package com.dissertation.findtheclue;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;
import model.GamesAdapter;
import model.GamesContent;
import model.PlayersAdapter;
import model.PlayersContent;
import utils.RestClient;
import utils.ServiceHandler;

public class TopPlayersActivity extends SideMenuActivity {

    // URL to get contacts JSON
    private static String url = "http://findtheclue.azurewebsites.net/api/players";

    //to do: add the names of the table columns
    // JSON Node names
    private static final String TAG_ID = "id_player";
    private static final String TAG_FIRST_NAME = "first_name";
    private static final String TAG_LAST_NAME = "last_name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE_NUMBER = "phone_number";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_SCORE = "score";
    private static final String TAG_PROFILE_PICTURE = "profile_picture";

    private PlayersContent.PlayerItem playerItems[];
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlayersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_top_players);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_top_players, null, false);
        drawer.addView(contentView, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.top_players_recycler_view);

        mAdapter = new PlayersAdapter(PlayersContent.ITEMS);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        //mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);

        try {
            getTopPlayersList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTopPlayersList() throws JSONException {
        {
            RestClient.get("players", null, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray players) {

                    for (int i = 0; i < players.length(); i++) {
                        JSONObject g = null;
                        try {
                            g = players.getJSONObject(i);
                            String id = g.getString(TAG_ID);
                            String first_name = g.getString(TAG_FIRST_NAME);
                            String last_name = g.getString(TAG_LAST_NAME);
                            String email = g.getString(TAG_EMAIL);
                            String phone_number = g.getString(TAG_PHONE_NUMBER);
                            String password = g.getString(TAG_PASSWORD);
                            String score = g.getString(TAG_SCORE);
                            String profile_picture = g.getString(TAG_PROFILE_PICTURE);

                            PlayersContent.ITEMS.add(new PlayersContent.PlayerItem(Integer.parseInt(id), first_name, last_name, email, phone_number, password, Double.parseDouble(score), profile_picture));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Collections.sort(PlayersContent.ITEMS, new Comparator<PlayersContent.PlayerItem>() {
                            @Override
                            public int compare(PlayersContent.PlayerItem o1, PlayersContent.PlayerItem o2) {
                                return (int)(o2.getScore() - o1.getScore()) ;
                            }
                        });
                        mAdapter.notifyDataSetChanged();
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
            });
        }
    }

    @Override
    public View onCreateView(View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View result = super.onCreateView(parent, name, context, attrs);

        playerItems= new PlayersContent.PlayerItem[PlayersContent.ITEMS.size()];
        playerItems = PlayersContent.ITEMS.toArray(playerItems);

        return result;
    }

    @Override
    public void onBackPressed() {
        if (this.isTaskRoot()) {
            Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
            super.onBackPressed();
        }
    }
}
