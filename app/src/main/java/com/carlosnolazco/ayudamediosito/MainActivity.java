package com.carlosnolazco.ayudamediosito;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST = 1;
    ArrayList<AudioModel> musica = new ArrayList<>();
    ListView mostrarMusica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mostrarMusica = findViewById(R.id.listaM);

        if(externalStorageEnabled())
           getAllAudioFromDevice();
        else
            requestPermissions(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST);
    }

    public void getAllAudioFromDevice() {
        // Uri de el audio
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // Info que recoger de las canciones
        String[] projection = {
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
        };
        // Condicional que recoge solo las canciones
        String where = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        // Hacer la consulta a las canciones
        Cursor c = getContentResolver().query(uri, projection, where, null,
            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        // Conseguir info
        if (c != null) {
            while (c.moveToNext()) {
                AudioModel audioModel = new AudioModel();
                audioModel.path = c.getString(0);
                audioModel.name = c.getString(1);

                musica.add(audioModel);
            }
            c.close();
        } else {
            Toast.makeText(this,
                "Error al conseguir la musica del dispositovo",
                Toast.LENGTH_LONG).show();
        }
    }

    /*
     * COSAS RELACIONADAS CON EL PERMISO DE
     * ACCEDER A LOS ARCHIVOS
     */

    public boolean externalStorageEnabled() {
        int readPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE);

        return readPermission != PackageManager.PERMISSION_GRANTED;
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
                getAllAudioFromDevice();
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
