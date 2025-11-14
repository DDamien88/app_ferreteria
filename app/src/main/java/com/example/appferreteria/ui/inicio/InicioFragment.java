package com.example.appferreteria.ui.inicio;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.appferreteria.R;
import com.example.appferreteria.databinding.FragmentInicioBinding;
import com.example.appferreteria.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private InicioViewModel viewModel;
    private StockBajoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(InicioViewModel.class);

        // Inicializar el adapter con lista vacía
        adapter = new StockBajoAdapter(new ArrayList<>());
        binding.rvStockBajo.setAdapter(adapter);

        // Observamos LiveData del ViewModel
        viewModel.getProductosBajoStock().observe(getViewLifecycleOwner(), productos -> {
            adapter.actualizarLista(productos);
            binding.tvStockCritico.setText("Productos con stock bajo: " + productos.size());
        });

        // Quick actions
        binding.btnAgregarProducto.setOnClickListener(v -> {
            // Navegar a agregar producto
            Navigation.findNavController(v).navigate(R.id.productosAgregarFragment);
        });

        binding.btnAjustarStock.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_slideshow);
        });

        binding.btnVerReportes.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.fragmentReportes);
        });






        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
