package com.dissertation.findtheclue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import model.GamesAdapter;
import model.GamesContent;
import model.QuestionContent;

public class EndGameActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private double ratingValue;
    private int ratingCounter;
    private boolean alreadyRated;
    private GamesContent.GameItem currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        //to-do: set the score for the current user
        TextView scoreView = (TextView) findViewById(R.id.score_view);
        scoreView.setText(Double.toString(QuestionContent.score));

        ratingBar = (RatingBar) findViewById(R.id.end_game_rating);
        ratingBar.setEnabled(true);
        ratingBar.setClickable(true);
        alreadyRated = false;
        currentGame = null;
        ratingValue = 0;
        ratingCounter = 0;

        if (getIntent().hasExtra("gameId")) {
            int gameID = getIntent().getIntExtra("gameId", 0);
            if (gameID != 0) {
                currentGame = GamesContent.getGameById(gameID);

                if (currentGame != null) {
                    ratingValue = currentGame.getRating();
                    ratingCounter = currentGame.getRatingCounter();
                }
            }
        }

        ratingBar.setRating((float)ratingValue);

        // Set ChangeListener to Rating Bar
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if(fromUser && !alreadyRated) {
                    ratingValue = ((ratingValue * ratingCounter) + rating)/ (++ratingCounter);
                    ratingBar.setRating((float)ratingValue);
                    Toast.makeText(getApplicationContext(), "Thank you! Your rating  : " + String.valueOf(rating), Toast.LENGTH_LONG).show();
                    alreadyRated = true;

                    currentGame.setRating(ratingValue);
                    currentGame.setRatingCounter(ratingCounter);

                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPut httpPUT = new
                                HttpPut("http://findtheclue.azurewebsites.net/api/Games/" + currentGame.getId_game());
                        String json = "";
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id_game",Integer.toString(currentGame.getId_game()));
                        jsonObject.put("name",currentGame.getName());
                        jsonObject.put("country", currentGame.getCountry());
                        jsonObject.put("city", currentGame.getCity());
                        jsonObject.put("difficulty", Integer.toString(currentGame.getDifficulty()));
                        jsonObject.put("rating", Double.toString(currentGame.getRating()));
                        jsonObject.put("picture", currentGame.getPicture());
                        jsonObject.put("duration", Integer.toString(currentGame.getDuration()));
                        jsonObject.put("description", currentGame.getDescription());
                        jsonObject.put("rating_counter", Integer.toString(currentGame.getRatingCounter()));
                        json = jsonObject.toString();
                        StringEntity se = new StringEntity(json);
                        httpPUT.setEntity(se);
                        httpPUT.setHeader("Accept", "application/json");
                        httpPUT.setHeader("Content-type", "application/json");
                        HttpResponse httpResponse = httpclient.execute(httpPUT);
                    } catch (Exception e) {
                        Log.d("InputStream", e.getLocalizedMessage());
                    }
                }

                if(!alreadyRated) {
                    ratingBar.refreshDrawableState();
                }

                if(alreadyRated)
                {
                    ratingBar.setIsIndicator(true);
                }
            }
        });

    }

}
