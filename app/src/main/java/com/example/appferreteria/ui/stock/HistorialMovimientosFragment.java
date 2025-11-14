package com.example.appferreteria.ui.stock;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager; // Se sigue necesitando aquí

import com.example.appferreteria.databinding.FragmentHistorialMovimientosBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class HistorialMovimientosFragment extends Fragment {

    private FragmentHistorialMovimientosBinding binding;
    private HistorialMovimientosViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHistorialMovimientosBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(HistorialMovimientosViewModel.class);

        return binding.getRoot();
    }

    // --- NUEVO MÉTODO AÑADIDO / MODIFICADO ---
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvMovimientos.setLayoutManager(new LinearLayoutManager(getContext()));

        inicializarUI();
        observarViewModel();

        viewModel.cargarTodos();
    }

    // ----------------------------------------

    private void inicializarUI() {
        // Escaneo de código de barras
        binding.btnScan.setOnClickListener(v -> iniciarEscaneo());

        // Búsqueda por código
        binding.etBuscarCodigo.setOnEditorActionListener((v, actionId, event) -> {
            String codigo = v.getText().toString();
            viewModel.cargarPorCodigo(codigo);
            return true;
        });
    }

    private void observarViewModel() {
        viewModel.getMovimientos().observe(getViewLifecycleOwner(), movimientos -> {
            // Solo asignamos el adapter aquí
            MovimientoAdapter adapter = new MovimientoAdapter(movimientos, getContext(), getLayoutInflater());
            binding.rvMovimientos.setAdapter(adapter);
        });

        viewModel.getMensaje().observe(getViewLifecycleOwner(),
                msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
    }

    // -------------------------------
    // ESCANEO DE CÓDIGO (Sin cambios)
    // -------------------------------
    private void iniciarEscaneo() {
        binding.previewView.setVisibility(View.VISIBLE);

        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(requireContext());

        future.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = future.get();
                iniciarCamara(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void iniciarCamara(ProcessCameraProvider cameraProvider) {
        BarcodeScanner scanner = BarcodeScanning.getClient();
        Preview preview = new Preview.Builder().build();
        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis analysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        analysis.setAnalyzer(Executors.newSingleThreadExecutor(), imageProxy -> {
            if (imageProxy.getImage() != null) {
                InputImage img = InputImage.fromMediaImage(
                        imageProxy.getImage(),
                        imageProxy.getImageInfo().getRotationDegrees()
                );

                scanner.process(img)
                        .addOnSuccessListener(barcodes -> procesarCodigos(barcodes, cameraProvider))
                        .addOnCompleteListener(task -> imageProxy.close());
            }
        });

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, selector, preview, analysis);
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
    }


    private void procesarCodigos(List<Barcode> barcodes, ProcessCameraProvider cameraProvider) {
        for (Barcode barcode : barcodes) {
            String codigo = barcode.getRawValue();
            if (codigo == null) continue;

            requireActivity().runOnUiThread(() -> {
                binding.etBuscarCodigo.setText(codigo);
                binding.previewView.setVisibility(View.GONE);
                viewModel.cargarPorCodigo(codigo);
            });

            cameraProvider.unbindAll();
            break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}