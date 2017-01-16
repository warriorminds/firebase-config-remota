package com.warriorminds.firebase;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    private static String TITULO = "titulo_bienvenida";
    private static String MOSTRAR_SUBTITULO = "mostrar_subtitulo";
    private static String COLOR_SUBTITULO = "color_subtitulo";
    private static String TEXTO = "texto_mostrar";

    private FirebaseRemoteConfig configuracionRemota;

    private TextView titulo;
    private TextView subtitulo;
    private TextView texto;
    private TextView colorSubtitulo;
    private Button botonConfigRemota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarConfiguracionRemota();
        inicializarVistas();
        mostrarValores();
    }

    private void inicializarConfiguracionRemota() {
        configuracionRemota = FirebaseRemoteConfig.getInstance();
        configuracionRemota.setDefaults(R.xml.valores_config_remota);

        FirebaseRemoteConfigSettings opcionesConfiguracionRemota = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        configuracionRemota.setConfigSettings(opcionesConfiguracionRemota);
    }

    private void inicializarVistas() {
        titulo = (TextView) findViewById(R.id.titulo);
        subtitulo = (TextView) findViewById(R.id.subtitulo);
        texto = (TextView) findViewById(R.id.texto);
        colorSubtitulo = (TextView) findViewById(R.id.color_subtitulo);
        botonConfigRemota = (Button) findViewById(R.id.boton_obtener_config);
        botonConfigRemota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerConfiguracionRemota();
            }
        });
    }

    private void mostrarValores() {
        titulo.setText(configuracionRemota.getString(TITULO));

        if (configuracionRemota.getBoolean(MOSTRAR_SUBTITULO)) {
            subtitulo.setVisibility(View.VISIBLE);
        } else {
            subtitulo.setVisibility(View.GONE);
        }

        subtitulo.setTextColor(Color.parseColor(configuracionRemota.getString(COLOR_SUBTITULO)));
        colorSubtitulo.setText("El color del subtitulo es " + configuracionRemota.getString(COLOR_SUBTITULO));
        texto.setText(configuracionRemota.getString(TEXTO));
    }

    private void obtenerConfiguracionRemota() {
        long tiempoExpiracionDatos = 3600; // en segundos.

        if (configuracionRemota.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            tiempoExpiracionDatos = 0;
        }

        configuracionRemota.fetch(tiempoExpiracionDatos).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> tarea) {
                if (tarea.isSuccessful()) {
                    configuracionRemota.activateFetched();
                }
                mostrarValores();
            }
        });
    }
}
