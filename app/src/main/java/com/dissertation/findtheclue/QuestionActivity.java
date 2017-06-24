package com.dissertation.findtheclue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.Normalizer;

public class QuestionActivity extends AppCompatActivity
    implements OnMapReadyCallback{

    Button submitAnswerBtn;
    String answer;
    String correctAnswer;
    EditText answerText;
    AppCompatButton checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
/*        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_question, null, false);
        drawer.addView(contentView, 0);*/

        answerText = (EditText) findViewById(R.id.answer_text);
        answerText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new CheckAnswer(QuestionActivity.this).execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.setViews(savedInstanceState);

        checkBtn = (AppCompatButton) findViewById(R.id.check_answer_btn);

        correctAnswer = QuestionContent.ITEMS.get(QuestionContent.questionCounter).getTextAnswer().toLowerCase().trim();

        submitAnswerBtn = (Button) findViewById(R.id.test_answer_btn);

        submitAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                answer = answerText.getText().toString().toLowerCase().trim();

                answer = Normalizer.normalize(answer, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                if(correctAnswer.equalsIgnoreCase(answer))
                {
                    QuestionContent.score += QuestionContent.ITEMS.get(QuestionContent.questionCounter).getScore();
                    if(QuestionContent.questionCounter != QuestionContent.ITEMS.size() - 1)
                    {
                        QuestionContent.questionCounter ++;
                        Intent intent = new Intent(v.getContext(), QuestionActivity.class);
                        v.getContext().startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(v.getContext(), EndGameActivity.class);
                        intent.putExtra("gameId", QuestionContent.ITEMS.get(QuestionContent.questionCounter).getGameId());
                        v.getContext().startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
                    builder.setTitle("Your answer is not correct. \nGo to next question?");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(QuestionContent.questionCounter != QuestionContent.ITEMS.size() - 1)
                            {
                                QuestionContent.questionCounter ++;
                                Intent intent = new Intent(v.getContext(), QuestionActivity.class);
                                v.getContext().startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent = new Intent(v.getContext(), EndGameActivity.class);
                                intent.putExtra("gameId", QuestionContent.ITEMS.get(QuestionContent.questionCounter).getGameId());
                                v.getContext().startActivity(intent);
                                finish();
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //TODO
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    //dialog.getWindow().setLayout(1000, 750);
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
        mMap.addMarker(new MarkerOptions().position(markerPos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
       // mMap.addMarker(new MarkerOptions().position(markerPos));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 16f));
    }

    @Override
    public void onBackPressed()
    {
        createAndShowAlertDialog();
        //super.onBackPressed();  // optional depending on your needs
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to leave this game?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class CheckAnswer extends AsyncTask<String, Integer, Long> {
        private Context context;
        private ProgressDialog nDialog;

        private Activity activity;
        ProgressDialog progressDialog;


        public CheckAnswer(Activity activity) {
            this.activity = activity;
        }


        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... arg0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (answerText.getText() != null) {
                        answer = answerText.getText().toString().toLowerCase().trim();
                        answer = Normalizer.normalize(answer, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                        if (correctAnswer.equalsIgnoreCase(answer)) {
                            checkBtn.setBackgroundResource(R.mipmap.ic_done_black_24dp);
                            checkBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.green));
                            answerText.setEnabled(false);
                        }
                    }
                }
            });

            return new Long(0);
        }

        @Override
        protected void onPostExecute(Long result) {
            //nDialog.dismiss();
            //showProgress(false);
        }
    }
}
