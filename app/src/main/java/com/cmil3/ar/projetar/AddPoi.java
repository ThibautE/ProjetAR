package com.cmil3.ar.projetar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import static com.cmil3.ar.projetar.R.layout.activity_note;

public class AddPoi extends Activity implements LocationListener {

    private LocationManager lm;
    private double latitude;
    private double longitude;

    private boolean geoloc = false;

    //send new POI data to php script
    public void sendPOI(View activity_note) {
        //get form data
        final EditText name = (EditText) findViewById(R.id.EditTextName);
        final EditText desc = (EditText) findViewById(R.id.EditTextDesc);

        //if note name is empty display toast
        if(name.getText().toString().trim().length()==0){
            String msg = "Veuillez renseigner un nom à votre note.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        //if note desc is empty display toast
        } else if(desc.getText().toString().trim().length()==0){
            String msg = "Veuillez ajouter une description à votre note.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        //if location is not detected
        } else if(geoloc==false) {
            String msg = "Veuillez patienter, nous essayons de vous localiser...";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        //if all test are OK, send POI with URL request
        } else {
            //convert name to String
            String nom = name.getText().toString();
            //convert description to String
            String description = desc.getText().toString();
            //set URL with parameters and open a web view wih it
            Uri uri = Uri.parse("http://www.lirmm.fr/campusar/add.php?lon=" + longitude + "&lat=" + latitude + "&des=" + description + "&nom=" + nom);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

            WebView webview = new WebView(this);
            setContentView(webview);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_note);

    }

    @Override
    protected void onResume() {
        super.onResume();

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //unlock the note send because location was received
        geoloc=true;
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