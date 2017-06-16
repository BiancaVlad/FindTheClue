package com.dissertation.findtheclue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import model.GamesAdapter;
import model.GamesContent;

public class EndGameActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private double ratingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        ratingBar = (RatingBar) findViewById(R.id.end_game_rating);
        ratingBar.setEnabled(true);
        ratingBar.setClickable(true);

        if (getIntent().hasExtra("gameId"))
        {
            int gameID = getIntent().getIntExtra("gameId", 0);
            if(gameID != 0)
            {
                for (GamesContent.GameItem item: GamesContent.ITEMS) {
                    if(item.getId_game() == gameID)
                    {
                        ratingValue = item.getRating();
                    }
                }
            }
            else
            {
                ratingValue = 0;
            }
        }

        ratingBar.setRating((float)ratingValue);

        // Set ChangeListener to Rating Bar
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratingBar.setRating((float)ratingValue + rating / 5);
                Toast.makeText(getApplicationContext(),"Your Selected Ratings  : " + String.valueOf(rating),Toast.LENGTH_LONG).show();
            }
        });

    }

}
