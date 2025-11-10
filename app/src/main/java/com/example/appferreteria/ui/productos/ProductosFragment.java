package com.example.appferreteria.ui.productos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;


import com.example.appferreteria.databinding.FragmentProductosBinding;
import com.example.appferreteria.modelo.Producto;

import java.util.List;

public class ProductosFragment extends Fragment {

    private FragmentProductosBinding binding;
    private ProductosViewModel vm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        vm = new ViewModelProvider(this).get(ProductosViewModel.class);
        binding = FragmentProductosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        vm.getListaProductos().observe(getViewLifecycleOwner(), new Observer<List<Producto>>() {
            @Override
            public void onChanged(List<Producto> productos) {
                ProductoAdapter adapter = new com.example.appferreteria.ui.productos.ProductoAdapter(getContext(),productos,getLayoutInflater());
                GridLayoutManager glm=new GridLayoutManager(getContext(),1,GridLayoutManager.VERTICAL,false);
                binding.rvProductos.setLayoutManager(glm);
                binding.rvProductos.setAdapter(adapter);
            }
        });

        vm.getMMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            if (!mensaje.isEmpty()) {
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });



        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.filtrarProductos(s.toString());
            }
        });


        vm.obtenerListaProductos();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
