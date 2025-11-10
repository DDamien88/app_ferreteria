package com.example.appferreteria.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appferreteria.R;
import com.example.appferreteria.databinding.FragmentPerfilBinding;


public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(PerfilViewModel.class);

        vm.getUsuario().observe(getViewLifecycleOwner(), usuario -> {

            //binding.etCodigo.setText(String.valueOf(usuario.getId()));
            binding.etDniPerfil.setText(usuario.getDni());
            binding.etNombrePerfil.setText(usuario.getNombre());
            binding.etApellidoPerfil.setText(usuario.getApellido());
            binding.etEmailPerfil.setText(usuario.getEmail());
            binding.etTelefonoPerfil.setText(usuario.getTelefono());
            binding.etEmailPerfil.setText(usuario.getEmail());
            binding.etRolPerfil.setText(usuario.getRol());

        });

        vm.obtenerPerfil();

        //Actualizar
        vm.getMEstado().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.etNombrePerfil.setEnabled(aBoolean);
                binding.etApellidoPerfil.setEnabled(aBoolean);
                binding.etDniPerfil.setEnabled(aBoolean);
                binding.etEmailPerfil.setEnabled(aBoolean);
                binding.etTelefonoPerfil.setEnabled(aBoolean);
                //binding.etRolPerfil.setEnabled(aBoolean);
            }
        });

        vm.getMNombre().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.btnEditarPerfil.setText(s);
            }
        });

        binding.btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.cambioBoton(binding.btnEditarPerfil.getText().toString(),
                        binding.etNombrePerfil.getText().toString(),
                        binding.etApellidoPerfil.getText().toString(),
                        binding.etDniPerfil.getText().toString(),
                        binding.etEmailPerfil.getText().toString(),
                        binding.etTelefonoPerfil.getText().toString(),
                        binding.etRolPerfil.getText().toString());
            }

        });

        binding.btnCambiarContra.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.cambiocontraseniaFragment);
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
