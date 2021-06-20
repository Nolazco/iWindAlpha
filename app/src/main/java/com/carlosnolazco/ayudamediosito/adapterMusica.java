package com.carlosnolazco.ayudamediosito;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapterMusica extends RecyclerView.Adapter<adapterMusica.ViewHolderMusica>
        implements View.OnClickListener{

    public ArrayList<AudioModel> audioModels;
    private View.OnClickListener listener;

    public adapterMusica(ArrayList<AudioModel> arrayDos)
    {
        audioModels = arrayDos;
    }

    @NonNull
    @Override
    public ViewHolderMusica onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elementos, parent, false);

        view.setOnClickListener(this);

        return new ViewHolderMusica(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMusica holder, int position) {
        holder.asignar(audioModels.get(position));
    }

    @Override
    public int getItemCount() {
        return audioModels.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public class ViewHolderMusica extends RecyclerView.ViewHolder {

        TextView nombreCancion;
        TextView rutaCancion;

        public ViewHolderMusica(@NonNull View itemView) {
            super(itemView);
            nombreCancion = itemView.findViewById(R.id.nombreCancion);
            rutaCancion = itemView.findViewById(R.id.rutaCancion);
        }

        public void asignar(AudioModel audioModel) {
            nombreCancion.setText(audioModel.name);
            rutaCancion.setText(audioModel.path);
        }
    }
}
