package com.example.appferreteria.ui.stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appferreteria.R;
import com.example.appferreteria.modelo.MovimientoInventario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.ViewHolderMovimiento> {

    private List<MovimientoInventario> lista;
    private Context context;
    private LayoutInflater li;

    public MovimientoAdapter(List<MovimientoInventario> lista, Context context, LayoutInflater li) {
        this.lista = lista;
        this.context = context;
        this.li = li;
    }

    @NonNull
    @Override
    public ViewHolderMovimiento onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = li.inflate(R.layout.item_movimiento, parent, false);
        return new ViewHolderMovimiento(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMovimiento holder, int position) {
        MovimientoInventario m = lista.get(position);

        holder.tvProducto.setText(m.getProducto().getNombre() + m.getProducto().getDescripcion());
        holder.tvTipo.setText(m.getTipo() != null ? m.getTipo() : "N/A");
        holder.tvCantidad.setText("Cantidad: " + m.getCantidad());
        holder.tvUsuario.setText("Usuario: " + m.getUsuario().getNombre() + m.getUsuario().getApellido());
        holder.tvFecha.setText(formatearFecha(m.getFecha()));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }


    private String formatearFecha(String fechaIso8601) {
        if (fechaIso8601 == null)
            return "Fecha no disponible";

        String isoPattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat isoFormat = new SimpleDateFormat(isoPattern, Locale.getDefault());

        try {
            Date fecha = isoFormat.parse(fechaIso8601);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(fecha);
        } catch (ParseException e) {
            return fechaIso8601; // muestra la fecha cruda si falla
        }
    }

    public static class ViewHolderMovimiento extends RecyclerView.ViewHolder {
        TextView tvTipo, tvCantidad, tvUsuario, tvFecha, tvProducto;

        public ViewHolderMovimiento(@NonNull View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvProducto = itemView.findViewById(R.id.tvProducto);
        }
    }
}
