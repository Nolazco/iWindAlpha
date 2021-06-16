package com.carlosnolazco.ayudamediosito;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Codido enviado cuando el permiso es concedido
    private final static int PERMISSION_REQUEST = 1;
    ArrayList<AudioModel> musica = new ArrayList<>();
    //RecyclerView lista = findViewById(R.id.lista);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mostrarMusica = findViewById(R.id.listaM);

        // Verificar que tenga permiso al almacenamiento
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si no tiene permiso al almacenamiento entonces pedirle al usuario que le de permiso
            ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        } else {
            getAllAudioFromDevice();
        }

        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1,musica);
        mostrarMusica.setAdapter(adaptador);

        mostrarMusica.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String songName=mostrarMusica.getItemAtPosition(position).toString();



                Intent intent=new Intent(MainActivity.this,reproductor.class);
                intent.putExtra("mySongName",songName);
                intent.putExtra("songPos",position);
                intent.putExtra("allSong",musica);
                startActivity(intent);
            }
        });
    }

    public void getAllAudioFromDevice() {
        final List<AudioModel> tempAudioList = new ArrayList<>();
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
            System.out.println("We have: " + c.getCount());
            while (c.moveToNext()) {
                AudioModel audioModel = new AudioModel();
                String path = c.getString(0);
                String name = c.getString(1);

                audioModel.setaName(name);
                audioModel.setaPath(path);
                musica.add(audioModel);

				Log.d("PATH: ", path);
				Log.d("TITLE: ", name);

                tempAudioList.add(audioModel);
            }
            c.close();
        }
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
