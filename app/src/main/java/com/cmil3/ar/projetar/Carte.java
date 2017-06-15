package com.cmil3.ar.projetar;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Carte extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carte);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;

        // Marqueur sur la faculté des sciences
        LatLng fds = new LatLng(43.632057, 3.864793);
        LatLng bat1_4 = new LatLng(43.633934, 3.861605);
        LatLng entree = new LatLng(43.631460, 3.861268);
        LatLng administration = new LatLng(43.631632, 3.863365);
        LatLng ru = new LatLng(43.631206, 3.860192);

        //ajout des marqueur sur la carte
        mMap.addMarker(new MarkerOptions().position(bat1_4).title("Batiment 1 à 4"));
        mMap.addMarker(new MarkerOptions().position(entree).title("Entrée principale"));
        mMap.addMarker(new MarkerOptions().position(administration).title("Administation"));
        mMap.addMarker(new MarkerOptions().position(ru).title("Restaurant Universitaire"));

        //déplacement de la caméra sur la carte au niveau de la fds
        mMap.moveCamera(CameraUpdateFactory.newLatLng(fds));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        //choix type carte (4 = satellite avec tracé des routes)
        mMap.setMapType(4);

        // Instantiates a new Polyline object and adds points to define campus
        Polyline campus = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(43.628287, 3.868336))
                .add(new LatLng(43.630572, 3.866346))
                .add(new LatLng(43.631123, 3.861454))
                .add(new LatLng(43.635637, 3.859745))
                .add(new LatLng(43.635905, 3.861323))
                .add(new LatLng(43.633928, 3.863511))
                .add(new LatLng(43.634635, 3.866919))
                .add(new LatLng(43.634670, 3.868000))
                .add(new LatLng(43.632384, 3.868094))
                .add(new LatLng(43.629655, 3.869856))
                .add(new LatLng(43.628287, 3.868336))
                .color(Color.rgb(53, 122, 183)));



        // ajout d'un marqueur sur ma position actuelle
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
    }
}
