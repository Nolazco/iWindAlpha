package com.carlosnolazco.ayudamediosito;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class reproductor extends AppCompatActivity {

    ImageView atras, repetir, play, detener, adelante;
    int posicion;
    String nombreC;
    SeekBar progreso;
    static MediaPlayer reproductor;
    TextView nombre;
    ArrayList listaDeMusica;
    int loop = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        atras = findViewById(R.id.atras);
        repetir = findViewById(R.id.repetir);
        play = findViewById(R.id.Play_pause);
        detener = findViewById(R.id.detener);
        adelante = findViewById(R.id.adelante);
        progreso = findViewById(R.id.progreso);
        nombre = findViewById(R.id.nombre);

        if(reproductor !=null)
        {
            reproductor.stop();
        }

        Bundle datosExtras = getIntent().getExtras();
        listaDeMusica = (ArrayList)datosExtras.getParcelableArrayList("TodaLaMusica");
        posicion = datosExtras.getInt("posicion", 0);

        iniciarReproductor(posicion);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducir();
            }
        });

        detener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reproductor != null){
                    reproductor.stop();
                    play.setImageResource(R.drawable.reproducir);
                    Toast.makeText(reproductor.this, "Musica detenida", Toast.LENGTH_LONG).show();
                    Intent listaM = new Intent(reproductor.this, MainActivity.class);
                    startActivity(listaM);
                }
            }
        });

        adelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posicion < listaDeMusica.size() -1)
                {
                    posicion++;
                }else
                {
                    posicion = 0;
                }
                iniciarReproductor(posicion);
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posicion <= 0)
                {
                    posicion = listaDeMusica.size();
                }else
                {
                    posicion++;
                }

                iniciarReproductor(posicion);
            }
        });

        repetir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loop == 1){
                    repetir.setImageResource(R.drawable.no_repetir);
                    Toast.makeText(reproductor.this, "No repetir", Toast.LENGTH_SHORT).show();
                    reproductor.setLooping(false);
                    loop = 2;
                }else{
                    repetir.setImageResource(R.drawable.repetir);
                    Toast.makeText(reproductor.this, "Repetir", Toast.LENGTH_SHORT).show();
                    reproductor.setLooping(true);
                    loop = 1;
                }
            }
        });
    }

    private void iniciarReproductor(int posicion)
    {
        if(reproductor != null && reproductor.isPlaying())
        {
            reproductor.reset();
        }

        String titulo=listaDeMusica.get(posicion).getName().toString;
        nombre.setText(titulo);

        Uri uri = Uri.parse(listaDeMusica.get(posicion).toString());

        reproductor = MediaPlayer.create(this, uri);

        reproductor.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                progreso.setMax(reproductor.getDuration());
                play.setImageResource(R.drawable.pausa);
                reproductor.start();
            }
        });

        reproductor.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageResource(R.drawable.reproducir);
            }
        });

        progreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser)
                {
                    progreso.setProgress(progress);
                    reproductor.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(reproductor !=null)
                {
                    try
                    {
                        if(reproductor.isPlaying())
                        {
                            Message message = new Message();
                            message.what = reproductor.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(10000);

                        }
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            progreso.setProgress(msg.what);
        }
    };

    private void reproducir()
    {
        if(reproductor != null && reproductor.isPlaying())
        {
            reproductor.pause();
            play.setImageResource(R.drawable.reproducir);
        }else
        {
            reproductor.start();
            play.setImageResource(R.drawable.pausa);
        }
    }
}