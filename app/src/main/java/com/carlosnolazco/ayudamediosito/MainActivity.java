package com.carlosnolazco.ayudamediosito;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listaCanciones;
    ArrayAdapter<String> ArrayAdapterMusica;
    String[] canciones;
    ArrayList<File> musica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaCanciones = findViewById(R.id.lista);
        getAllAudioFromDevice();
    }

    public List<AudioModel> getAllAudioFromDevice() {
        final List<AudioModel> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = this.getContentResolver();

        String[] projection = {
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        };
        String where = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor c = resolver.query(uri, projection, null, null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (c != null) {
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
}
