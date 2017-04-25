package com.cmil3.ar.projetar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private ArchitectView architectView;

    private LocationManager lm;

    private double latitude;
    private double longitude;
    private double altitude = 0;
    private float accuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // démarrage du button listener pour la liste
        buttonFonction();

        this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey("upoJVHRJU99JXtNViBd0ncWRV+udr0NNNdCJsbU3kZOaibssZU1hT41oIvz2QsaGpbEe+JBNlVJV1raote0uX8uDS7CGc//YMzAj8ZN4Cddn8oCY3gIGBq9AsGJkwXZ4+qWJQjuqivgsVIJr5O4dAZRmFufEpCtqoEp8ouvVSdJTYWx0ZWRfX5xY3fLVmlDlkqW3dbiuK5OgLGOkCcldja89s8nYWh6wixPYBtK8Y+ZYRg2dQGB/O/0KynWAY6ShUDj9+opF5FgN4yWUefkBf/aAwSlZaWIkKS3xcfvWR2k3XbtfngweHpZqXshZGt4rmtY/ZqhP0/1vWLLc12/oHFBXSHWDTPhFJT+KcOVbXCbzs4pTVKHyFde96liKgLavhP04q4lWziO9Sdq15/oN5Hcr2ZkWthUtaV0t8CtCAFyg58UT8s3/y61zJxM/khRQApQFvy772yyZLnmbucR8Cn8gYefXEbwySogSYGF6b9yHI79pmJB9DZU/Zq4apReSZDtOZXavgGMLV8oydmY+XX6twKyRO7pG76Z6cDVivQxm2F7pgQMCo4f8WzC9dw1darBn/WEhTh+a1JE6QfiFc9SlsnraM5ytYZCkHAN93NA3S4WMInC+x6Ktt5KZ0mIQaKq3RNkNRUldFVN+fX9pzjSEBtWK22GwAfROVAN+ziI=");
        this.architectView.onCreate(config);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        architectView.onPostCreate();

        try {
            //this.architectView.load("file:///android_asset/06_Point$Of$Interest_4_Selecting$Pois/index.html");
            //this.architectView.load("file:///android_asset/08_Browsing$Pois_1_Presenting$Details/index.html");
            this.architectView.load("file:///android_asset/08_BrowsingPois_5_NativeDetailScreen/index.html");
            architectView.setLocation(latitude, longitude, altitude, accuracy);
        } catch (Exception e) {
            System.out.println("erreur wikitude chargement asset");
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);

        architectView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        architectView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(this);

        architectView.onPause();
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = 0;
        accuracy = location.getAccuracy();

        String msg = String.format(
                getResources().getString(R.string.new_location), latitude, longitude, altitude, accuracy);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();


        architectView.setLocation(latitude, longitude, altitude, accuracy);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String newStatus = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "OUT_OF_SERVICE";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "TEMPORARILY_UNAVAILABLE";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "AVAILABLE";
                break;
        }
        String msg = String.format((getResources().getString(R.string.provider_new_status)), provider, newStatus);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderEnabled(String provider) {

        String msg = String.format(getResources().getString(R.string.provider_enabled), provider);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        architectView.setLocation(latitude, longitude, altitude, accuracy);

    }

    @Override
    public void onProviderDisabled(String provider) {
        String msg = String.format(getResources().getString(R.string.provider_disabled), provider);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //Bouton de menu pour la liste et pour la carte

    private void buttonFonction() {
        Button BListe = (Button) findViewById(R.id.button3);
        Button BCarte = (Button) findViewById(R.id.button2);
        BListe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent liste = new Intent(MainActivity.this, Liste.class);

                startActivity(liste);
            }
        });

        BCarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent carte = new Intent(MainActivity.this, Carte.class);

                startActivity(carte);
            }
        });
    }




}
