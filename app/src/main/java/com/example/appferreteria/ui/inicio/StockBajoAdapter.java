package com.example.appferreteria.ui.inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appferreteria.R;
import com.example.appferreteria.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class StockBajoAdapter extends RecyclerView.Adapter<StockBajoAdapter.StockBajoViewHolder> {

    private List<Producto> lista = new ArrayList<>();

    public StockBajoAdapter() {}

    @NonNull
    @Override
    public StockBajoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_bajo_item, parent, false);
        return new StockBajoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockBajoViewHolder holder, int position) {
        Producto producto = lista.get(position);
        holder.nombre.setText(producto.getNombre());
        holder.stock.setText("Stock: " + producto.getStock());
        holder.stockMinimo.setText("Mínimo: " + "5");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setProductos(List<Producto> productos) {
        this.lista = productos;
        notifyDataSetChanged();
    }

    static class StockBajoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, stock, stockMinimo;

        public StockBajoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombreProducto);
            stock = itemView.findViewById(R.id.tvStockProducto);
            stockMinimo = itemView.findViewById(R.id.tvStockMinimo);
        }
    }
}
