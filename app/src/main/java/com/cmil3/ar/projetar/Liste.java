package com.cmil3.ar.projetar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Liste extends AppCompatActivity {

    private void init(){
        Button back = (Button) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent retour = new Intent(Liste.this, MainActivity.class);

                startActivity(retour);
            }
        });
    }

    public void afficherPOI(){
        for (int i = 0; i < 20; i++){
            System.out.println("Test");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        init();
        afficherPOI();
    }


}
