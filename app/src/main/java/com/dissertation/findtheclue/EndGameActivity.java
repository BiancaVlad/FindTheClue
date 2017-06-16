package com.dissertation.findtheclue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class EndGameActivity extends AppCompatActivity {

    //final private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        final RatingBar showRatingBar = (RatingBar) findViewById(R.id.end_game_rating);
        showRatingBar.setEnabled(true);
        showRatingBar.setClickable(true);
        showRatingBar.setRating(0);
        showRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                String rateValue = String.valueOf(showRatingBar.getRating());
                System.out.println("Rate for Module is"+rateValue);
            }
        });
    }

}
