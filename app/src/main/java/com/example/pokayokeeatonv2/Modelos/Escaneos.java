package com.example.pokayokeeatonv2.Modelos;
import java.io.Serializable;

public class Escaneos implements Serializable {
    private String etiqueta2D;
    private String etiquetaAIAG;
    private String etiquetaLineSet;
    private String fecha;

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
    public void setetiquetaLineSet(String etiquetaLineSet){
        this.etiquetaLineSet = etiquetaLineSet;
    }
    public String getfecha(){
        return this.fecha;
    }
    public void setfecha(String fecha){
        this.fecha = fecha;
    }
    public Escaneos(String etiqueta2D, String etiquetaAIAG, String etiquetaLineSet, String fecha){
        this.etiqueta2D = etiqueta2D;
        this.etiquetaAIAG = etiquetaAIAG;
        this.etiquetaLineSet = etiquetaLineSet;
        this.fecha = fecha;
    }
}
