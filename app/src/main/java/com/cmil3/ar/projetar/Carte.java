package com.cmil3.ar.projetar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Double.valueOf;

public class Carte extends FragmentActivity implements OnMapReadyCallback {


    //URL to get JSON Array
    private static String url = "http://www.lirmm.fr/campusar/poiFile.json";

    //array list which will contain all the poi to display on map
    ArrayList<String[]> poiList = new ArrayList<>();



    //initiate tool bar with buttons and actions
    private void initToolbar() {
        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.menuToolbar);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_main:
                        Intent main = new Intent(Carte.this, MainActivity.class);
                        startActivity(main);
                        break;
                    case R.id.action_carte:
                        break;
                    case R.id.action_addPoi:
                        Intent addPoi = new Intent(Carte.this, AddPoi.class);
                        startActivity(addPoi);
                        break;
                    case R.id.action_calendar:
                        Intent calen = new Intent(Carte.this, Calendar.class);
                        startActivity(calen);
                        break;
                }
                return true;
            }
        });
        // Inflate a menu to be displayed in the toolbar
        toolbarBottom.inflateMenu(R.menu.menumain);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carte);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //init menu toolbar
        initToolbar();

        //get poi from http://www.lirmm.fr/campusar/poiFile.json
        new GetPoi().execute();


    }


    /**
     * Async task class to get json by making HTTP call to http://www.lirmm.fr/campusar/poiFile.json
     */
    private class GetPoi extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Carte.this);
            pDialog.setMessage("Chargement des lieux...");
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url http://www.lirmm.fr/campusar/poiFile.json and getting response
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray jsonArr = new JSONArray(jsonStr);

                    // looping through all poi
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject c = jsonArr.getJSONObject(i);

                        //create a tab with the poi data
                        String[] obj={c.getString("name"), c.getString("latitude"), c.getString("longitude"), c.getString("description") };
                        //store the tab into arrayList
                        poiList.add(obj);


                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

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


        //adding poi from the array to the map
        for(String[] s: poiList){
            if(s[3].indexOf("Note")==0){ // if description begin with "note" i.e communautary POI
                //display marker with low opacity ( alpha O.8) and a different color (purple)
                mMap.addMarker(new MarkerOptions().position(new LatLng(valueOf(s[1]), valueOf(s[2]))).title(s[0]).snippet(s[3]).alpha(0.5f).icon(BitmapDescriptorFactory.defaultMarker(225)));
            } else {
                //else display marker with default options
                mMap.addMarker(new MarkerOptions().position(new LatLng(valueOf(s[1]), valueOf(s[2]))).title(s[0]).snippet(s[3]));

            }
        }


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context mContext = getApplicationContext();
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        //déplacement de la caméra sur la carte au niveau de la fds
        LatLng fds = new LatLng(43.632057, 3.864793);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(fds));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        //choix type carte (4 = satellite avec tracé des routes)
        mMap.setMapType(4);

        // Instantiates a new Polyline object and adds points to define campus
        Polyline campus = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(43.629593, 3.867466))//premier point extrémité sud-ouest
                .add(new LatLng(43.630262, 3.867027))// puis rotation sens aiguille de montre
                .add(new LatLng(43.630572, 3.866346))
                .add(new LatLng(43.631183, 3.862327))
                .add(new LatLng(43.631010, 3.861528))//rond point / entrée principale
                .add(new LatLng(43.631908, 3.861019))
                .add(new LatLng(43.632065, 3.860476))
                .add(new LatLng(43.634281, 3.860092))
                .add(new LatLng(43.634863, 3.862146))
                .add(new LatLng(43.633928, 3.863511))
                .add(new LatLng(43.634635, 3.866919))
                .add(new LatLng(43.634670, 3.868000))
                .add(new LatLng(43.632384, 3.868094))
                .add(new LatLng(43.629827, 3.869895))
                .add(new LatLng(43.629309, 3.869798))
                .add(new LatLng(43.630071, 3.868230))
                .add(new LatLng(43.629593, 3.867466))
                .color(Color.rgb(53, 122, 183)));


        // ajout d'un marqueur sur ma position actuelle
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}
