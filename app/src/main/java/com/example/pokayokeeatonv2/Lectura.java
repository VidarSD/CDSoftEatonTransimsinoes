package com.example.pokayokeeatonv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokayokeeatonv2.Modelos.Escaneos;
import com.example.pokayokeeatonv2.Modelos.ModeloBD;
import com.example.pokayokeeatonv2.Modelos.Registros;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import repack.org.bouncycastle.crypto.util.Pack;

public class Lectura extends AppCompatActivity {

    String Folio;


    ArrayList<String> DatosRec;
    ArrayList<Escaneos> escaneos;
    ArrayList<Registros> Registros;
    EditText edCodigo2D, edAIAG, edLineSet;
    TextView tvCantidad;
    Button btSiguiente;
    int PiezasEscaneadas;
    boolean estado = false;
    int contador = 0;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    String currentDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura);
        //Permisos para la lectura y escritura del almacenamiento de la memoria del dispositivo
        int PERMISSION = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(PERMISSION != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        //Datos recibidos de la activity Packing
        Bundle extras = getIntent().getExtras();
        DatosRec = extras.getStringArrayList("datos");
        PiezasEscaneadas = extras.getInt("Piezas");
        //Si las piezas vienen null, se asignan a 0
        if(PiezasEscaneadas == 0){
            PiezasEscaneadas = 0;
        }
        else{
            PiezasEscaneadas = extras.getInt("Piezas");
        }



        Folio = DatosRec.get(5);

        //Declaración de los componentes para el uso de sus propiedades
        edCodigo2D = findViewById(R.id.edCodigo2D);
        edCodigo2D.requestFocus();
        //edCodigo2D.setInputType(InputType.TYPE_NULL);
        edAIAG = findViewById(R.id.edAIAG);
        edAIAG.setEnabled(false);
        edLineSet = findViewById(R.id.edLineSet);
        edLineSet.setEnabled(false);
        tvCantidad = findViewById(R.id.tvCantidad);
        btSiguiente = findViewById(R.id.btSiguiente);
        btSiguiente.setVisibility(View.INVISIBLE);
        edCodigo2D.setInputType(InputType.TYPE_NULL);
        //Arreglo que contiene los archivos txt en la ruta de la aplicación
        try{
            String[] archivos = fileList();
            crearFolder("Transmisiones");
            validar(edCodigo2D, "2D");
            validar(edAIAG, "AIAG");
            validar(edLineSet, "LineSet");
            //Verifica si se escanean 2 o 3 etiquetas
            InputFilter[] filterArray = new InputFilter[1];
            switch (IsTwo(DatosRec)){
                case 1:
                    tvCantidad.setText(" " + PiezasEscaneadas + " / " + DatosRec.get(2));
                    edLineSet.setVisibility(View.INVISIBLE);
                    filterArray[0] = new InputFilter.LengthFilter(8);
                    edAIAG.setFilters(filterArray);
                /*TextDisable(12, edCodigo2D, "2D");
                TextDisable(8, edAIAG, "AIAG");*/
                    break;
                case 2:
                    tvCantidad.setText(" " + PiezasEscaneadas + " / " + DatosRec.get(2));
                    edLineSet.setVisibility(View.VISIBLE);
                /*TextDisable(12, edCodigo2D,"2D");
                TextDisable(8, edAIAG, "AIAG");
                TextDisable(8, edLineSet, "LineSet");*/
                    break;
                case 3:
                    tvCantidad.setText(" " + PiezasEscaneadas + " / " + DatosRec.get(2));
                    edAIAG.setVisibility(View.VISIBLE);
                    edLineSet.setVisibility(View.INVISIBLE);
                    edAIAG.setHint("LineSet");
                    filterArray[0] = new InputFilter.LengthFilter(12);
                    edAIAG.setFilters(filterArray);
                    break;
            }
            if(PiezasEscaneadas == Integer.parseInt(DatosRec.get(2))){
                /*alerta();*/
                ModeloBD adminBDs = new ModeloBD(this, "Eaton", null, 1);
                SQLiteDatabase BDs = adminBDs.getWritableDatabase();
                Date date = new Date();
                DateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                currentDate = fecha.format(date);
                BDs.execSQL("update Etiqueta set Estado = 1 where CodigoBarras = " + Integer.parseInt(DatosRec.get(3)));
                //crearArchivoTxt();
                InsertarTemp();
                crearArchivo(currentDate);
                limpiarBD(false);
                regresar();
            }
            //Verfica que no exista el archivo para crearlo
            if(archivoExist(archivos, currentDate + ".txt")){
                try {
                    InputStreamReader archivo = new InputStreamReader(
                            openFileInput(currentDate + ".txt"));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String todo = "";
                    while (linea != null) {
                        todo = todo + linea + "\n";
                        linea = br.readLine();
                    }
                    br.close();
                    archivo.close();
                } catch (IOException e) {
                }
            }
        }catch(Exception ex){ Toast.makeText(this,"Error ON create" +ex.getMessage(), Toast.LENGTH_LONG).show();}


    }
    //metodo para detectar si el Codigo llega a su limite de caracteres
    /*private void TextDisable(int carc, EditText text, String metodo){
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Verificación para saber si el contador es igual a los caracteres del código
                contador += count;
                if(contador == carc){
                    next(metodo);
                    contador = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }*/
    @Override
    public void onBackPressed(){

    }
    //Método que detecta si se escanean 2 o 3 codigos o International
    private int IsTwo(ArrayList<String> datos){
        int estado = 0;
        int etiqueta = 0;
        ArrayList<String> Destinos = new ArrayList<>();
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        Cursor fila = BD.rawQuery("Select NoEtiquetas from destinos where nombre = '" + datos.get(0) + "'", null);
        if(fila.moveToFirst()){
            etiqueta = fila.getInt(0);
            switch (etiqueta){
                //2 etiquetas = 1
                case 2: estado = 1;
                break;
                //3 = 2
                case 3: estado = 2;
                break;
                //4 = 2 diferentes
                case 4: estado = 3;
                break;
                default: estado = 0;
            }
        }
        BD.close();
        return estado;
    }
    //Método para simular un DataTable (Lista de objeto)
    private void llenarDataTable(){
        ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
        SQLiteDatabase BD = adminBD.getWritableDatabase();
        escaneos = new ArrayList<>();
        //Consuta a la base de datos
        Cursor fila = BD.rawQuery("SELECT  IDRegistro, etiqueta2D, etiquetaAIAG, etiquetaLineSet, fecha  from Registros WHERE Folio ='"+Folio+"';", null);
        //Verifica que tenga datos
        if(fila.moveToFirst()){
            do{
                //Llenado del Datatable (Lista de objeto)
                escaneos.add(new Escaneos(fila.getString(1),fila.getString(2),fila.getString(3),fila.getString(4)));
            }
            //Se detiene cuando ya no encuentra datos
            while (fila.moveToNext());
        }
        BD.close();
    }

    //Método que busca si el archivo que se creara existe
    private boolean archivoExist(String[] archivos, String name){
        //Busca en el arreglo de los archivos internos si existe
        for (int f = 0; f < archivos.length; f++)
            //Si lo encuentra devuelve verdadero
            if (name.equals(archivos[f]))
                return true;
        return false;
    }
    private void crearArchivo(String name){
        llenarDataTable();
        Document docPDF = new Document();
        try{
            File file = new File(Environment.getExternalStorageDirectory() + "/Documents/Transmisiones" , name + ".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());
            PdfWriter writer = PdfWriter.getInstance(docPDF, ficheroPDF);
            docPDF.open();
            Bitmap logo = BitmapFactory.decodeResource(this.getResources(), R.drawable.eatonlogo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            logo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_LEFT);
            image.scaleAbsolute(125,25);
            docPDF.add(image);
            docPDF.add(new Paragraph("Work Order: " + DatosRec.get(1)));
            docPDF.add(new Paragraph("Gafete: " + DatosRec.get(3)));
            docPDF.add(new Paragraph("Cliente: " + DatosRec.get(4)));
            docPDF.add(new Paragraph("Destino: " + DatosRec.get(0)));
            docPDF.add(new Paragraph("Cantidad: " + DatosRec.get(2)));
            docPDF.add(new Paragraph(" "));
            if(IsTwo(DatosRec) == 1){
                for(int i = 0; i < Integer.parseInt(DatosRec.get(2)); i++){
                    docPDF.add(new Paragraph("" + escaneos.get(i).getEtiqueta2D() + " | " + escaneos.get(i).getetiquetaAIAG() + " | " + escaneos.get(i).getfecha()));
                }
            }
            else if(IsTwo(DatosRec) == 3){
                for(int i = 0; i < Integer.parseInt(DatosRec.get(2)); i++){
                    docPDF.add(new Paragraph("" + escaneos.get(i).getEtiqueta2D() + " | " + escaneos.get(i).getetiquetaLineSet() + " | " + escaneos.get(i).getfecha()));
                }
            }
            else{
                for(int i = 0; i < Integer.parseInt(DatosRec.get(2)); i++){
                    docPDF.add(new Paragraph("" + escaneos.get(i).getEtiqueta2D() + " | " + escaneos.get(i).getetiquetaAIAG() + " | " + escaneos.get(i).getetiquetaLineSet() + " | " + escaneos.get(i).getfecha()));
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            docPDF.close();
            Toast.makeText(this, "Archivo generado /Documents/Transmisiones", Toast.LENGTH_SHORT).show();
            escaneos.clear();
            Intent intent = new Intent(this, Packing.class);
            limpiarBD(false);
            startActivity(intent);
            finish();
        }
    }
    //Metodo que crea la carpeta donde se guardaran los pdf
    private boolean crearFolder(String nombreCarpeta){
        //Se crea la carpeta
        File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), nombreCarpeta);
        //Si no existe la crea y devuelve falso
        if(!carpeta.mkdirs()){
            return false;
        }
        else{
            return true;
        }
    }
    //Metodo que cambia a la siguiente etiqueta
    public void siguiente(){
        //aumenta las piezas escaneadas
        PiezasEscaneadas += 1;
        //actualiza los datos en la activity
        Intent intent = new Intent(this, Lectura.class);
        intent.putExtra("datos", DatosRec);
        intent.putExtra("Piezas", PiezasEscaneadas);
        //Guarda los escaneos en la BD
        Escaneos();
        startActivity(intent);
    }
    //Metodo que guarda los escaneos en la BD
    private void Escaneos(){
        //Se lee el codigo 2D y se le retira el prefijo DM17
        try{
            String Codigo2D = edCodigo2D.getText().toString();
            String _auxCod[] = Codigo2D.split("DM17");
            Codigo2D = _auxCod[1];
            ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
            SQLiteDatabase BD = adminBD.getWritableDatabase();
            ContentValues add = new ContentValues();
            ContentValues add2 = new ContentValues();
            Date date = new Date();
            DateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat fechaoHrs = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = fecha.format(date);
            String currentDateoHrs = fechaoHrs.format(date);
            String _aux = "";
            //Verifcia si son 2 o 3 etiquetas para la inserción a la BD
            if(IsTwo(DatosRec) == 1){
                add.put("CodigoBarras", DatosRec.get(3));
                add.put("etiqueta2D", Codigo2D);
                add.put("etiquetaAIAG", edAIAG.getText().toString());
                add.put("etiquetaLineSet", "");
                add.put("fecha", currentDate);
                add.put("fechaInsercion", currentDateoHrs);
                add.put("Estado", 0);
                add.put("Folio",String.valueOf(Folio));

                BD.insert("Etiqueta", null, add);

            /*add2.put("CodigoBarras", DatosRec.get(3));
            add2.put("etiqueta2D", Codigo2D);
            add2.put("etiquetaAIAG", edAIAG.getText().toString());
            add2.put("etiquetaLineSet", "");
            add2.put("fecha", currentDate);
            add2.put("fechaInsercion", currentDateoHrs);
            BD.insert("Registros", null, add2);*/
            }
            else if(IsTwo(DatosRec) == 3){
                String _auxCodigoAIAG = edAIAG.getText().toString().trim();
                int count = edAIAG.getText().toString().length();
                if(count == 12)
                    _auxCodigoAIAG = _auxCodigoAIAG.substring(4, 12);
                add.put("CodigoBarras", DatosRec.get(3));
                add.put("etiqueta2D", Codigo2D);
                add.put("etiquetaAIAG", "");
                add.put("etiquetaLineSet", _auxCodigoAIAG);
                add.put("fecha", currentDate);
                add.put("fechaInsercion", currentDateoHrs);
                add.put("Estado", 0);
                add.put("Folio",String.valueOf(Folio));
                BD.insert("Etiqueta", null, add);

            /*add2.put("CodigoBarras", DatosRec.get(3));
            add2.put("etiqueta2D", Codigo2D);
            add2.put("etiquetaAIAG", "");
            add2.put("etiquetaLineSet", _auxCodigoAIAG);
            add2.put("fecha", currentDate);
            add2.put("fechaInsercion", currentDateoHrs);
            BD.insert("Registros", null, add2);*/
            }
            else {
                add.put("CodigoBarras", DatosRec.get(3));
                add.put("etiqueta2D", Codigo2D);
                add.put("etiquetaAIAG", edAIAG.getText().toString());
                add.put("etiquetaLineSet", edLineSet.getText().toString());
                add.put("fecha", currentDate);
                add.put("fechaInsercion", currentDateoHrs);
                add.put("Folio",String.valueOf(Folio));

                add.put("Estado", 0);

                BD.insert("Etiqueta", null, add);

            /*add2.put("CodigoBarras", DatosRec.get(3));
            add2.put("etiqueta2D", Codigo2D);
            add2.put("etiquetaAIAG", edAIAG.getText().toString());
            add2.put("etiquetaLineSet", edLineSet.getText().toString());
            add2.put("fecha", currentDate);
            add2.put("fechaInsercion", currentDateoHrs);
            BD.insert("Registros", null, add2);*/
            }
            add.clear();
            //add2.clear();
            BD.close();
        }catch(Exception ex){Toast.makeText(this, "Error Metodo Escaneos"+ex.getMessage(), Toast.LENGTH_SHORT).show();}
    }
    //Metodo que limpia la tabla registros
    private void limpiarBD(boolean estado){
        try{
            ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
            SQLiteDatabase BD = adminBD.getWritableDatabase();
            BD.execSQL("delete from Registros where Estado = 1 and CodigoBarras = '" + DatosRec.get(3) + "'" );
            if(estado)
                BD.execSQL("delete from Etiqueta where CodigoBarras = '" + DatosRec.get(3) + "'" );
            BD.close();
        }
        catch(Exception ex){
            Toast.makeText(this, "Error limpiar BD: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Contacta al desarrollador", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para regresar de Activity
    private void regresar(){
        Intent intent = new Intent(this, Packing.class);
        startActivity(intent);
        finish();
    }
    //Método para validar si se presiona enter en cada uno de los EditText
    public void validar(EditText edText, String Codigo){
        edText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    next(Codigo);
                }
                return false;
            }
        });
    }
    //Metodo que valida si ya se escaneo el codigo 2D
    private boolean validarCodigo2D(){
        boolean estado = false;
        try{
            String Codigo2D = edCodigo2D.getText().toString();
            String _auxCod[] = Codigo2D.split("DM17");
            Codigo2D = _auxCod[1];
            String C2D = "";
            ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
            SQLiteDatabase BD = adminBD.getWritableDatabase();
            Cursor fila = BD.rawQuery("Select etiqueta2D from Etiqueta where etiqueta2D = '" + Codigo2D + "'", null);
            if(fila.moveToNext()){
                C2D = fila.getString(0);
            }
            BD.close();
            //Si existe devuelve Verdadero
            if(!C2D.equals("")){
                estado = true;
            }
            else{
                estado = false;
            }
            return estado;
        }
        catch (Exception ex){
            return estado;
        }
    }
    //Metodo que valida que el codigo 2D Contiene su prefijo correspondiente
    private boolean prefijo(String codigo){
        try{
            String[] _auxCodigo = codigo.split("DM17");
            boolean bandera = false;
            //Si existe devuelve verdadero
            if(_auxCodigo.length > 1){
                bandera = true;
            }
            else if(_auxCodigo == null){
                bandera = false;
            }
            return bandera;
        }
        catch(Exception ex){
            Toast.makeText(this, "Error metodo Prefijo"+ex.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    //Método que valida el EditText y cambia al siguiente
    private void next(String metodo){
        //Verificiación para el tipo de codigo
        try{
            switch (metodo){
                case "2D":
                    //Verifica que no este vacio
                    if(!edCodigo2D.getText().toString().equals("")){
                        //Verificca que contenga el prefijo
                        if(prefijo(edCodigo2D.getText().toString())){
                            //Verifica que no exista y si no existe habilita el Código AIAG
                            if(validarCodigo2D()){
                                Toast.makeText(Lectura.this, "Etiqueta previamente Escaneada", Toast.LENGTH_SHORT).show();
                                limpiar();
                            }
                            else{
                                //edCodigo2D.setEnabled(false);
                                edAIAG.setEnabled(true);
                                //edAIAG.requestFocus();
                                edAIAG.setInputType(InputType.TYPE_NULL);
                            }
                        }
                        else{
                            Toast.makeText(Lectura.this, "No se encontró el prefijo", Toast.LENGTH_SHORT).show();
                            limpiar();
                        }
                    }
                    else{
                        edAIAG.setEnabled(false);
                    }
                    break;
                case "AIAG":
                    String Codigo = edCodigo2D.getText().toString().replace("DM17","");
                    String _auxCodigo  = Codigo;
                    String _auxCodigoAIAG = edAIAG.getText().toString();
                    int count = edAIAG.getText().toString().length();
                    //Verifica que los codigos hagan match
                    if(IsTwo(DatosRec) == 3)
                        if(count == 12)
                            _auxCodigoAIAG = _auxCodigoAIAG.substring(4, 12);
                    else
                        if(count == 12)
                            _auxCodigoAIAG = _auxCodigoAIAG.substring(4, 12);
                    if(_auxCodigoAIAG.equals(_auxCodigo)){
                        //Si son 3 etiquetas habilita la siguiente y si no acaba con ese escaneo
                        if(IsTwo(DatosRec) == 2){
                            //edAIAG.setEnabled(false);
                            edLineSet.setEnabled(true);
                            //edLineSet.requestFocus();
                            edLineSet.setInputType(InputType.TYPE_NULL);
                            estado = true;
                        }
                        else if(IsTwo(DatosRec) == 1 || IsTwo(DatosRec) == 3){
                            siguiente();
                        }
                    }
                    else{
                        Toast.makeText(this, "No coinciden los códigos", Toast.LENGTH_SHORT).show();
                        edLineSet.setEnabled(false);
                        estado = false;
                        limpiar();
                    }
                    break;
                case "LineSet":
                    String codigo = edCodigo2D.getText().toString().trim();
                    codigo = codigo.replace("DM17", "");
                    String codigoLineSet = edLineSet.getText().toString().trim();
                    boolean isExistC = codigoLineSet.contains(codigo);
                    //Verifica que los codigos hagan match
                    if(isExistC && estado){
                        siguiente();
                    }
                    else{
                        Toast.makeText(this, "No coinciden los códigos", Toast.LENGTH_SHORT).show();
                        limpiar();
                    }
                    break;
            }
        }
        catch(Exception ex){
            Toast.makeText(this, "Error Metodo Next > " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //Método para cerrar la activity
    public void cerrar(View v){

        if(v.getId() == R.id.btCancelar)
        {
        Intent intent = new Intent(this, Packing.class);
        startActivity(intent);
        finish();

        }

//
//        //limpiarBD(true);
    }
    //Método que limpia la activity
    public void limpiar(){
        edCodigo2D.setText("");
        edAIAG.setText("");
        edLineSet.setText("");
        edCodigo2D.setInputType(InputType.TYPE_NULL);
        edAIAG.setInputType(InputType.TYPE_NULL);
        edAIAG.setEnabled(false);
        edLineSet.setInputType(InputType.TYPE_NULL);
        edLineSet.setEnabled(false);
        edCodigo2D.requestFocus();
    }
    public void limpiarClick(View v){
        limpiar();
    }

    private void InsertarTemp(){
        try{
            Registros();
            ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
            SQLiteDatabase BD = adminBD.getWritableDatabase();
            ContentValues add = new ContentValues();
            for(int i = 0; i <= Registros.size(); i++ ){
                if(IsTwo(DatosRec) == 1){
                    add.put("CodigoBarras", DatosRec.get(3));
                    add.put("etiqueta2D", Registros.get(i).getEtiqueta2D());
                    add.put("etiquetaAIAG", Registros.get(i).getetiquetaAIAG());
                    add.put("etiquetaLineSet", "");
                    add.put("fecha", Registros.get(i).getfecha());
                    add.put("fechaInsercion", Registros.get(i).getFechaInsercion());
                    add.put("Estado", 0);
                    add.put("Folio",String.valueOf(Folio));
                    BD.insert("Registros", null, add);
                }
                else if(IsTwo(DatosRec) == 3){
                    String _auxCodigoAIAG = edAIAG.getText().toString().trim();
                    int count = edAIAG.getText().toString().length();
                    if(count == 12)
                        _auxCodigoAIAG = _auxCodigoAIAG.substring(4, 12);
                    add.put("CodigoBarras", DatosRec.get(3));
                    add.put("etiqueta2D", Registros.get(i).getEtiqueta2D());
                    add.put("etiquetaAIAG", "");
                    add.put("etiquetaLineSet", Registros.get(i).getetiquetaLineSet());
                    add.put("fecha", Registros.get(i).getfecha());
                    add.put("fechaInsercion", Registros.get(i).getFechaInsercion());
                    add.put("Estado", 0);
                    add.put("Folio",String.valueOf(Folio));

                    BD.insert("Registros", null, add);
                }
                else {
                    add.put("CodigoBarras", DatosRec.get(3));
                    add.put("etiqueta2D", Registros.get(i).getEtiqueta2D());
                    add.put("etiquetaAIAG", Registros.get(i).getetiquetaAIAG());
                    add.put("etiquetaLineSet", Registros.get(i).getetiquetaLineSet());
                    add.put("fecha", Registros.get(i).getfecha());
                    add.put("fechaInsercion", Registros.get(i).getfecha());
                    add.put("Estado", 0);
                    add.put("Folio",String.valueOf(Folio));

                    BD.insert("Registros", null, add);

                }
            }

            add.clear();

            BD.close();
        }catch (Exception ex){
            Toast.makeText(this, "Error insert TEMP" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void Registros(){
        try{
            Registros = new ArrayList<>();
            ModeloBD adminBD = new ModeloBD(this, "Eaton", null, 1);
            SQLiteDatabase BD = adminBD.getWritableDatabase();
            Cursor fila = BD.rawQuery("SELECT DISTINCT etiqueta2D, etiquetaAIAG, etiquetaLineSet, fecha, fechaInsercion from Etiqueta where Estado = 1 and CodigoBarras = '" + DatosRec.get(3) + "' and Folio ='"+Folio+"'" , null);
            //BD.execSQL("INSERT INTO Registros SELECT DISTINCT * Etiqueta where estado = 1 and CodigoBarras = '" + DatosRec.get(3) + "'", null);
            if(fila.moveToFirst()){
                do{
                    Registros.add(new Registros(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), String.valueOf(Folio)));
                }while (fila.moveToNext());
            }
            BD.close();
        }catch (Exception ex){
            Toast.makeText(this, "Error Metodo Registros"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}