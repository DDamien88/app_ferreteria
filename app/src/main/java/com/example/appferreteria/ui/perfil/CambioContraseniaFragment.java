package com.example.appferreteria.ui.perfil;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appferreteria.R;
import com.example.appferreteria.databinding.FragmentCambiocontraseniaBinding;
import com.example.appferreteria.request.ApiClient;

public class CambioContraseniaFragment extends Fragment {

    private CambioContraseniaViewModel vm;
    private ApiClient api;
    private FragmentCambiocontraseniaBinding binding;

    public static CambioContraseniaFragment newInstance() {
        return new CambioContraseniaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCambiocontraseniaBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(CambioContraseniaViewModel.class);

        // Observar mensajes del ViewModel
        vm.getMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        });

        binding.btnGuardarContraNueva.setOnClickListener(v -> {
            String vieja = binding.etContraVieja.getText().toString().trim();
            String nueva = binding.etContraNueva.getText().toString().trim();

            String token = ApiClient.leerToken(getContext());
            vm.cambiarPassword(vieja, nueva);
        });

        return binding.getRoot();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}