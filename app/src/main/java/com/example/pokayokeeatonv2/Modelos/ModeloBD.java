package com.example.pokayokeeatonv2.Modelos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ModeloBD extends SQLiteOpenHelper {
    public ModeloBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase BD){
        //0 false 1 true
        BD.execSQL("CREATE TABLE Folio (IDFolio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , Ftype TEXT)");
        BD.execSQL("create table usuarios (ID INTEGER PRIMARY KEY autoincrement NOT NULL, nombre Text, Pass Text)");
        BD.execSQL("create table clientes (IDCliente INTEGER PRIMARY KEY autoincrement NOT NULL, nombre Text)");
        BD.execSQL("create table destinos (IDDestino INTEGER PRIMARY KEY autoincrement NOT NULL, IDCliente INTEGER, nombre Text, NoEtiquetas INTEGER, foreign key(IDDestino) references clientes(IDCliente))");
        BD.execSQL("create table Etiqueta (IDEtiqueta INTEGER PRIMARY KEY autoincrement NOT NULL, CodigoBarras Text, etiqueta2D Text, etiquetaAIAG Text ,etiquetaLineSet Text, fecha Text, fechaInsercion Text, Estado INTEGER ,Folio TEXT)");
        BD.execSQL("create table Registros(IDRegistro INTEGER PRIMARY KEY autoincrement NOT NULL, CodigoBarras Text, etiqueta2D Text, etiquetaAIAG Text ,etiquetaLineSet Text, fecha Text, fechaInsercion Text, Estado INTEGER ,Folio TEXT )");

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il){

    }

}