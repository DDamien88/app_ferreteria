package com.example.appferreteria.ui.notificaciones;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appferreteria.databinding.FragmentNotificacionBinding;
import com.example.appferreteria.modelo.Notificacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificacionFragment extends Fragment {

    private FragmentNotificacionBinding binding;
    private NotificacionViewModel viewModel;
    private NotificacionAdapter adapter;

    private List<Notificacion> listaCompleta = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNotificacionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // RecyclerView
        binding.rvNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificacionAdapter();
        binding.rvNotificaciones.setAdapter(adapter);

        // DatePicker al tocar fecha
        binding.etBuscarFecha.setOnClickListener(v -> mostrarDatePicker());

        // Botón FILTRAR
        binding.btnFiltrar.setOnClickListener(v -> aplicarFiltros());

        // Botón LIMPIAR
        binding.btnLimpiar.setOnClickListener(v -> limpiarFiltros());

        viewModel = new ViewModelProvider(this).get(NotificacionViewModel.class);

        viewModel.getNotificaciones().observe(getViewLifecycleOwner(), lista -> {
            listaCompleta = lista;

            if (lista == null || lista.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvNotificaciones.setVisibility(View.GONE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
                binding.rvNotificaciones.setVisibility(View.VISIBLE);
                adapter.setNotificaciones(lista);
            }
        });

        viewModel.cargarNotificaciones();

        return root;
    }

    // ---------------------------------------
    //              FILTROS
    // ---------------------------------------

    private void aplicarFiltros() {
        String usuarioFiltro = binding.etBuscarUsuario.getText().toString().trim().toLowerCase();
        String fechaFiltro = binding.etBuscarFecha.getText().toString().trim();

        List<Notificacion> filtrada = new ArrayList<>();

        for (Notificacion n : listaCompleta) {

            boolean coincideUsuario = true;
            boolean coincideFecha = true;

            // ---- FILTRO USUARIO ----
            if (!usuarioFiltro.isEmpty()) {
                String nombreCompleto = (n.getUsuarioNombre() + " " + n.getUsuarioApellido()).toLowerCase();
                coincideUsuario = nombreCompleto.contains(usuarioFiltro);
            }

            // ---- FILTRO FECHA ----
            if (!fechaFiltro.isEmpty()) {
                String fechaFormateada = formatearFecha(n.getFecha());
                coincideFecha = fechaFormateada.equals(fechaFiltro);
            }

            if (coincideUsuario && coincideFecha) {
                filtrada.add(n);
            }
        }

        adapter.setNotificaciones(filtrada);

        if (filtrada.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvNotificaciones.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvNotificaciones.setVisibility(View.VISIBLE);
        }
    }

    private void limpiarFiltros() {
        binding.etBuscarUsuario.setText("");
        binding.etBuscarFecha.setText("");

        adapter.setNotificaciones(listaCompleta);
        binding.tvEmpty.setVisibility(View.GONE);
        binding.rvNotificaciones.setVisibility(View.VISIBLE);
    }

    // ---------------------------------------
    //          DATE PICKER
    // ---------------------------------------

    private void mostrarDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            binding.etBuscarFecha.setText(sdf.format(c.getTime()));

        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // Convierte fecha del backend a dd/MM/yyyy
    private String formatearFecha(String fecha) {

        String[] entradas = {
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String formato : entradas) {
            try {
                SimpleDateFormat in = new SimpleDateFormat(formato);
                SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
                return out.format(in.parse(fecha));
            } catch (Exception ignored) {}
        }

        return fecha; // fallback
    }
}
