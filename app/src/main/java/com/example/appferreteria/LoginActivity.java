package com.example.appferreteria;



import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.example.appferreteria.databinding.ActivityLoginBinding;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginActivityViewModel vm;


    //llamada
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Permiso denegado para realizar llamadas", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        vm = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LoginActivityViewModel.class);
        setContentView(binding.getRoot());

        //

        // Solicitar permiso para llamadas si no lo tiene
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }

        // Observamos los "shakes"
        vm.getShake().observe(this, shake -> {
            if ("SHAKE".equals(shake)) {
                vm.hacerLlamada(this);
            }
        });


        vm.getMMensaje().

                observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usu = binding.etEmail.getText().toString();
                String contra = binding.etPassword.getText().toString();
                vm.login(usu, contra);
            }
        });

        binding.tvOlvideContra.setOnClickListener(v ->

        {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });


    }
    @Override
    protected void onResume () {
        super.onResume();
        vm.activarSensor();
    }

    @Override
    protected void onPause () {
        super.onPause();
        vm.desactivarSensor();
    }
}

