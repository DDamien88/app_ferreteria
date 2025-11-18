package com.example.appferreteria.ui.productos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.appferreteria.R;
import com.example.appferreteria.databinding.FragmentDetalleProductosBinding;
import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;

public class DetalleProductosFragment extends Fragment {

    private FragmentDetalleProductosBinding binding;
    private DetalleProductosViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleProductosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(DetalleProductosViewModel.class);

        Producto p = (Producto) getArguments().getSerializable("productoBundle");
        vm.setProducto(p);

        vm.getProducto().observe(getViewLifecycleOwner(), producto -> {
            binding.tvNombreDetalle.setText(producto.getNombre());
            binding.tvCategoriaDetalle.setText("Categoría: " + producto.getCategoria());
            binding.tvPrecioDetalle.setText("$ " + producto.getPrecio());
            binding.tvStockDetalle.setText("Stock: " + producto.getStock());
            binding.tvDescripcionDetalle.setText(producto.getDescripcion());

            // Mostrar imagen con Glide
            String imagenUrl = producto.getImagen();
            if (imagenUrl != null && imagenUrl.startsWith("/")) {
                imagenUrl = imagenUrl.substring(1);
            }
            imagenUrl = ApiClient.BASE_URL + imagenUrl;

            Glide.with(requireContext())
                    .load(imagenUrl)
                    .placeholder(R.drawable.fotologin)
                    .error(R.drawable.fotologin)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgProductoLarge);
        });

        vm.getMensaje().observe(getViewLifecycleOwner(),
                msg -> Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        );

        // Acción editar
        binding.btnEditarProducto.setOnClickListener(v -> {
            Producto producto = vm.getProducto().getValue();
            Bundle bundle = new Bundle();
            bundle.putSerializable("productoBundle", producto);
            Navigation.findNavController(v).navigate(
                    R.id.productosAgregarFragment,
                    bundle
            );
        });

        binding.btnBajaProducto.setOnClickListener(v -> {
            Producto producto = vm.getProducto().getValue(); // ← Obtenemos el producto actual
            if (producto == null) {
                Toast.makeText(getContext(), "No se encontró el producto", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmar baja")
                    .setMessage("¿Seguro que deseas dar de baja este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> vm.darDeBajaProducto(producto))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        vm.getMNavegar().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Navigation.findNavController(view).navigate(R.id.nav_home);
            }
        });

    }
}
