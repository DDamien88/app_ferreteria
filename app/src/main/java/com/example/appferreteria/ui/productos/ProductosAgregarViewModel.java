package com.example.appferreteria.ui.productos;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosAgregarViewModel extends AndroidViewModel {

    private final MutableLiveData<Producto> mProducto = new MutableLiveData<>();
    private final MutableLiveData<Uri> uriMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mNombre = new MutableLiveData<>();
    private boolean esEdicion = false;

    public ProductosAgregarViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Producto> getProducto() {
        return mProducto;
    }

    public LiveData<Uri> getUri() {
        return uriMutableLiveData;
    }

    public LiveData<String> getMNombre() {
        return mNombre;
    }

    public void recibirFoto(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Uri uri = result.getData().getData();
            uriMutableLiveData.setValue(uri);
        }
    }

    /**
     * Inicializa los campos si viene un producto para editar
     */
    public void setProductoParaEditar(Producto producto) {

        if (producto != null) {
            esEdicion = true;
            mProducto.setValue(producto);
            mNombre.setValue("Actualizar producto");
        } else {
            esEdicion = false;
            mNombre.setValue("Guardar producto");
        }
    }


    public void guardarProducto(String nombre,
                                String descripcion,
                                String categoria,
                                String precio,
                                String stock,
                                String proveedor,
                                String codigoBarras) {
        try {
            int stoc = Integer.parseInt(stock);
            double prec = Double.parseDouble(precio);

            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setCategoria(categoria);
            producto.setPrecio(prec);
            producto.setStock(stoc);
            producto.setProveedor(proveedor);
            producto.setCodigoBarras(codigoBarras);

            if (nombre.isEmpty() || descripcion.isEmpty() || categoria.isEmpty() || precio.isEmpty()
                    || stock.isEmpty() || proveedor.isEmpty() || codigoBarras.isEmpty()) {
                Toast.makeText(getApplication(), "No pude haber campos vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (esEdicion) {
                producto.setId(mProducto.getValue().getId());
                actualizarProducto(producto);
            } else {
                cargarProductoNuevo(producto);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getApplication(), "Ingrese números válidos", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * POST
     */
    private void cargarProductoNuevo(Producto producto) {
        byte[] imagen = transformarImagen();
        if (imagen.length == 0) {
            Toast.makeText(getApplication(), "Debe seleccionar foto", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody productoBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                new Gson().toJson(producto)
        );
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imagen);
        MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", "imagen.jpg", requestFile);

        String token = ApiClient.leerToken(getApplication());
        Call<Producto> llamada = ApiClient.getInmoServicio().cargarProducto("Bearer " + token, imagenPart, productoBody);
        llamada.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplication(), "Producto guardado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("API_ERROR", "Código: " + response.code() + " - " + response.message());
                    Toast.makeText(getApplication(), "Ya existe un producto con ese nombre o código interno." + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(getApplication(), "Error al guardar producto", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * PUT
     */

    public void actualizarProducto(Producto producto) {
        String token = ApiClient.leerToken(getApplication());

        Uri uri = uriMutableLiveData.getValue();
        MultipartBody.Part imagenPart = null;

        if (uri != null) {
            try {
                InputStream is = getApplication().getContentResolver().openInputStream(uri);
                byte[] bytes = readBytes(is);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
                imagenPart = MultipartBody.Part.createFormData("imagen", "imagen.jpg", requestFile);
            } catch (Exception e) {
                Toast.makeText(getApplication(), "Error con la imagen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        producto.setEstado(true);

        RequestBody productoBody = RequestBody.create(
                MediaType.parse("text/plain"),
                new Gson().toJson(producto)
        );

        Call<Producto> call = ApiClient.getInmoServicio()
                .actualizarProducto("Bearer " + token, producto.getId(), imagenPart, productoBody);

        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplication(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("API_ERROR Actualizar", "Código: " + response.code() + " - " + response.message());
                    Toast.makeText(getApplication(), "Error al actualizar: Ya existe un producto con ese nombre o código interno. " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Log.d("error actualizar", t.getMessage());
                Toast.makeText(getApplication(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private byte[] readBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private byte[] transformarImagen() {
        Uri uri = uriMutableLiveData.getValue();
        if (uri == null) return new byte[]{};
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplication(), "Error con la imagen", Toast.LENGTH_SHORT).show();
            return new byte[]{};
        }
    }
}
