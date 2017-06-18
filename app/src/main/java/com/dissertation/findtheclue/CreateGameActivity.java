package com.dissertation.findtheclue;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateGameActivity extends AppCompatActivity {

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

    String selectedImagePath;
    ScrollView createGameScrollView;
    EditText createGameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

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
}
