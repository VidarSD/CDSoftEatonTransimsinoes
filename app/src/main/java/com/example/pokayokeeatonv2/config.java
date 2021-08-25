package com.example.pokayokeeatonv2;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class config {
    public interface DatosCuadroDialogo{
        void resultadoCuadroDialogo(String user, String pass);
    }
    private DatosCuadroDialogo interfaz;
    public void CuadroDialogo(Context context, DatosCuadroDialogo actividad){
        interfaz = actividad;
        final Dialog dialogo = new Dialog(context);
        dialogo.setTitle("Configuración");
        dialogo.setContentView(R.layout.activity_config);
        final EditText etUser = dialogo.findViewById(R.id.etUser);
        final EditText etPass = dialogo.findViewById(R.id.etPass);
        Button btGuardar = dialogo.findViewById(R.id.bGuardar);
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUser.getText().toString();
                String pass = etPass.getText().toString();
                interfaz.resultadoCuadroDialogo(user, pass);
                dialogo.dismiss();
            }
        });
        dialogo.show();
    }
}
