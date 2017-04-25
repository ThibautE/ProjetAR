package com.cmil3.ar.projetar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Liste extends AppCompatActivity {

    private void ent(){
        Button ent = (Button) findViewById(R.id.ENT);
        ent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ent.umontpellier.fr/"));
                startActivity(browserIntent);
            }
        });
    }

    public void fds(){
        Button fds = (Button) findViewById(R.id.FDS);
        fds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sciences.edu.umontpellier.fr/"));
                startActivity(browserIntent);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        fds();
        ent();
    }


}
