package com.carlosnolazco.ayudamediosito;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MusicServ {
	public static ArrayList<AudioModel> canciones;
	public static AudioModel song;
	public static int currentSong;
	public static MediaPlayer player;
	public static Runnable onSongChanged;

	public static void init() {
	    canciones = new ArrayList<>();
	    player = new MediaPlayer();
		player.setOnCompletionListener(MusicServ::completedSong);
		onSongChanged = null;
		song = null;
		currentSong = -1;
	}

	public static void getSongs (Context context) {
        // Uri de el auio
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // Info que recoger de las canciones
        String[] projection = {
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
        };
        // Condicional que recoge solo las canciones
        String where = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        // Hacer la consulta a las canciones
        Cursor c = context.getContentResolver().query(
                uri, projection, where, null,
                MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        // Conseguir info
        if (c != null) {
        	canciones.clear();
            while (c.moveToNext())
            {
                AudioModel audioModel = new AudioModel();
                audioModel.path = c.getString(0);
                audioModel.name = c.getString(1);
				audioModel.album = c.getInt(2);
				audioModel.artista = c.getInt(3);

                canciones.add(audioModel);
            }
            c.close();
        } else {
            Toast.makeText(context,
                "Error al conseguir la musica del dispositovo",
                Toast.LENGTH_LONG).show();
        }
    }

    public static void prevSong () {
		currentSong--;
		if (currentSong < 0)
			currentSong = canciones.size() - 1;
		changeSong();
	}

    public static void nextSong () {
		currentSong++;
		if (currentSong >= canciones.size())
			currentSong = 0;
		changeSong();
	}

	public static void changeSong () {
		try {
			song = canciones.get(currentSong);
			player.reset();
			player.setDataSource(song.path);
			player.prepare();
			player.start();
			if (onSongChanged != null)
				onSongChanged.run();
		} catch (IOException e) { e.printStackTrace(); }
	}

	public static void completedSong (MediaPlayer mp) {
			if (!player.isLooping())
				nextSong();
	}
}
