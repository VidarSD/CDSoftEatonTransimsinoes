package com.example.pokayokeeatonv2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pokayokeeatonv2.Modelos.ModeloBD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements config.DatosCuadroDialogo{
    EditText edUser, edPass;
    ArrayList<String> fechas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edUser = findViewById(R.id.edtUSER);
        edPass = findViewById(R.id.edtPass);
        edUser.requestFocus();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
        }
        crearUserDefault();

        try{
            LimpiarXFecha();
            checkEstado();
        }
        catch(Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            copyAppDbToDownloadFolder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    private void checkEstado(){
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        BD.execSQL("Delete From Etiqueta where Estado = 0");
        BD.close();
    }
    private Date nextWeek(String Fecha, int dias){
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = fecha.parse(Fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }
    private void LimpiarXFecha(){
        Date date = new Date();
        DateFormat fechaActual = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = fechaActual.format(date);
        //String currentDate = "08/07/2021";
        fechas = getDates();
        boolean estado = false;
        if(fechas != null){
            for(int i = 0; i < fechas.size(); i++){
                String _auxFechaInicio = fechas.get(i);
                for(int j = 0; j < 7 ; j++ ){
                    Date _auxweekAfter = nextWeek(_auxFechaInicio, j);
                    DateFormat _auxFechaAfter = new SimpleDateFormat("dd/MM/yyyy");
                    String _auxFechaComparacion = _auxFechaAfter.format(_auxweekAfter);
                    if(currentDate.equals(_auxFechaComparacion)){
                        estado = true;
                        break;
                    }
                    else{
                        estado = false;
                    }
                }
                if(!estado){
                    ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
                    SQLiteDatabase BD = adminBD.getWritableDatabase();
                    BD.execSQL("Delete from Etiqueta where fechaInsercion = '" + _auxFechaInicio + "'" );
                    BD.close();
                }
            }
        }
    }
    private ArrayList<String> getDates(){
        ArrayList<String> fechas = new ArrayList<>();
        fechas.clear();
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        Cursor fila = BD.rawQuery("SELECT DISTINCT fechaInsercion from Etiqueta", null);
        if(fila.moveToFirst()){
            do{
                fechas.add(fila.getString(0));
            }
            while(fila.moveToNext());
        }
        BD.close();
        return fechas;
    }
    public void Log(View v){
        String usuario = "";
        String pass = "";
        String query = "";
        try{
            ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
            SQLiteDatabase BD = adminBD.getWritableDatabase();
            if(!(edUser.getText().toString() == "") || !(edPass.getText().toString() == "")){
                query = "Select * from usuarios where nombre = '" + edUser.getText().toString() + "' and Pass = '" + edPass.getText().toString() + "'";
            }
            else{
                query = "Select * from usuarios where nombre = '' and Pass = ''";
            }
            Cursor fila = BD.rawQuery(query, null);
            if(fila.moveToNext()){
                usuario = fila.getString(1);
                pass = fila.getString(2);
            }
            BD.close();
            if(!(usuario.isEmpty()) || !(pass.isEmpty())){
                Intent intent = new Intent(this, Packing.class);
                startActivity(intent);
            }
            else
                Toast.makeText(this, "Sin acceso", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Toast.makeText(this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void config(View v){
        config con = new config();
        con.CuadroDialogo(this, MainActivity.this);
    }
    @Override
    public void resultadoCuadroDialogo(String user, String pass) {
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        ContentValues add = new ContentValues();
        add.put("nombre", user);
        add.put("Pass", pass);
        BD.insert("usuarios", null, add);
        BD.close();
    }
    private void crearUserDefault(){
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        ContentValues add = new ContentValues();
        Cursor fila = BD.rawQuery("Select * from usuarios", null);
        if(!fila.moveToFirst()){
            add.put("nombre", "Test");
            add.put("Pass", "Test");
            BD.insert("usuarios", null, add);
        }
        BD.close();
    }



    public void copyAppDbToDownloadFolder() throws IOException {
        try {
            File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EatonTransimisiones"); // for example "my_data_backup.db"
            File currentDB = getApplicationContext().getDatabasePath("Eaton"); //databaseName=your current application database name, for example "my_data.db"
            if (((File) currentDB).exists()) {
                FileInputStream fis = new FileInputStream(currentDB);
                FileOutputStream fos = new FileOutputStream(backupDB);
                fos.getChannel().transferFrom(fis.getChannel(), 0, fis.getChannel().size());
                // or fis.getChannel().transferTo(0, fis.getChannel().size(), fos.getChannel());
                fis.close();
                fos.close();
                Log.i("Database successfully", " copied to download folder");
                Toast.makeText(this, "Database successfully copied to download folder", Toast.LENGTH_SHORT).show();
               // return true;
            } else   Toast.makeText(this, "DB NOT FOUND ", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
//            Log.d("Copying Database", "fail, reason:", e);
            Toast.makeText(this, "Exporting DB has failed " +e, Toast.LENGTH_LONG).show();
        }
    }
}