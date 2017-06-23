package com.dissertation.findtheclue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.GamesContent;
import model.QuestionContent;
import utils.ServiceHandler;

public class PlayGameActivity extends SideMenuActivity {

    private static final String TAG_ID = "gameId";
    private static final String TAG_NAME = "gameName";
    private static final String TAG_COUNTRY = "gameCountry";
    private static final String TAG_CITY = "gameCity";
    private static final String TAG_DESCRIPTION = "gameDescription";
    private static final String TAG_DIFFICULTY = "gameDifficulty";
    private static final String TAG_RATING = "gameRating";
    private static final String TAG_PICTURE = "gamePicture";
    private static final String TAG_DURATION = "gameDuration";

    private static final String TAG_QUESTION_ID = "id_question";
    private static final String TAG_QUESTION_TEXT = "question_text";
    private static final String TAG_QUESTION_ANSWER = "text_answer";
    private static final String TAG_QUESTION_SCORE = "score";
    private static final String TAG_QUESTION_GAME_ID = "game_id_game";
    private static final String TAG_QUESTION_LONGITUDE = "longitude";
    private static final String TAG_QUESTION_LATITUDE = "latitude";

    // URL to get contacts JSON
    private static String url = "http://findtheclue.azurewebsites.net/api/Questions";

    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_play_game);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_play_game, null, false);
        drawer.addView(contentView, 0);

        this.setViews();

        playButton = (Button) findViewById(R.id.play_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(QuestionContent.ITEMS.size() > 0) {
                    QuestionContent.questionCounter = 0;
                    Intent intent = new Intent(v.getContext(), QuestionActivity.class);
                    v.getContext().startActivity(intent);
                }
            }
        });

        //new GetQuestionsForGame().execute();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new GetQuestionsForGame().execute();
    }

    private class GetQuestionsForGame extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            getQuestions(getIntent().getIntExtra(TAG_ID, 0));
        }
    }

    private void setViews() {

        if (getIntent().hasExtra(TAG_PICTURE)) {
            byte[] decodedByte = getIntent().getByteArrayExtra(TAG_PICTURE);
            Bitmap bmp = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra(TAG_PICTURE), 0, decodedByte.length);
            ImageView imgView = (ImageView) findViewById(R.id.game_picture_details);
            imgView.setImageBitmap(bmp);
        }
        if(getIntent().hasExtra(TAG_NAME)) {
            TextView InputName = (TextView) findViewById(R.id.game_name_details);
            InputName.setText(getIntent().getStringExtra(TAG_NAME).toUpperCase());
        }

        String city = "", country = "";

        if(getIntent().hasExtra(TAG_CITY))
        {
            city = getIntent().getStringExtra(TAG_CITY);
        }
        if(getIntent().hasExtra(TAG_COUNTRY))
        {
            country = getIntent().getStringExtra(TAG_COUNTRY);
        }

        if(city != "" && country != "")
        {
            TextView InputName = (TextView) findViewById(R.id.game_details_location);
            InputName.setText(city + ", " + country);
        }
        else if(city != "")
        {
            TextView InputName = (TextView) findViewById(R.id.game_details_location);
            InputName.setText(city);
        }
        else if(country != "")
        {
            TextView InputName = (TextView) findViewById(R.id.game_details_location);
            InputName.setText(country);
        }

        if(getIntent().hasExtra(TAG_DESCRIPTION))
        {
            TextView InputName = (TextView) findViewById(R.id.short_description);
            InputName.setText(getIntent().getStringExtra(TAG_DESCRIPTION));
        }

        if(getIntent().hasExtra(TAG_RATING))
        {
            RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
            ratingBar.setRating((float)getIntent().getDoubleExtra(TAG_RATING, 0.00));
        }

        if(getIntent().hasExtra(TAG_DURATION))
        {
            TextView InputName = (TextView) findViewById(R.id.game_details_duration);
            InputName.setText("Duration: " + getIntent().getStringExtra(TAG_DURATION));
        }

        if(getIntent().hasExtra(TAG_DIFFICULTY))
        {
            TextView InputName = (TextView) findViewById(R.id.game_details_difficulty);
            InputName.setText("Difficulty: " + getIntent().getStringExtra(TAG_DIFFICULTY));
        }
    }

    protected void getQuestions(int id)
    {
        ServiceHandler sh = new ServiceHandler();

        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
        if (jsonStr != null) {
            try {
                // Getting JSON Array node
                JSONArray questions = new JSONArray(jsonStr);
                QuestionContent.ITEMS.clear();

                for (int i = 0; i < questions.length(); i++) {
                    JSONObject q = questions.getJSONObject(i);

                    String gameId = q.getString(TAG_QUESTION_GAME_ID);
                    if(Integer.parseInt(gameId) == id) {
                        String questionId = q.getString(TAG_QUESTION_ID);
                        String questionText = q.getString(TAG_QUESTION_TEXT);
                        String questionAnswer = q.getString(TAG_QUESTION_ANSWER);
                        String score = q.getString(TAG_QUESTION_SCORE);
                        String longitude = q.getString(TAG_QUESTION_LONGITUDE);
                        String latitude = q.getString(TAG_QUESTION_LATITUDE);

                        QuestionContent.ITEMS.add(new QuestionContent.QuestionItem(Integer.parseInt(questionId), questionAnswer, Double.parseDouble(score), Integer.parseInt(gameId), Double.parseDouble(longitude), Double.parseDouble(latitude), questionText));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("utils.ServiceHandler", "Couldn't get any data from the url");
        }
    }
}
