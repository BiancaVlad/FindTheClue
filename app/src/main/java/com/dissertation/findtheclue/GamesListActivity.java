package com.dissertation.findtheclue;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;

import model.GamesAdapter;
import model.GamesContent;
import utils.ServiceHandler;

public class GamesListActivity extends AppCompatActivity {

    // URL to get contacts JSON
    private static String url = "http://findtheclue.azurewebsites.net/api/games";

    // JSON Node names
    private static final String TAG_ID = "id_game";
    private static final String TAG_NAME = "name";
    private static final String TAG_COUNTRY = "country";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CITY = "city";
    private static final String TAG_DIFFICULTY = "difficulty";
    private static final String TAG_RATING = "rating";
    private static final String TAG_RATING_COUNTER = "rating_counter";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_DURATION = "duration";

    private GamesContent.GameItem gamesItems[];

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GamesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_list);

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
                Intent intent=new Intent(view.getContext(), PlayGameActivity.class);
                intent.putExtra("gameId", game.getId_game());
                intent.putExtra("gameName", game.getName());
                intent.putExtra("gameCity", game.getCity());
                intent.putExtra("gameCountry", game.getCountry());
                intent.putExtra("gameDescription", game.getDescription());
                intent.putExtra("gameDifficulty", GamesAdapter.GetDifficulty(game.getDifficulty()));
                intent.putExtra("gameDuration", GamesAdapter.GetDuration(game.getDuration()));
                byte[] decodedByte = Base64.decode(game.getPicture(), Base64.DEFAULT);
                intent.putExtra("gamePicture", decodedByte);
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

        ServiceHandler sh = new ServiceHandler();
        if(GamesContent.ITEMS.isEmpty()) {
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray games = new JSONArray(jsonStr);

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

                        GamesContent.ITEMS.add(new GamesContent.GameItem(Integer.parseInt(id), name, country, city, description, Integer.parseInt(difficulty), Double.parseDouble(rating), Integer.parseInt(ratingCounter),picture, Integer.parseInt(duration)));
                    }

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

        gamesItems= new GamesContent.GameItem[GamesContent.ITEMS.size()];
        gamesItems = GamesContent.ITEMS.toArray(gamesItems);

        return result;
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetGames extends AsyncTask<Void, Void, Void> {
        private Context context;
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            nDialog = new ProgressDialog(context); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            nDialog.dismiss();
            //showProgress(false);
        }
    }
}
