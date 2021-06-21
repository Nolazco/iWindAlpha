package com.carlosnolazco.ayudamediosito;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ReproductorActivity extends AppCompatActivity {
    ImageButton next, prev, pause, stop, repetir;
    SeekBar progreso;
    ImageView caratula;
    TextView songName;
    Timer timerTask;
    boolean stopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        songName = findViewById(R.id.nombre);
        progreso = findViewById(R.id.progreso);
        caratula = findViewById(R.id.caratula);
        next = findViewById(R.id.adelante);
        prev = findViewById(R.id.atras);
        pause = findViewById(R.id.Play_pause);
        stop = findViewById(R.id.detener);
        repetir = findViewById(R.id.repetir);

        MusicServ.onSongChanged = this::bindData;
        next.setOnClickListener(v -> MusicServ.nextSong());
        prev.setOnClickListener(v -> MusicServ.prevSong());
        pause.setOnClickListener(this::playPauseSong);
        stop.setOnClickListener(this::stopSong);
        repetir.setOnClickListener(v ->
            MusicServ.player.setLooping(!MusicServ.player.isLooping()));
        progreso.setOnSeekBarChangeListener(changeListener);

        stopped = false;
        timerTask = new Timer();
        timerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progreso.setProgress(MusicServ.player.getCurrentPosition());
            }
        }, 0, 500);
        int song_pos = getIntent().getIntExtra("song_pos", -1);
        if (song_pos != -1)
            if (MusicServ.currentSong != song_pos) {
                MusicServ.currentSong = song_pos;
                MusicServ.changeSong();
            } else
                bindData();
        else
            finish();
    }

    private void stopSong(View view) {
        stopped = true;
        MusicServ.player.stop();
    }

    public void playPauseSong (View v) {
        try {
            if (!stopped) {
                if (MusicServ.player.isPlaying())
                    MusicServ.player.pause();
                else
                    MusicServ.player.start();
            } else {
                stopped = false;
                MusicServ.player.prepare();
                MusicServ.player.start();
            }
            changeButton();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void bindData () {
        stopped = false;
        songName.setText(MusicServ.song.name);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(MusicServ.song.path);

        Bitmap bitmap;
        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data,
                0, data.length, null);
            caratula.setImageBitmap(bitmap);
        } else
            caratula.setImageResource(R.drawable.logo);

        progreso.setMax(MusicServ.player.getDuration());
        progreso.setProgress(MusicServ.player.getCurrentPosition());
        changeButton();
    }

    public void changeButton () {
        if (MusicServ.player.isPlaying())
            pause.setImageResource(R.drawable.reproducir);
        else
            pause.setImageResource(R.drawable.pausa);
    }

    public SeekBar.OnSeekBarChangeListener changeListener =
        new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (fromUser) {
                        if (stopped) {
                            stopped = false;
                            MusicServ.player.prepare();
                        }
                        MusicServ.player.seekTo(progress);
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

    @Override
    protected void onStop() {
        super.onStop();
        MusicServ.onSongChanged = null;
        timerTask.cancel();
        timerTask.purge();
    }
}