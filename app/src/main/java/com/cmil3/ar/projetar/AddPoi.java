package com.cmil3.ar.projetar;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import static com.cmil3.ar.projetar.R.layout.activity_note;

public class AddPoi extends Activity implements LocationListener{

    private LocationManager lm;

    private double latitude;
    private double longitude;

    //send new POI data to php script
    public void sendPOI(View activity_note) {
        //get name from form
        final EditText nameField = (EditText) findViewById(R.id.EditTextName);
        String nom = nameField.getText().toString();
        //get description from form
        final EditText feedbackField = (EditText) findViewById(R.id.EditTextDesc);
        String description = feedbackField.getText().toString();


        Uri uri = Uri.parse("http://www.lirmm.fr/campusar/add.php?lon="+longitude+"&lat="+latitude+"&des="+description+"&nom="+nom);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        WebView webview = new WebView(this);
        setContentView(webview);

        // Simplest usage: note that an exception will NOT be thrown
        // if there is an error loading this page (see below).
        // webview.loadUrl("http://slashdot.org/");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_note);

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}