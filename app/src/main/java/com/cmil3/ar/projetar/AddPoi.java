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
        if(geoloc==true) {
            //get name from form
            final EditText nameField = (EditText) findViewById(R.id.EditTextName);
            String nom = nameField.getText().toString();
            //get description from form
            final EditText feedbackField = (EditText) findViewById(R.id.EditTextDesc);
            String description = feedbackField.getText().toString();


            Uri uri = Uri.parse("http://www.lirmm.fr/campusar/add.php?lon=" + longitude + "&lat=" + latitude + "&des=" + description + "&nom=" + nom);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

            WebView webview = new WebView(this);
            setContentView(webview);
        } else {
            String msg = "Veuillez patienter, nous essayons de vous localiser...";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_note);
/*
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        geoloc=true;
        String msg = "Trouvé! Vous pouvez valider votre note.";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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