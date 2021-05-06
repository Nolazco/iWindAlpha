package com.carlosnolazco.ayudamediosito;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listaCanciones;
    ArrayAdapter<String> ArrayAdapterMusica;
    String canciones[];
    ArrayList<File> musica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaCanciones = findViewById(R.id.lista);

        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse)
            {
                musica = BuscarMusica(Environment.getExternalStorageDirectory());
                canciones = new String[musica.size()];
                for(int i = 0; i < musica.size(); i++)
                {
                    canciones[i] = musica.get(i).getName();
                }

                ArrayAdapterMusica = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, canciones);

                listaCanciones.setAdapter(ArrayAdapterMusica);

                /*listaCanciones.setOnClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<String> parent, View view, int position, long id) {
                        
                    }
                });*/
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse)
            {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken)
            {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private ArrayList<File> BuscarMusica(File file)
    {
        ArrayList<File> MusicaEncontrada = new ArrayList<>();
        File [] files = file.listFiles();

        for(File currentFiles: files)
        {
            if(currentFiles.isDirectory() && !currentFiles.isHidden())
            {
                MusicaEncontrada.addAll(BuscarMusica(currentFiles));
            }else
            {
                if(currentFiles.getName().endsWith(".mp3"))
                {
                    MusicaEncontrada.add(currentFiles);
                }
            }
        }
        return MusicaEncontrada;
    }
}