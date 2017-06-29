package com.dissertation.findtheclue;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import model.GamesContent;
import model.QuestionContent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.ServiceHandler;
import utils.TokenSaver;

public class PlayGameActivity extends SideMenuActivity
        implements OnMapReadyCallback {

    private static final String TAG_ID = "gameId";
    private static final String TAG_NAME = "gameName";
    private static final String TAG_COUNTRY = "gameCountry";
    private static final String TAG_CITY = "gameCity";
    private static final String TAG_DESCRIPTION = "gameDescription";
    private static final String TAG_DIFFICULTY = "gameDifficulty";
    private static final String TAG_RATING = "gameRating";
    private static final String TAG_PICTURE = "gamePicture";
    private static final String TAG_DURATION = "gameDuration";

    private static final String TAG_QUESTION_ID = "Id";
    private static final String TAG_QUESTION_TEXT = "QuestionText";
    private static final String TAG_QUESTION_ANSWER = "TextAnswer";
    private static final String TAG_QUESTION_SCORE = "Score";
    private static final String TAG_QUESTION_GAME_ID = "GameId";
    private static final String TAG_QUESTION_LONGITUDE = "Longitude";
    private static final String TAG_QUESTION_LATITUDE = "Latitude";

    // URL to get contacts JSON
    private static String url = "http://findthecluebe.azurewebsites.net/api/Questions/game/";

    Button playButton;
    private GoogleMap mMap;
    ScrollView playScrollView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_play_game, null, false);
        drawer.addView(contentView, 0);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setViews();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.route_map);
        mapFragment.getMapAsync(this);

        ImageView transparentImageView = (ImageView) findViewById(R.id.play_transparent_image);

        playScrollView = (ScrollView) findViewById(R.id.play_scroll_view);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        playScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        playScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        playScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        playButton = (Button) findViewById(R.id.play_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (QuestionContent.ITEMS.size() > 0) {
                    QuestionContent.questionCounter = 0;
                    Intent intent = new Intent(v.getContext(), QuestionActivity.class);
                    v.getContext().startActivity(intent);
                    finish();
                }
            }
        });

        //new GetQuestionsForGame().execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        /*if(QuestionContent.ITEMS.size() > 0)
        {
            double lat = QuestionContent.ITEMS.get(0).getLatitude();
            double lng = QuestionContent.ITEMS.get(0).getLongitude();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lat, lng), 14f));
        }*/
        if (QuestionContent.ITEMS.size() > 0) {
            PolylineOptions poly;
            poly = new PolylineOptions()
                    .color(R.color.colorPrimary)
                    .width(5)
                    .visible(true)
                    .zIndex(30);

            for (int i = 0; i < QuestionContent.ITEMS.size(); i++) {
                LatLng position = new LatLng(QuestionContent.ITEMS.get(i).getLatitude(), QuestionContent.ITEMS.get(i).getLongitude());
                poly.add(position);

                if (i == 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            position, 14f));

                    mMap.addMarker(new MarkerOptions().position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title("Start point"));
                } else if (i == QuestionContent.ITEMS.size() - 1) {
                    mMap.addMarker(new MarkerOptions().position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).title("End point"));
                } else {
                    mMap.addMarker(new MarkerOptions().position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                }
            }

            mMap.addPolyline(poly);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new GetQuestionsForGame().execute();
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PlayGame Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.dissertation.findtheclue/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PlayGame Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.dissertation.findtheclue/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    private class GetQuestionsForGame extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getIntent().hasExtra(TAG_ID)) {
                getQuestions(getIntent().getIntExtra(TAG_ID, 0));
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            /*getQuestions(getIntent().getIntExtra(TAG_ID, 0));*/
        }
    }

    private void setViews() {
        if (getIntent().hasExtra(TAG_PICTURE)) {
            ImageView imgView = (ImageView) findViewById(R.id.game_picture_details);
            String picUrl = getIntent().getStringExtra(TAG_PICTURE);
            if (picUrl != null && !picUrl.isEmpty()) {
                Uri uri = Uri.parse(picUrl);
                if (uri != null && URLUtil.isValidUrl(uri.toString())) {
                    try {
                        Picasso.with(imgView.getContext()).load(uri).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgView);
                    } catch (Exception ex) {
                        Bitmap icon = BitmapFactory.decodeResource(imgView.getContext().getResources(),
                                R.mipmap.investi4);
                        imgView.setImageBitmap(icon);
                    }
                } else {
                    Bitmap icon = BitmapFactory.decodeResource(imgView.getContext().getResources(),
                            R.mipmap.investi4);
                    imgView.setImageBitmap(icon);
                }
            } else {
                Bitmap icon = BitmapFactory.decodeResource(imgView.getContext().getResources(),
                        R.mipmap.investi4);
                imgView.setImageBitmap(icon);
            }
        } else {
            ImageView imgView = (ImageView) findViewById(R.id.game_picture_details);
            Bitmap icon = BitmapFactory.decodeResource(imgView.getContext().getResources(),
                    R.mipmap.investi4);
            imgView.setImageBitmap(icon);
        }

        if (getIntent().hasExtra(TAG_NAME)) {
            TextView InputName = (TextView) findViewById(R.id.game_name_details);
            InputName.setText(getIntent().getStringExtra(TAG_NAME).toUpperCase());
        }

        String city = "", country = "";

        if (getIntent().hasExtra(TAG_CITY)) {
            city = getIntent().getStringExtra(TAG_CITY);
        }
        if (getIntent().hasExtra(TAG_COUNTRY)) {
            country = getIntent().getStringExtra(TAG_COUNTRY);
        }

        if (city != "" && country != "") {
            TextView InputName = (TextView) findViewById(R.id.game_details_location);
            InputName.setText(city + ", " + country);
        } else if (city != "") {
            TextView InputName = (TextView) findViewById(R.id.game_details_location);
            InputName.setText(city);
        } else if (country != "") {
            TextView InputName = (TextView) findViewById(R.id.game_details_location);
            InputName.setText(country);
        }

        if (getIntent().hasExtra(TAG_DESCRIPTION)) {
            TextView InputName = (TextView) findViewById(R.id.short_description);
            InputName.setText(getIntent().getStringExtra(TAG_DESCRIPTION));
        }

        if (getIntent().hasExtra(TAG_RATING)) {
            RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
            ratingBar.setRating((float) getIntent().getDoubleExtra(TAG_RATING, 0.00));
        }

        if (getIntent().hasExtra(TAG_DURATION)) {
            TextView InputName = (TextView) findViewById(R.id.game_details_duration);
            InputName.setText("Duration: " + getIntent().getStringExtra(TAG_DURATION));
        }

        if (getIntent().hasExtra(TAG_DIFFICULTY)) {
            TextView InputName = (TextView) findViewById(R.id.game_details_difficulty);
            InputName.setText("Difficulty: " + getIntent().getStringExtra(TAG_DIFFICULTY));
        }
    }

    protected void getQuestions(int id) {
        String currentToken = TokenSaver.getToken(getApplicationContext());

        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(url + id)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("authorization", "bearer " + currentToken)
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseData = response.body().string();
                try {
                    JSONArray questions = new JSONArray(responseData);
                    QuestionContent.ITEMS.clear();
                    QuestionContent.score = 0;

                    for (int i = 0; i < questions.length(); i++) {
                        JSONObject q = questions.getJSONObject(i);
                        String gameId = q.getString(TAG_QUESTION_GAME_ID);
                        String questionId = q.getString(TAG_QUESTION_ID);
                        String questionText = q.getString(TAG_QUESTION_TEXT);
                        String questionAnswer = q.getString(TAG_QUESTION_ANSWER);
                        String score = q.getString(TAG_QUESTION_SCORE);
                        String longitude = q.getString(TAG_QUESTION_LONGITUDE);
                        String latitude = q.getString(TAG_QUESTION_LATITUDE);

                        QuestionContent.ITEMS.add(new QuestionContent.QuestionItem(Integer.parseInt(questionId), questionAnswer, Double.parseDouble(score), Integer.parseInt(gameId), Double.parseDouble(longitude), Double.parseDouble(latitude), questionText));
                    }
                } catch (JSONException e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

       /* if (jsonStr != null) {
            try {
                // Getting JSON Array node
                JSONArray questions = new JSONArray(jsonStr);
                QuestionContent.ITEMS.clear();
                QuestionContent.score = 0;

                for (int i = 0; i < questions.length(); i++) {
                    JSONObject q = questions.getJSONObject(i);
                    String gameId = q.getString(TAG_QUESTION_GAME_ID);
                    String questionId = q.getString(TAG_QUESTION_ID);
                    String questionText = q.getString(TAG_QUESTION_TEXT);
                    String questionAnswer = q.getString(TAG_QUESTION_ANSWER);
                    String score = q.getString(TAG_QUESTION_SCORE);
                    String longitude = q.getString(TAG_QUESTION_LONGITUDE);
                    String latitude = q.getString(TAG_QUESTION_LATITUDE);

                    QuestionContent.ITEMS.add(new QuestionContent.QuestionItem(Integer.parseInt(questionId), questionAnswer, Double.parseDouble(score), Integer.parseInt(gameId), Double.parseDouble(longitude), Double.parseDouble(latitude), questionText));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("utils.ServiceHandler", "Couldn't get any data from the url");
        }*/
    }
}
