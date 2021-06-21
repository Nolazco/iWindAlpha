package com.carlosnolazco.ayudamediosito;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST = 1;
    RecyclerView listaF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listaF = findViewById(R.id.listaF);

        if(!externalStorageEnabled()) {
            requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST);
        } else
            init();
    }

    public void init() {
        MusicServ.init();
        MusicServ.getSongs(this);

        AdapterMusica adapter = new AdapterMusica(MusicServ.canciones);
        listaF.setLayoutManager(new LinearLayoutManager(this));
        listaF.setHasFixedSize(true);
        listaF.setAdapter(adapter);
    }

    /*
     * COSAS RELACIONADAS CON EL PERMISO DE
     * ACCEDER A LOS ARCHIVOS
     */
    public boolean externalStorageEnabled() {
        int readPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE);

        return readPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode,
                               @NonNull String[] permissions,
                               @NonNull int[] grantResults) {
        /* Si el permiso es nuestro permiso de almacenamiento entonces
         * verificar que los permisos esten realmente y entonces
         * hacer la consulta de las canciones
         * */
        if (requestCode == PERMISSION_REQUEST) {
            if(externalStorageEnabled()) {
                Toast.makeText(this, "Permiso concedido",
                    Toast.LENGTH_LONG).show();
                MusicServ.init();
            } else {
                Toast.makeText(this, "Permiso no concedido",
                    Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST);
            }
        }
    }
}
