package com.carlosnolazco.ayudamediosito;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Codido enviado cuando el permiso es concedido
    private final static int PERMISSION_REQUEST = 1;
    ListView listaCanciones;
    ArrayAdapter<String> ArrayAdapterMusica;
    String[] canciones;
    ArrayList<File> musica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // listaCanciones = findViewById(R.id.lista);

        // Verificar que tenga permiso al almacenamiento
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si no tiene permiso al almacenamiento entonces pedirle al usuario que le de permiso
            ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        } else {
            getAllAudioFromDevice();
        }
    }

    public List<AudioModel> getAllAudioFromDevice() {
        final List<AudioModel> tempAudioList = new ArrayList<>();
        // Uri de el audio
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // Info que recoger de las canciones
        String[] projection = {
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        };
        // Condicional que recoge solo las canciones
        String where = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        // Hacer la consulta a las canciones
        Cursor c = getContentResolver().query(uri, projection, where, null,
            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        // Conseguir info
        if (c != null) {
            System.out.println("We have: " + c.getCount());
            while (c.moveToNext()) {
                AudioModel audioModel = new AudioModel();
                String path = c.getString(0);
                String album = c.getString(1);
                String name = c.getString(2);
                String artist = c.getString(3);

                audioModel.setaName(name);
                audioModel.setaAlbum(album);
                audioModel.setaArtist(artist);
                audioModel.setaPath(path);

				Log.d("PATH: ", path);
				Log.d("TITLE: ", name);

                tempAudioList.add(audioModel);
            }
            c.close();
        }
        return tempAudioList;
    }

    // Pedir permiso a el almacenamiento
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        /* Si el permiso es nuestro permiso de almacenamiento entonces
         * verificar que los permisos esten realmente y entonces
         * hacer la consulta de las canciones
         * */
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                    getAllAudioFromDevice();
                }
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
