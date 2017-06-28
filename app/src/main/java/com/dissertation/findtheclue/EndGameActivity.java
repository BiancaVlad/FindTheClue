package com.dissertation.findtheclue;

import android.content.Intent;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import model.GamesAdapter;
import model.GamesContent;
import model.QuestionContent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.TokenSaver;

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

        updateScore(QuestionContent.score);


        Button returnBtn = (Button) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), GamesListActivity.class);
                        v.getContext().startActivity(intent);
                        finish();
                    }
                }));

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
                                HttpPut("http://findthecluebe.azurewebsites.net/api/Games/" + currentGame.getId_game());
                        String json = "";
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Id",Integer.toString(currentGame.getId_game()));
                        jsonObject.put("Name",currentGame.getName());
                        jsonObject.put("Country", currentGame.getCountry());
                        jsonObject.put("City", currentGame.getCity());
                        jsonObject.put("Difficulty", Integer.toString(currentGame.getDifficulty()));
                        jsonObject.put("Rating", Double.toString(currentGame.getRating()));
                        jsonObject.put("PictureUrl", currentGame.getPicture());
                        jsonObject.put("Duration", Integer.toString(currentGame.getDuration()));
                        jsonObject.put("Description", currentGame.getDescription());
                        jsonObject.put("RatingCounter", Integer.toString(currentGame.getRatingCounter()));
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

    private void updateScore(double score) {
        String myUrl = "http://findthecluebe.azurewebsites.net/api/account/edit";
        MediaType jsonMedia
                = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();

        String accessToken = TokenSaver.getToken(getApplicationContext());

        try {
            jsonObject.put("Points", score);
            RequestBody body = RequestBody.create(jsonMedia, String.valueOf(jsonObject));

            Request request = new Request.Builder()
                    .url(myUrl)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("accept", "application/json")
                    .addHeader("authorization", "bearer " + accessToken)
                    .build();

            Response response = client.newCall(request).execute();

            boolean ok = false;
            if (response.isSuccessful())
            {
                ok = true;
                Log.d("InputStream",String.valueOf(ok));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
        startActivity(intent);
        finish();
        //super.onBackPressed();  // optional depending on your needs
    }
}
