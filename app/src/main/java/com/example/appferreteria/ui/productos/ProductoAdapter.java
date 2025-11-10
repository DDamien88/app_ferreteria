package com.example.appferreteria.ui.productos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.appferreteria.R;
import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolderProducto> {

    private Context context;
    private List<Producto> listado;
    private LayoutInflater inflater;

    public ProductoAdapter(Context context, List<Producto> listado, LayoutInflater inflater) {
        this.context = context;
        this.listado = listado;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ViewHolderProducto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_producto, parent, false);
        return new ViewHolderProducto(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProducto holder, int position) {
        Producto productoActual = listado.get(position);

        holder.tvNombre.setText("Nombre: " + productoActual.getNombre());
        holder.tvCategoria.setText("Categoría: " + productoActual.getCategoria());
        holder.tvPrecio.setText("Precio: $" + productoActual.getPrecio());
        holder.tvStock.setText("Cantidad stock: " + productoActual.getStock());

        // Asegurarse de que la URL no tenga doble barra
        String imagenUrl = productoActual.getImagen();
        if (imagenUrl != null && imagenUrl.startsWith("/")) {
            imagenUrl = imagenUrl.substring(1);
        }
        imagenUrl = ApiClient.BASE_URL + imagenUrl;

        Glide.with(context)
                .load(imagenUrl)
                .placeholder(R.drawable.fotologin)
                .error(R.drawable.fotologin)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProducto);

        // Click para ir a detalle
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("productoBundle", productoActual);
            Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.detalleProductosFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return listado.size();
    }

    public static class ViewHolderProducto extends RecyclerView.ViewHolder {

        TextView tvNombre, tvPrecio, tvCategoria, tvStock;
        ImageView imgProducto;

        public ViewHolderProducto(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvStock = itemView.findViewById(R.id.tvStock);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}
