package com.carlosnolazco.ayudamediosito;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterMusica extends RecyclerView.Adapter<AdapterMusica.ViewHolderMusica> {
    public ArrayList<AudioModel> audioModels;

    public AdapterMusica(ArrayList<AudioModel> arrayDos)
    {
        audioModels = arrayDos;
    }

    @NonNull
    @Override
    public ViewHolderMusica onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elementos, parent, false);

        return new ViewHolderMusica(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMusica holder, int position) {
        holder.asignar(audioModels.get(position));
        holder.pos = position;
    }

    @Override
    public int getItemCount() {
        return audioModels.size();
    }

    public static class ViewHolderMusica extends RecyclerView.ViewHolder {
        public int pos;
        LinearLayout container;
        TextView nombreCancion;
        TextView rutaCancion;

        public ViewHolderMusica(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            nombreCancion = itemView.findViewById(R.id.nombreCancion);
            rutaCancion = itemView.findViewById(R.id.rutaCancion);
        }

        public void asignar(AudioModel audioModel) {
            nombreCancion.setText(audioModel.name);
            rutaCancion.setText(audioModel.path);
            container.setOnClickListener(this::onClick);
        }

        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), ReproductorActivity.class);
            intent.putExtra("song_pos", pos);
            itemView.getContext().startActivity(intent);
        }
    }
}
