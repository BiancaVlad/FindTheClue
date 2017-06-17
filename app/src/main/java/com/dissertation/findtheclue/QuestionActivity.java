package com.dissertation.findtheclue;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import model.GamesContent;
import model.QuestionContent;

import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class QuestionActivity extends AppCompatActivity
    implements OnMapReadyCallback{

    Button submitAnswerBtn;
    String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        final EditText answerText = (EditText) findViewById(R.id.answer_text);
        this.setViews(savedInstanceState);

        submitAnswerBtn = (Button) findViewById(R.id.test_answer_btn);

        submitAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = answerText.getText().toString().toLowerCase().trim();
                String correctAnswer = QuestionContent.ITEMS.get(QuestionContent.questionCounter).getTextAnswer().toLowerCase().trim();

                if(correctAnswer.equalsIgnoreCase(answer))
                {
                    QuestionContent.score += QuestionContent.ITEMS.get(QuestionContent.questionCounter).getScore();
                }

                if(QuestionContent.questionCounter != QuestionContent.ITEMS.size() - 1)
                {
                    QuestionContent.questionCounter ++;
                    Intent intent = new Intent(v.getContext(), QuestionActivity.class);
                    v.getContext().startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(v.getContext(), EndGameActivity.class);
                    intent.putExtra("gameId", QuestionContent.ITEMS.get(QuestionContent.questionCounter).getGameId());
                    v.getContext().startActivity(intent);
                }
            }
        });
    }
    private double latitude;
    private double longitude;
    private GoogleMap mMap;
    private void setViews(Bundle savedInstanceState)
    {
        QuestionContent.QuestionItem currentQuestion = QuestionContent.ITEMS.get(QuestionContent.questionCounter);
        TextView questionView = (TextView) findViewById(R.id.question_text);
        questionView.setText(currentQuestion.getQuestionText());




        latitude = currentQuestion.getLatitude();
        longitude = currentQuestion.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng markerPos = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(markerPos));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 16f));
    }

    @Override
    public void onBackPressed()
    {
        //this.
        super.onBackPressed();  // optional depending on your needs
    }
}
