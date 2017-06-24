package com.dissertation.findtheclue;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import utils.GPS;

public class AddQuestionActivity extends SideMenuActivity
        implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private GoogleMap mMap;
    Geocoder geocoder;
    private LatLng latLng;
    private Marker marker;
    ScrollView addQScrollview;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add_question);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_add_question, null, false);
        drawer.addView(contentView, 0);

      /*  Location loc = GPS.getLastLocation(getApplicationContext());
        latitude = loc.getLongitude();
        longitude = loc.getLatitude();*/

        geocoder = new Geocoder(this, Locale.getDefault());
        setUpMapIfNeeded();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress().toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12f));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.i(TAG, "An error occurred: " + status);
            }
        });

        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        addQScrollview= (ScrollView) findViewById(R.id.add_question_scrollview);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        addQScrollview.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        addQScrollview.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        addQScrollview.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.add_question_map);
            mapFragment.getMapAsync(this);
            //mMap = mapFragment.

            // Check if we were successful in obtaining the map.

        }
    }

    private void setUpMap() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //save current location
                latLng = point;

                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                android.location.Address address = addresses.get(0);

                if (address != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i) + "\n");
                    }
                    Toast.makeText(AddQuestionActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                }

                //remove previously placed Marker
                if (marker != null) {
                    marker.remove();
                }

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point).title("Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

            }
        });

    }

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                //Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
// Or use LocationManager.GPS_PROVIDER

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);
        LatLng currentPosition = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        marker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("The selected location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12f));
        //LatLng markerPos = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(markerPos));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 16f));

        if (mMap != null) {
            setUpMap();
        }
    }
}
