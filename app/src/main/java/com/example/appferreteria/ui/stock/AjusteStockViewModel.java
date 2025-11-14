package com.example.appferreteria.ui.stock;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appferreteria.modelo.MovimientoInventario;
import com.example.appferreteria.request.ApiClient;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjusteStockViewModel extends AndroidViewModel {

    private MutableLiveData<String> mensaje = new MutableLiveData<>();

    public AjusteStockViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public void registrarMovimiento(MovimientoInventario movimiento) {
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();
        String token = ApiClient.leerToken(getApplication());

        Call<ResponseBody> call = api.registrarMovimiento("Bearer " + token, movimiento);
        Log.d("API_URL", call.request().url().toString());
        Log.d("API_BODY", new Gson().toJson(movimiento));


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    mensaje.postValue("Movimiento registrado con éxito.");
                } else {
                    Log.d("errorStock", response.message());
                    mensaje.postValue("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mensaje.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

}