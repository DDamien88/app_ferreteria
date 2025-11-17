package com.example.appferreteria.ui.notificaciones;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appferreteria.R;
import com.example.appferreteria.modelo.Notificacion;

import java.util.ArrayList;
import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private List<Notificacion> lista = new ArrayList<>();

    public NotificacionAdapter() {
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notificacion_item, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificacion notificacion = lista.get(position);

        holder.titulo.setText(notificacion.getTitulo());
        holder.mensaje.setText(notificacion.getMensaje());
        holder.fecha.setText(formatearFecha(notificacion.getFecha()));
        holder.usuario.setText(
                "Usuario: " + notificacion.getUsuarioNombre() + " " + notificacion.getUsuarioApellido()
        );

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setNotificaciones(List<Notificacion> nuevasNotificaciones) {
        this.lista = nuevasNotificaciones;
        notifyDataSetChanged();
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, mensaje, fecha, usuario;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.tvTituloNotif);
            mensaje = itemView.findViewById(R.id.tvMensajeNotif);
            fecha = itemView.findViewById(R.id.tvFechaNotif);
            usuario = itemView.findViewById(R.id.tvUsuarioNotif);
        }
    }

    private String formatearFecha(String fechaOriginal) {
        try {
            // Detectar formato que viene del backend
            String[] formatosEntrada = {
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
            };

            for (String f : formatosEntrada) {
                try {
                    java.text.SimpleDateFormat entrada = new java.text.SimpleDateFormat(f);
                    java.util.Date fecha = entrada.parse(fechaOriginal);

                    // Formato de salida: día/mes/año hora:minutos
                    java.text.SimpleDateFormat salida = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    return salida.format(fecha);
                } catch (Exception ignore) {
                }
            }

            return fechaOriginal; // Si falla todo, la dejo como viene

        } catch (Exception e) {
            return fechaOriginal;
        }
    }


}
