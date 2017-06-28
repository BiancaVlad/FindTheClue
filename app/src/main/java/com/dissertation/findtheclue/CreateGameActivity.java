package com.dissertation.findtheclue;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import utils.BlobGettingStartedTask;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import model.GamesAdapter;
import model.GamesContent;
import model.QuestionContent;
import model.QuestionsAdapter;
import utils.OnTaskCompleted;
import utils.RestClient;

public class CreateGameActivity extends SideMenuActivity
implements OnTaskCompleted{

    AppCompatButton addPicture;
    AppCompatButton addQuestionBtn;
    AppCompatButton removeQuestionBtn;
    AppCompatButton moveUpBtn;
    AppCompatButton moveDownBtn;

    ImageView img_logo;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;
    int selectedPosition;

    String selectedImagePath;
    ScrollView createGameScrollView;
    EditText createGameEditText;
    ListView listView;
    ArrayAdapter<QuestionContent.QuestionItem> adapter;
    View previousSelectedItem;
    EditText gameName;
    EditText gameDescription;
    Spinner spinnerDifficulty;
    NumberPicker hourPicker;
    NumberPicker minutesPicker;

    private static String url = "http://findthecluebe.azurewebsites.net/api/games";

    // JSON Node names
    private static final String TAG_ID = "Id";
    private static final String TAG_NAME = "Name";
    private static final String TAG_COUNTRY = "Country";
    private static final String TAG_DESCRIPTION = "Description";
    private static final String TAG_CITY = "City";
    private static final String TAG_DIFFICULTY = "Difficulty";
    private static final String TAG_RATING = "Rating";
    private static final String TAG_RATING_COUNTER = "RatingCounter";
    private static final String TAG_PICTURE = "PictureUrl";
    private static final String TAG_DURATION = "Duration";

    private static final String TAG_QUESTION_ID = "Id";
    private static final String TAG_QUESTION_TEXT = "QuestionText";
    private static final String TAG_QUESTION_ANSWER = "Answer";
    private static final String TAG_QUESTION_TEXT_ANSWER = "TextAnswer";
    private static final String TAG_QUESTION_ANSWER_TYPE = "AnswerType";
    private static final String TAG_QUESTION_SCORE = "Score";
    private static final String TAG_QUESTION_GAME_ID = "GameId";
    private static final String TAG_QUESTION_LONGITUDE = "Longitude";
    private static final String TAG_QUESTION_LATITUDE = "Latitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_create_game);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_create_game, null, false);
        drawer.addView(contentView, 0);

        Button returnBtn = (Button) findViewById(R.id.return_game_btn);
        returnBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!createdQuestions) {
                    createAndShowAlertDialog();
                }
                else
                {
                    QuestionsAdapter.GameQuestions.clear();
                    qCounter = 0;
                    Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
                    getApplicationContext().startActivity(intent);
                    finish();
                }
            }
        }));

        hourPicker = (NumberPicker) findViewById(R.id.hour_nr);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(10);

        minutesPicker = (NumberPicker) findViewById(R.id.minutes_nr);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        spinnerDifficulty = (Spinner) findViewById(R.id.spinner_difficulty);
        String[] items = new String[]{"Begginer", "Amateur", "Intermediate", "Advanced", "Expert"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerDifficulty.setAdapter(spinnerAdapter);

        listView = (ListView) findViewById(R.id.question_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                view.setSelected(true);
                if(previousSelectedItem != null)
                {
                    previousSelectedItem.setBackgroundColor(Color.TRANSPARENT);
                }

                previousSelectedItem = view;
                selectedPosition = position;
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

        gameName = (EditText) findViewById(R.id.create_game_name);
        gameDescription = (EditText) findViewById(R.id.create_game_description);

        final Button addGame = (Button) findViewById(R.id.add_game_btn);
        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(gameName.getText().toString())) {
                    gameName.setError(getString(R.string.error_field_required));
                    return;
                }
                if (TextUtils.isEmpty(gameDescription.getText().toString())) {
                    gameDescription.setError(getString(R.string.error_field_required));
                    return;
                }
                if (TextUtils.isEmpty(gameDescription.getText().toString())) {
                    gameDescription.setError(getString(R.string.error_field_required));
                    return;
                }

                if(QuestionsAdapter.GameQuestions == null || QuestionsAdapter.GameQuestions.size() < 1)
                {
                    Toast.makeText(CreateGameActivity.this, "You have to add at least one question to this game.", Toast.LENGTH_LONG).show();
                    return;
                }

                addGame.setEnabled(false);

                try {
                    addGameListCall();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //add game and then get the last added game id;

                //finish();
            }
        });

        adapter = new ArrayAdapter<QuestionContent.QuestionItem>(this,android.R.layout.simple_selectable_list_item, QuestionsAdapter.GameQuestions);
        listView.setAdapter(adapter);

        createGameEditText = (EditText) findViewById(R.id.create_game_description);

        createGameScrollView = (ScrollView) findViewById(R.id.create_game_scrollview);
        createGameScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                createGameEditText.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        createGameEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                createGameEditText.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        img_logo= (ImageView) findViewById(R.id.add_game_picture);

        addPicture = (AppCompatButton) findViewById(R.id.add_pic_btn);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

        addPicture.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    addPicture.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.colorAccent));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    addPicture.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.darkGrey));
                }
                return false;
            }
        });

        addQuestionBtn = (AppCompatButton) findViewById(R.id.add_question_btn);

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddQuestionActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        addQuestionBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    addQuestionBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.colorAccent));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    addQuestionBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.darkGrey));
                }
                return false;
            }
        });

        removeQuestionBtn = (AppCompatButton) findViewById(R.id.remove_question_btn);

        removeQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to-do add question
            }
        });

        removeQuestionBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    removeQuestionBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.colorAccent));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    removeQuestionBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.darkGrey));
                }
                return false;
            }
        });

        moveDownBtn = (AppCompatButton) findViewById(R.id.move_down_question_btn);

        moveDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to-do add question
            }
        });

        moveDownBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    moveDownBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.colorAccent));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    moveDownBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.darkGrey));
                }
                return false;
            }
        });

        moveUpBtn = (AppCompatButton) findViewById(R.id.move_up_question_btn);

        moveUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to-do add question
            }
        });

        moveUpBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    moveUpBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.colorAccent));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    moveUpBtn.setSupportBackgroundTintList(ContextCompat.getColorStateList(getApplication(), R.color.darkGrey));
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        if(!createdQuestions) {
            createAndShowAlertDialog();
        }
        else
        {
            QuestionsAdapter.GameQuestions.clear();
            qCounter = 0;
            Intent intent = new Intent(getApplicationContext(), GamesListActivity.class);
            getApplicationContext().startActivity(intent);
            finish();
        }
        //super.onBackPressed();  // optional depending on your needs
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to leave the game creation?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                QuestionsAdapter.GameQuestions.clear();
                qCounter = 0;
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

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);
                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));

                        startActivityForResult(intent,
                                CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getBaseContext(),

                        "Error while capturing image", Toast.LENGTH_LONG)

                        .show();

                return;

            }

            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);

                img_logo.setImageBitmap(bitmap);

                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                if (selectedImagePath != null) {
                    //txt_image_path.setText(selectedImagePath);
                }

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                img_logo.setImageBitmap(bitmap);


            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void addGameListCall() throws JSONException, UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            String gameNameText = gameName.getText().toString().trim();
            gameNameText = Normalizer.normalize(gameNameText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            jsonObject.put(TAG_NAME, gameNameText);

            String gameDescriptionText = gameDescription.getText().toString().trim();
            gameDescriptionText = Normalizer.normalize(gameDescriptionText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            jsonObject.put(TAG_DESCRIPTION, gameDescriptionText);

            Geocoder geocoder = new Geocoder(CreateGameActivity.this, Locale.getDefault());
            double latitude = QuestionsAdapter.GameQuestions.get(0).getLatitude();
            double longitude = QuestionsAdapter.GameQuestions.get(0).getLongitude();
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String cityName = addresses.get(0).getAddressLine(1);
            cityName = Normalizer.normalize(cityName, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            jsonObject.put(TAG_CITY, cityName);

            String countryName = addresses.get(0).getAddressLine(2);
            countryName = Normalizer.normalize(countryName, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

            jsonObject.put(TAG_COUNTRY, countryName);

            if(spinnerDifficulty.getSelectedItem() != null) {
                jsonObject.put(TAG_DIFFICULTY, spinnerDifficulty.getSelectedItemId());
            }

            int duration = hourPicker.getValue() * 60 + minutesPicker.getValue();
            jsonObject.put(TAG_DURATION, duration);
            double rating = 0.0;
            jsonObject.put(TAG_RATING, rating);
            jsonObject.put(TAG_RATING_COUNTER, 0);
            jsonObject.put(TAG_PICTURE, null);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(selectedImagePath != null && !selectedImagePath.isEmpty()) {
            UUID uniqueId = UUID.randomUUID();
            new BlobGettingStartedTask(uniqueId.toString(), selectedImagePath, jsonObject, this).execute();
        }
        else {
            onTaskCompleted(jsonObject);
        }
    }

    @Override
    public void onTaskCompleted(JSONObject jsonObject) {

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        RestClient.post(getApplicationContext(), "games", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                //Toast.makeText(CreateGameActivity.this, "Your game was created!", Toast.LENGTH_LONG).show();
                try {
                    String id = response.getString(TAG_ID);
                    postQuestionsToGame(Integer.parseInt(id));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray games) {
                //Toast.makeText(CreateGameActivity.this, "Your game was created!", Toast.LENGTH_LONG).show();

                //showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(CreateGameActivity.this, "Your could NOT be created!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(CreateGameActivity.this, "Your could NOT be created!", Toast.LENGTH_LONG).show();
            }
        });
    }
    private boolean createdQuestions;
    private int qCounter = 0;

    private StringEntity getStringEntity(int id, double score) {
        JSONObject jsonObject = new JSONObject();
        StringEntity entity = null;

        try {
            QuestionContent.QuestionItem currentQuestion = QuestionsAdapter.GameQuestions.get(qCounter);
            jsonObject.put(TAG_QUESTION_GAME_ID, id);
            jsonObject.put(TAG_QUESTION_LATITUDE, currentQuestion.getLatitude());
            jsonObject.put(TAG_QUESTION_LONGITUDE, currentQuestion.getLongitude());
            jsonObject.put(TAG_QUESTION_TEXT, currentQuestion.getQuestionText());
            jsonObject.put(TAG_QUESTION_TEXT_ANSWER, currentQuestion.getTextAnswer());
            jsonObject.put(TAG_QUESTION_SCORE, score);
            jsonObject.put(TAG_QUESTION_ANSWER_TYPE, 0);
            jsonObject.put(TAG_QUESTION_ANSWER, "empty");

            entity = new StringEntity(jsonObject.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (Exception ex) {
        }

        return entity;
    }

    private void postQuestionsToGame(final int id)
    {
        final double score = 100 / QuestionsAdapter.GameQuestions.size();

        if(QuestionsAdapter.GameQuestions.size() > 0) {

                StringEntity entity = getStringEntity(id, score);
            if(entity != null) {
                RestClient.post(getApplicationContext(), "questions", entity, "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (qCounter < QuestionsAdapter.GameQuestions.size() - 1) {
                            qCounter++;
                            postQuestionsToGame(id);
                        }
                        // If the response is JSONObject instead of expected JSONArray
                        //Toast.makeText(CreateGameActivity.this, "Your game was created!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray games) {
                        //Toast.makeText(CreateGameActivity.this, "Your game was created!", Toast.LENGTH_LONG).show();

                        //showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(CreateGameActivity.this, "Your game could NOT be created!", Toast.LENGTH_LONG).show();
                        createdQuestions = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(CreateGameActivity.this, "Your game could NOT be created!", Toast.LENGTH_LONG).show();
                        createdQuestions = false;
                    }
                });

                if (createdQuestions && qCounter == QuestionsAdapter.GameQuestions.size() - 1) {
                    Toast.makeText(CreateGameActivity.this, "Your game was created!", Toast.LENGTH_LONG).show();
                    QuestionsAdapter.GameQuestions.clear();
                }
            }
        }
    }
}
