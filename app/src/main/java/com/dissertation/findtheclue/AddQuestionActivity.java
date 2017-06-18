package com.dissertation.findtheclue;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import utils.GPS;

public class AddQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        Button placeMarkerButton = (Button) findViewById(R.id.add_location);
        placeMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Location loc = GPS.getLastLocation(getApplicationContext());
                    Double myLongitude = loc.getLongitude();
                    Double myLatitude = loc.getLatitude();
                    //using google maps you will create a map setting lat & long.
                    String urlAddress = "http://maps.google.com/maps?q="+ myLatitude +"," + myLongitude +"("+ "Location" + ")&iwloc=A&hl=es";
                    //second option:: urlAddress = "http://maps.googleapis.com/maps/api/streetview?size=500x500&location=" + myLatitude + "," + myLongitude + "&fov=90&heading=235&pitch=10&sensor=false";
                    //third option:: urlAddress = "geo:<" + myLatitude + ">,<" + myLongitude +">?q=<" + latitude + ">,<" + longitude +">(this is my currently location)"
                    Intent maps = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));
                    startActivity(maps);
                } catch (Exception e) {
                    //Log.e(TAG, e.toString());
                }
            }
        });
    }
}
