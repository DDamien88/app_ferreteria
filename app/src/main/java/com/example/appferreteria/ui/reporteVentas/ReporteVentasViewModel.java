package com.example.appferreteria.ui.reporteVentas;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.ReporteVentas;
import com.example.appferreteria.request.ApiClient;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteVentasViewModel extends AndroidViewModel {

    private  MutableLiveData<List<ReporteVentasFragment.SalesEntry>> salesByPeriodData = new MutableLiveData<>();
    private  MutableLiveData<Map<String, Float>> salesByCategoryData = new MutableLiveData<>();
    private  MutableLiveData<Boolean> loadingStatus = new MutableLiveData<>(false);


    private static final String BASE_URL = "http://10.0.2.2:5000/"; // Ajustar la URL base de tu API
    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Formato de fecha para la API



    public ReporteVentasViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ReporteVentasFragment.SalesEntry>> getSalesByPeriodData() {
        return salesByPeriodData;
    }

    public LiveData<Map<String, Float>> getSalesByCategoryData() {
        return salesByCategoryData;
    }

    public LiveData<Boolean> getLoadingStatus() {
        return loadingStatus;
    }


    public void fetchSalesData(String periodFilter) {
        loadingStatus.setValue(true);

        Map<String, String> dates = calculateDateRange(periodFilter);
        String fechaInicio = dates.get("inicio");
        String fechaFin = dates.get("fin");

        if (fechaInicio == null || fechaFin == null) {
            loadingStatus.setValue(false);
            Log.e("ViewModel", "Filtro de período no reconocido.");
            return;
        }

        // Llamada a la API usando el modelo ReporteVentas
        String token = ApiClient.leerToken(getApplication());
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();
        api.obtenerPorRango("Bearer " + token, fechaInicio, fechaFin).enqueue(new Callback<List<ReporteVentas>>() {
            @Override
            public void onResponse(Call<List<ReporteVentas>> call, Response<List<ReporteVentas>> response) {
                loadingStatus.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ReporteVentas> rawData = response.body();
                    Log.d("ViewModel", "Datos brutos recibidos: " + rawData.size() + " entradas.");

                    // 3. Transformación de datos de la API a modelos de gráfico
                    Map<String, Object> chartData = transformToChartData(rawData);

                    salesByPeriodData.setValue((List<ReporteVentasFragment.SalesEntry>) chartData.get("periodData"));
                    salesByCategoryData.setValue((Map<String, Float>) chartData.get("categoryData"));
                } else {
                    Log.e("ViewModel", "Error al cargar datos: " + response.code());
                    salesByPeriodData.setValue(new ArrayList<>());
                    salesByCategoryData.setValue(new HashMap<>());
                }
            }

            @Override
            public void onFailure(Call<List<ReporteVentas>> call, Throwable t) {
                loadingStatus.setValue(false);
                Log.e("ViewModel", "Fallo de red al obtener reportes", t);
                salesByPeriodData.setValue(new ArrayList<>());
                salesByCategoryData.setValue(new HashMap<>());
            }
        });
    }

    // --- Lógica de Utilidad ---

    /**
     * Convierte el nombre del período ("Mensual", "Anual") en un rango de fechas (ISO 8601).
     */
    private Map<String, String> calculateDateRange(String period) {
        Calendar cal = Calendar.getInstance();
        Calendar calInicio = (Calendar) cal.clone();
        Map<String, String> dates = new HashMap<>();

        switch (period) {
            case "Mensual":
                calInicio.add(Calendar.MONTH, -1);
                calInicio.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, 0);
                break;
            case "Trimestral":
                calInicio.add(Calendar.MONTH, -3);
                break;
            case "Anual":
                calInicio.add(Calendar.YEAR, -1);
                break;
            default:
                return new HashMap<>();
        }

        dates.put("inicio", API_DATE_FORMAT.format(calInicio.getTime()));
        dates.put("fin", API_DATE_FORMAT.format(cal.getTime()));
        return dates;
    }

    /**
     * Transforma la lista de datos brutos (ReporteVentas) en los dos formatos de gráfico requeridos.
     */
    private Map<String, Object> transformToChartData(List<ReporteVentas> rawData) {
        Map<String, Object> result = new HashMap<>();

        // 1. Datos para Gráfico de Torta (Agregación por Producto usando cantidadVendida)
        Map<String, Float> categoryMap = new HashMap<>();

        // 2. Datos para Gráfico de Barras (Agregación por Día usando totalVentas)
        Map<String, Double> periodMap = new HashMap<>(); // Usamos Double ya que totalVentas es Double

        for (ReporteVentas entry : rawData) {

            // Agregación para PIE CHART (Producto vs. Cantidad Vendida)
            if (entry.getProducto() != null && entry.getCantidadVendida() != null) {
                // Usamos la cantidad vendida como valor (convertida a float para el gráfico)
                categoryMap.put(entry.getProducto(), categoryMap.getOrDefault(entry.getProducto(), 0f) + entry.getCantidadVendida());
            }

            // Agregación para BAR CHART (Día vs. Total Ventas)
            if (entry.getDia() != null && entry.getTotalVentas() != null) {
                // Usamos el día como etiqueta y el totalVentas como valor
                periodMap.put(entry.getDia(), periodMap.getOrDefault(entry.getDia(), 0.0) + entry.getTotalVentas());
            }
        }

        // Convertir el Map de Períodos a la List<SalesEntry>
        List<ReporteVentasFragment.SalesEntry> periodDataList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : periodMap.entrySet()) {
            // Convertimos Double a float para el gráfico
            periodDataList.add(new ReporteVentasFragment.SalesEntry(entry.getKey(), entry.getValue().floatValue()));
        }

        result.put("periodData", periodDataList);
        result.put("categoryData", categoryMap);
        return result;
    }

}
