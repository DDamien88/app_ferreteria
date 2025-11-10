package com.example.appferreteria.ui.inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appferreteria.R;
import com.example.appferreteria.modelo.Producto;

import java.util.List;

public class StockBajoAdapter extends RecyclerView.Adapter<StockBajoAdapter.ViewHolder> {

    private List<Producto> lista;

    public StockBajoAdapter(List<Producto> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = lista.get(position);
        holder.tvNombre.setText(p.getNombre());
        holder.tvStock.setText("Stock: " + p.getStock());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizarLista(List<Producto> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvStock = itemView.findViewById(R.id.tvStockProducto);
        }
    }
}
