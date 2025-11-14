package com.example.appferreteria.ui.stock;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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

import com.example.appferreteria.databinding.FragmentAjustestockBinding;
import com.example.appferreteria.modelo.MovimientoInventario;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class AjusteStockFragment extends Fragment {

    private FragmentAjustestockBinding binding;
    private AjusteStockViewModel viewModel;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAjustestockBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AjusteStockViewModel.class);

        viewModel.getMensaje().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );

        binding.btnConfirmarAjuste.setOnClickListener(v -> registrarMovimiento());
        binding.btnEscanearCodigo.setOnClickListener(v -> verificarPermisoCamara());

        return binding.getRoot();
    }

    /*** 🔹 Paso 1: Pedir permiso antes de iniciar el escaneo ***/
    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            iniciarEscaneo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarEscaneo();
            } else {
                Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*** 🔹 Paso 2: Iniciar la cámara ***/
    private void iniciarEscaneo() {
        binding.previewView.setVisibility(View.VISIBLE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void startCamera(ProcessCameraProvider cameraProvider) {
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
                InputImage image = InputImage.fromMediaImage(
                        imageProxy.getImage(),
                        imageProxy.getImageInfo().getRotationDegrees()
                );

                scanner.process(image)
                        .addOnSuccessListener(barcodes -> procesarCodigos(barcodes, cameraProvider))
                        .addOnCompleteListener(task -> imageProxy.close());
            } else {
                imageProxy.close();
            }
        });

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, selector, preview, analysis);
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
    }

    private void procesarCodigos(List<Barcode> barcodes, ProcessCameraProvider cameraProvider) {
        for (Barcode barcode : barcodes) {
            if (barcode.getRawValue() != null) {
                String codigo = barcode.getRawValue();

                requireActivity().runOnUiThread(() -> {
                    binding.etProductoAjuste.setText(codigo);
                    binding.previewView.setVisibility(View.GONE);
                });

                cameraProvider.unbindAll();
                break;
            }
        }
    }

    private void registrarMovimiento() {
        String producto = binding.etProductoAjuste.getText().toString();
        String tipo = binding.rbCompra.isChecked() ? "Compra" : "Inventario";
        String cantidadStr = binding.etCantidadAjuste.getText().toString();
        String observacion = binding.etObservacion.getText().toString();

        if (producto.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        MovimientoInventario mov = new MovimientoInventario();
        mov.setCodigoBarras(producto);
        mov.setTipo(tipo);
        mov.setCantidad(Integer.parseInt(cantidadStr));
        mov.setObservacion(observacion);

        viewModel.registrarMovimiento(mov);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
