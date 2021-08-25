package com.example.pokayokeeatonv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void Escaner(View v){
        Intent intent = new Intent(this, Packing.class);
        startActivity(intent);
    }
    public void Archivos(View v){
        Intent intent = new Intent(this, Archivos.class);
        startActivity(intent);
    }
}