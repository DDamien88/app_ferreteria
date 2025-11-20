package com.example.appferreteria.ui.usuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appferreteria.databinding.FragmentRegistrarUsuarioBinding;

public class RegistrarUsuarioFragment extends Fragment {

    private FragmentRegistrarUsuarioBinding binding;
    private RegistrarUsuarioViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentRegistrarUsuarioBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(RegistrarUsuarioViewModel.class);

        String[] roles = {"Dueño", "encargado"};

        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                roles
        );

        binding.etRol.setAdapter(adapterRoles);
        binding.etRol.setKeyListener(null);


        binding.btnRegistrar.setOnClickListener(v -> {
            viewModel.registrarUsuario(
                    binding.etNombre.getText().toString().trim(),
                    binding.etApellido.getText().toString().trim(),
                    binding.etDni.getText().toString().trim(),
                    binding.etTelefono.getText().toString().trim(),
                    binding.etEmail.getText().toString().trim(),
                    binding.etPassword.getText().toString().trim(),
                    binding.etRol.getText().toString().trim()
            );
        });

        viewModel.registroExitoso.observe(getViewLifecycleOwner(), exito -> {
            if (exito) {
                Toast.makeText(requireContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
        });

        viewModel.mensajeError.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void limpiarCampos() {
        binding.etNombre.setText("");
        binding.etApellido.setText("");
        binding.etDni.setText("");
        binding.etEmail.setText("");
        binding.etTelefono.setText("");
        binding.etPassword.setText("");
        binding.etRol.setText("");
    }
}
