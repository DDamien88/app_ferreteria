package com.example.appferreteria.ui.notificaciones;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appferreteria.R;
import com.example.appferreteria.modelo.Notificacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificacionFragment extends Fragment {

    private NotificacionViewModel viewModel;
    private NotificacionAdapter adapter;
    private RecyclerView rv;
    private TextView tvEmpty;

    private EditText etBuscarUsuario, etBuscarFecha;
    private Button btnFiltrar, btnLimpiar;

    private List<Notificacion> listaCompleta = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notificacion, container, false);

        rv = root.findViewById(R.id.rvNotificaciones);
        tvEmpty = root.findViewById(R.id.tvEmpty);

        etBuscarUsuario = root.findViewById(R.id.etBuscarUsuario);
        etBuscarFecha = root.findViewById(R.id.etBuscarFecha);
        btnFiltrar = root.findViewById(R.id.btnFiltrar);
        btnLimpiar = root.findViewById(R.id.btnLimpiar);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificacionAdapter();
        rv.setAdapter(adapter);

        // DatePicker al tocar el campo fecha
        etBuscarFecha.setOnClickListener(v -> mostrarDatePicker());

        // Botón FILTRAR
        btnFiltrar.setOnClickListener(v -> aplicarFiltros());

        // Botón LIMPIAR
        btnLimpiar.setOnClickListener(v -> limpiarFiltros());

        viewModel = new ViewModelProvider(this).get(NotificacionViewModel.class);

        viewModel.getNotificaciones().observe(getViewLifecycleOwner(), lista -> {
            listaCompleta = lista;  // guardamos toda la lista original

            if (lista == null || lista.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
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
        String usuarioFiltro = etBuscarUsuario.getText().toString().trim().toLowerCase();
        String fechaFiltro = etBuscarFecha.getText().toString().trim(); // dd/MM/yyyy

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
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    private void limpiarFiltros() {
        etBuscarUsuario.setText("");
        etBuscarFecha.setText("");

        adapter.setNotificaciones(listaCompleta);
        tvEmpty.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
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
            etBuscarFecha.setText(sdf.format(c.getTime()));

        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // Convierte la fecha del backend a dd/MM/yyyy
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
