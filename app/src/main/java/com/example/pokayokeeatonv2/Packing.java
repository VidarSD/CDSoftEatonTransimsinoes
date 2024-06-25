package com.example.pokayokeeatonv2;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pokayokeeatonv2.Modelos.ModeloBD;

import java.util.ArrayList;

public class Packing extends AppCompatActivity {
    EditText edtCodigo, edNoDelivery, edNoPzas;
    Spinner cbCliente, cbDestino;
    Button btCodigo, btPiezas;
    ArrayList<String> lClientes, lDestinos;
    ArrayAdapter<String> adapter;
    long Folio = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing);
        edtCodigo = findViewById(R.id.edtCodigo);
        edNoDelivery = findViewById(R.id.edNoDelivery);
        edNoPzas = findViewById(R.id.edNoPzas);
        cbCliente = findViewById(R.id.cbCliente);
        cbDestino = findViewById(R.id.cbDestino);
        btCodigo = findViewById(R.id.btCodigo);
        btPiezas = findViewById(R.id.btPiezas);
        edtCodigo.requestFocus();
        lClientes = new ArrayList<String>();
        lDestinos = new ArrayList<String>();
        edNoDelivery.setEnabled(false);
        edNoPzas.setEnabled(false);
        btPiezas.setEnabled(false);

        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("Ftype","A");
        Folio = BD.insert("Folio",null,cv);
        Toast.makeText(this, String.valueOf(Folio)+"Folio ", Toast.LENGTH_LONG).show();
        Log.v("El folio",String.valueOf(Folio));




        validar();


        cbCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position +=1;
                llenarCB(cbDestino, lDestinos, "destinos", "nombre", position);
                edNoDelivery.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }
    @Override
    public void onBackPressed(){

    }
    private void llenarCB(Spinner combo, ArrayList<String> lista, String tabla, String columna, int Id){
        try{
            Clientes();
            Destinos();
            ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
            SQLiteDatabase BD = adminBD.getWritableDatabase();
            Cursor fila = BD.rawQuery("Select  * from " + tabla, null);
            if(Id > 0){
                lDestinos.clear();
                if(lClientes.size() > 0)
                    fila = BD.rawQuery("Select " + columna + " from " + tabla + " where IDCliente = "+ Id, null);
            }
            else
                fila = BD.rawQuery("Select " + columna + " from " + tabla, null);
            if(fila.moveToFirst()){
                do{
                    lista.add(fila.getString(0));
                }
                while(fila.moveToNext());
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lista);
            combo.setAdapter(adapter);
        }
        catch (Exception ex){
            Toast.makeText(this, "Error Llenando Clientes" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void Clientes(){
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        Cursor fila = BD.rawQuery("Select * from clientes", null);
        if(!fila.moveToFirst()){
            ContentValues add = new ContentValues();
            //1
            add.put("Nombre", "International");
            BD.insert("clientes", null, add);
            //2
            add.put("Nombre", "Kenworth");
            BD.insert("clientes", null, add);
            //3
            add.put("Nombre", "Paccar Peterbilt");
            BD.insert("clientes", null, add);
            //4
            add.put("Nombre", "DTNA /Freightliner");
            BD.insert("clientes", null, add);
            //5
            add.put("Nombre", "Intercompanias");
            BD.insert("clientes", null, add);
            //6
            add.put("Nombre", "Mack");
            BD.insert("clientes", null, add);
            //7
            add.put("Nombre", "Volvo");
            BD.insert("clientes", null, add);
            //8
            add.put("Nombre", "Blue Bird");
            BD.insert("clientes", null, add);

            add.put("Nombre", "ELC");
            BD.insert("clientes", null, add);
        }
        BD.close();
    }
    private void Destinos(){
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        Cursor fila = BD.rawQuery("Select * from Destinos", null);
        if(!fila.moveToFirst()) {
            ContentValues add = new ContentValues();
            add.put("Nombre", "Escobedo");
            add.put("IDCliente", 1);
            add.put("NoEtiquetas", 4);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Blue Diamond");
            add.put("IDCliente", 1);
            add.put("NoEtiquetas", 4);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Springfield");
            add.put("IDCliente", 1);
            add.put("NoEtiquetas", 4);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Spring 3PL");
            add.put("IDCliente", 1);
            add.put("NoEtiquetas", 4);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Mexicali");

            add.put("IDCliente", 2);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "MD St. Therese");
            add.put("IDCliente", 2);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Renton");
            add.put("IDCliente", 2);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);

            add.put("Nombre", "Denton");
            add.put("IDCliente", 3);
            add.put("NoEtiquetas", 3);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Chillicothe");
            add.put("IDCliente", 3);
            add.put("NoEtiquetas", 3);
            BD.insert("destinos", null, add);

            add.put("Nombre", "Cleveland");
            add.put("IDCliente", 4);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Portland");
            add.put("IDCliente", 4);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Santiago");
            add.put("IDCliente", 4);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Saltillo");
            add.put("IDCliente", 4);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Stalesville");
            add.put("IDCliente", 4);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);

            add.put("Nombre", "Servicios  /AFM");
            add.put("IDCliente", 5);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Greenfield");
            add.put("IDCliente", 5);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Brasil");
            add.put("IDCliente", 5);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Australia");
            add.put("IDCliente", 5);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
            add.put("Nombre", "Galesburg");
            add.put("IDCliente", 5);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);

            add.put("Nombre", "Mack");
            add.put("IDCliente", 6);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);

            add.put("Nombre", "Salem");
            add.put("IDCliente", 7);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);

            add.put("Nombre", "Blue Bird");
            add.put("IDCliente", 8);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);

            add.put("Nombre", "ELC");
            add.put("IDCliente", 9);
            add.put("NoEtiquetas", 2);
            BD.insert("destinos", null, add);
        }
        BD.close();
    }
    public void btnCodigoBarras_Click(View v){
        validaCodigoBarras();
    }
    private void validaCodigoBarras(){
        if(!(edtCodigo.getText().toString().isEmpty())){
            llenarCB(cbCliente, lClientes, "clientes", "nombre", 0);
            btCodigo.setEnabled(false);
            edtCodigo.setEnabled(false);
            edNoDelivery.setFocusable(true);
            edNoDelivery.requestFocus();
        }
    }
    public void btLimpiar_Click(View v){
        btCodigo.setEnabled(true);
        edtCodigo.setEnabled(true);
        edNoPzas.setEnabled(false);
        edNoPzas.setEnabled(false);
        cbCliente.setAdapter(null);
        cbDestino.setAdapter(null);
        lClientes.clear();
        lDestinos.clear();
        edtCodigo.setText("");
        edNoDelivery.setText("");
        edNoPzas.setText("");
        edtCodigo.setFocusable(true);
        edtCodigo.requestFocus();
    }
    public void btPiezas_Click(View v){
        validaPiezas();
    }
    private void validaPiezas(){
        if(!edNoDelivery.getText().equals("")){
            ArrayList <String> Datos = new ArrayList<>();
            Datos.add(cbDestino.getSelectedItem().toString());
            Datos.add(edNoDelivery.getText().toString());
            Datos.add(edNoPzas.getText().toString());
            Datos.add(edtCodigo.getText().toString());
            Datos.add(cbCliente.getSelectedItem().toString());
            Datos.add(String.valueOf(Folio));
            Intent intent = new Intent(this, Lectura.class);
            intent.putExtra("datos", Datos);
            startActivity(intent);
            finish();
        }
    }
    private void validar(){
        edtCodigo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    validaCodigoBarras();
                }
                return false;
            }
        });
        edNoDelivery.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    edNoPzas.setEnabled(true);
                    btPiezas.setEnabled(true);
                }
                return false;
            }
        });
        edNoPzas.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    validaPiezas();
                }
                return false;
            }
        });


    }
}