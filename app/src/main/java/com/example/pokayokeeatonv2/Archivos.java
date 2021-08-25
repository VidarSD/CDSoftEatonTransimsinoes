package com.example.pokayokeeatonv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Archivos extends AppCompatActivity {
    ListView lsArchivos;
    String name;
    String archivos[];
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    int posicion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivos);
        lsArchivos = findViewById(R.id.lsArchivos);
        archivos = fileList();
        listarArchivos();
        lsArchivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name = lsArchivos.getItemAtPosition(position).toString();
                posicion = position;
                alerta();
            }
        });
    }
    private void listarArchivos(){
        list = new ArrayList<>();
        File f = new File("/data/data/com.example.pokayokeeatonv2/files");
        File file[] = f.listFiles();
        for(int i = 0; i < file.length; i++){
            String extension = file[i].getName().substring(file[i].getName().lastIndexOf("."));
            if(extension.equals(".txt")){
                list.add(file[i].getName());
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lsArchivos.setAdapter(adapter);
    }
    private boolean fileExist(String[] archivos,String name){
        for (int f = 0; f < archivos.length; f++)
            if (name.equals(archivos[f]))
                return true;
        return false;
    }
    private void alerta(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Embarque");
        dialog.setMessage("¿Generar Archivo?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Generar Archivo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                crearArchivoPDF(name);
                list.remove(posicion);
                adapter.notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
    private void crearArchivoPDF(String archivo){
        ArrayList<String> DatosTXT = new ArrayList<>();
        Document docPDF = new Document();
        try{
            if(fileExist(archivos, archivo)){
                InputStreamReader txt = new InputStreamReader(openFileInput(archivo));
                BufferedReader br = new BufferedReader(txt);
                String linea = br.readLine() + "\n";
                String[] lineas;
                int contL = 0;
                while(linea != null){
                    lineas = linea.split("\n");
                    for(int i = 0; i < lineas.length; i++){
                        DatosTXT.add(lineas[i]);
                    }
                    linea = br.readLine();
                }
                File _file = new File(archivo);
                _file.delete();
            }
            String[] nombre = archivo.split(".txt");
            File file = new File(Environment.getExternalStorageDirectory() + "/Documents/Transmisiones" , nombre[0] + ".pdf");
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
            docPDF.add(new Paragraph("\n"));
            for(int i = 0; i < DatosTXT.size(); i++){
                docPDF.add(new Paragraph(DatosTXT.get(i)));
            }
        }
        catch (Exception ex){
            Toast.makeText(this, "No se genero el archivo", Toast.LENGTH_SHORT).show();
        }
        finally {
            docPDF.close();
            Toast.makeText(this, "Archivo generado /Documents/EatonFiles", Toast.LENGTH_SHORT).show();
        }
    }
}