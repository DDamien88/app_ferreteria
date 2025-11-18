package com.example.appferreteria.ui.inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appferreteria.R;
import com.example.appferreteria.databinding.FragmentInicioBinding;
import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.modelo.MovimientoInventario;
import com.example.appferreteria.ui.inicio.StockBajoAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InicioFragment extends Fragment {

    private InicioViewModel viewModel;
    private FragmentInicioBinding binding;
    private StockBajoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);

        // RecyclerView
        adapter = new StockBajoAdapter();
        binding.rvStockBajo.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvStockBajo.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(InicioViewModel.class);

        viewModel.getProductos().observe(getViewLifecycleOwner(), productos ->
                actualizarResumen(productos, viewModel.getMovimientos().getValue())
        );
        viewModel.getMovimientos().observe(getViewLifecycleOwner(), movimientos ->
                actualizarResumen(viewModel.getProductos().getValue(), movimientos)
        );
        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.cargarResumen();

        return binding.getRoot();
    }

    private void actualizarResumen(List<Producto> productos, List<MovimientoInventario> movimientos) {
        if (productos == null || movimientos == null) return;

        binding.tvTotalProductos.setText("Total productos: " + productos.size());

        int stockCritico = 0;
        int stockBajo = 5;
        List<Producto> bajoStock = new ArrayList<>();

        for (Producto p : productos) {
            int minimo = stockBajo;
            if (p.getStock() <= minimo) {
                stockCritico++;
                bajoStock.add(p);
            }
        }

        binding.tvStockCritico.setText("Productos con stock bajo: " + stockCritico);
        if (bajoStock.size() == 0) {
            // Mostrar mensaje y ocultar lista
            binding.tvNoStockBajo.setVisibility(View.VISIBLE);
            binding.rvStockBajo.setVisibility(View.GONE);
        } else {
            // Mostrar lista y ocultar mensaje
            binding.tvNoStockBajo.setVisibility(View.GONE);
            binding.rvStockBajo.setVisibility(View.VISIBLE);

            adapter.setProductos(bajoStock);
        }


        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm 'hs'", Locale.getDefault());

        StringBuilder ultMov = new StringBuilder("Últimos movimientos:\n");

        for (int i = 0; i < Math.min(movimientos.size(), 5); i++) {
            MovimientoInventario m = movimientos.get(i);
            String fechaFormateada;

            try {
                Date fecha = formatoEntrada.parse(m.getFecha());
                fechaFormateada = formatoSalida.format(fecha);
            } catch (ParseException e) {
                fechaFormateada = m.getFecha();
            }

            ultMov.append(m.getTipo())
                    .append(" - ").append(m.getCantidad())
                    .append(" - ").append(m.getProducto().getNombre() + m.getProducto().getDescripcion())
                    .append(" - ").append(fechaFormateada)
                    .append("\n");
        }

        binding.tvUltimosMovimientos.setText(ultMov.toString());

        // Quick actions
        binding.btnAgregarProducto.setOnClickListener(v -> {
            // Navegar a agregar producto
            Navigation.findNavController(v).navigate(R.id.productosAgregarFragment);
        });

        binding.btnAjustarStock.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_slideshow);
        });

        binding.btnVerReportes.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.reporteVentasFragment);
        });

    }
}
