package com.example.appferreteria.ui.productos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appferreteria.R;
import com.example.appferreteria.databinding.FragmentProductosAgregarBinding;
import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;

public class ProductosAgregarFragment extends Fragment {

    private ProductosAgregarViewModel vm;
    private FragmentProductosAgregarBinding binding;
    private Intent intent;
    private ActivityResultLauncher<Intent> arl;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductosAgregarBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(this).get(ProductosAgregarViewModel.class);


        // Acceso vía ViewBinding
        AutoCompleteTextView autoCategoria = binding.autoCategoria;

// Lista de categorías
        String[] categorias = {
                "Insumos varios",
                "Protección Personal (EPP)",
                "Limpieza",
                "Seguridad",
                "Pintura",
                "Plomería",
                "Electricidad",
                "Máquinas y Equipos",
                "Herramientas"
        };

// Adaptador
        ArrayAdapter<String> adapterCategorias =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, categorias);

// Setear adaptador
        autoCategoria.setAdapter(adapterCategorias);

// Bloquear escritura
        autoCategoria.setInputType(0);
        autoCategoria.setKeyListener(null);

// Mostrar menú automáticamente
        autoCategoria.setOnClickListener(v -> autoCategoria.showDropDown());


        vm.getMNombre().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.btnGuardarProducto.setText(s);
            }
        });


        abrirGaleria();
        binding.btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arl.launch(intent);
            }
        });
        binding.btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarProducto();
            }
        });

        vm.getUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                binding.imgProductoPreview.setImageURI(uri);
            }
        });


        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Producto productoParaEditar = null;
        if (getArguments() != null) {
            productoParaEditar = (Producto) getArguments().getSerializable("productoBundle");
        }

        if (productoParaEditar != null) {
            vm.setProductoParaEditar(productoParaEditar);


            binding.etNombreProducto.setText(productoParaEditar.getNombre());
            binding.etDescripcion.setText(productoParaEditar.getDescripcion());
            binding.autoCategoria.setText(productoParaEditar.getCategoria());
            binding.etPrecio.setText(String.valueOf(productoParaEditar.getPrecio()));
            binding.etStock.setText(String.valueOf(productoParaEditar.getStock()));
            binding.etProveedor.setText(productoParaEditar.getProveedor());
            binding.etCodigoBarras.setText(productoParaEditar.getCodigoBarras());

            // Mostrar imagen con Glide
            Glide.with(requireContext())
                    .load(ApiClient.BASE_URL + productoParaEditar.getImagen())
                    .placeholder(R.drawable.fotologin)
                    .into(binding.imgProductoPreview);
        } else {
            vm.setProductoParaEditar(null);
        }
    }


    private void cargarProducto() {
        String nombre = binding.etNombreProducto.getText().toString();
        String descripcion = binding.etDescripcion.getText().toString();
        String categoria = binding.autoCategoria.getText().toString();
        String precio = binding.etPrecio.getText().toString();
        String stock = binding.etStock.getText().toString();
        String proveedor = binding.etProveedor.getText().toString();
        String codigoBarras = binding.etCodigoBarras.getText().toString();


        /*if (nombre.isEmpty() || descripcion.isEmpty() || categoria.isEmpty() || precio.isEmpty()
                || stock.isEmpty() || proveedor.isEmpty() || codigoBarras.isEmpty()) {
            Toast.makeText(getContext(), "No pude haber campos vacíos", Toast.LENGTH_SHORT).show();
            return;
        }*/
        vm.guardarProducto(nombre, descripcion, categoria, precio, stock, proveedor, codigoBarras);
    }

    private void abrirGaleria() {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                vm.recibirFoto(result);
                binding.btnGuardarProducto.setEnabled(true);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


