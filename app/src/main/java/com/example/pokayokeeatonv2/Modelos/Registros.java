package com.example.pokayokeeatonv2.Modelos;

public class Registros {
    private String etiqueta2D;
    private String etiquetaAIAG;
    private String etiquetaLineSet;
    private String fecha;
    private String fechaInsercion;

    private  String folio;

    public String getFolio() {
        return this.folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getEtiqueta2D(){
        return this.etiqueta2D;
    }
    public void setEtiqueta2D(String etiqueta2D){
        this.etiqueta2D = etiqueta2D;
    }
    public String getetiquetaAIAG(){
        return this.etiquetaAIAG;
    }
    public void setetiquetaAIAG(String etiquetaAIAG){
        this.etiquetaAIAG = etiquetaAIAG;
    }
    public String getetiquetaLineSet(){
        return this.etiquetaLineSet;
    }
    public void setetiquetaLineSet(String etiquetaLineSet){ this.etiquetaLineSet = etiquetaLineSet; }
    public String getfecha(){
        return this.fecha;
    }
    public void setfecha(String fecha){
        this.fecha = fecha;
    }
    public String getFechaInsercion(){return this.fechaInsercion;}
    public void setFechaInsercion(String fechaInsercion){this.fechaInsercion = fechaInsercion;}
    public Registros(String etiqueta2D, String etiquetaAIAG, String etiquetaLineSet, String fecha, String fechaInsercion, String folion){
        this.etiqueta2D = etiqueta2D;
        this.etiquetaAIAG = etiquetaAIAG;
        this.etiquetaLineSet = etiquetaLineSet;
        this.fecha = fecha;
        this.fechaInsercion = fechaInsercion;
        this.folio =folion;

    }
}
