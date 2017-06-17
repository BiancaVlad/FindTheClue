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
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import model.GamesAdapter;
import model.GamesContent;
import model.PlayersAdapter;
import model.PlayersContent;
import utils.ServiceHandler;

public class TopPlayersActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_top_players);

        mRecyclerView = (RecyclerView) findViewById(R.id.top_players_recycler_view);

        mAdapter = new PlayersAdapter(PlayersContent.ITEMS);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        //mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);

        ServiceHandler sh = new ServiceHandler();
        if(PlayersContent.ITEMS.isEmpty()) {
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray players = new JSONArray(jsonStr);

                    for (int i = 0; i < players.length(); i++) {
                        JSONObject g = players.getJSONObject(i);

                        String id = g.getString(TAG_ID);
                        String first_name = g.getString(TAG_FIRST_NAME);
                        String last_name = g.getString(TAG_LAST_NAME);
                        String email = g.getString(TAG_EMAIL);
                        String phone_number = g.getString(TAG_PHONE_NUMBER);
                        String password = g.getString(TAG_PASSWORD);
                        String score = g.getString(TAG_SCORE);
                        String profile_picture = g.getString(TAG_PROFILE_PICTURE);

                        PlayersContent.ITEMS.add(new PlayersContent.PlayerItem(Integer.parseInt(id), first_name, last_name, email, phone_number, password, Double.parseDouble(score), profile_picture));
                    }

                    Collections.sort(PlayersContent.ITEMS, new Comparator<PlayersContent.PlayerItem>() {
                        @Override
                        public int compare(PlayersContent.PlayerItem o1, PlayersContent.PlayerItem o2) {
                            return (int)(o2.getScore() - o1.getScore()) ;
                        }
                    });

                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("utils.ServiceHandler", "Couldn't get any data from the url");
            }
        }
    }

    @Override
    public View onCreateView(View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View result = super.onCreateView(parent, name, context, attrs);

        playerItems= new PlayersContent.PlayerItem[PlayersContent.ITEMS.size()];
        playerItems = PlayersContent.ITEMS.toArray(playerItems);

        return result;
    }
}
