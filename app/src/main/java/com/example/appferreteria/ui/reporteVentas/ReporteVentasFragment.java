package com.example.appferreteria.ui.reporteVentas;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appferreteria.R;
import com.example.appferreteria.databinding.FragmentReporteVentasBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReporteVentasFragment extends Fragment {
    private ReporteVentasViewModel viewModel;
    private FragmentReporteVentasBinding binding;

    private final int[] CHART_COLORS = new int[]{
            Color.rgb(255, 99, 132), Color.rgb(54, 162, 235), Color.rgb(255, 206, 86),
            Color.rgb(75, 192, 192), Color.rgb(153, 102, 255), Color.rgb(255, 159, 64)
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReporteVentasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReporteVentasViewModel.class);

        setupPeriodFilter();
        setupObservers();

        // Carga de datos inicial con el token y filtro por defecto
        viewModel.fetchSalesData("Mensual");
    }

    private void setupPeriodFilter() {
        String[] periods = new String[]{"Mensual", "Trimestral", "Anual"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                periods
        );

        AutoCompleteTextView autoCompleteTextView = binding.tilFiltroPeriodo.findViewById(R.id.autoFiltroPeriodo);
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setAdapter(adapter);

            autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                String selectedPeriod = (String) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Cargando datos para: " + selectedPeriod, Toast.LENGTH_SHORT).show();
                viewModel.fetchSalesData(selectedPeriod);
            });

            // Establecer un valor por defecto
            autoCompleteTextView.setText(periods[0], false);
        }
    }

    private void setupObservers() {
        viewModel.getSalesByPeriodData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && !data.isEmpty()) {
                loadBarChart(data);
            } else {
                binding.barChartVentas.clear();
                binding.barChartVentas.setNoDataText("No hay datos de ventas por período disponibles.");
            }
        });

        viewModel.getSalesByCategoryData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && !data.isEmpty()) {
                loadPieChart(data);
            } else {
                binding.pieChartCategorias.clear();
                binding.pieChartCategorias.setNoDataText("No hay datos de distribución de productos disponibles.");
            }
        });

        viewModel.getLoadingStatus().observe(getViewLifecycleOwner(), isLoading -> {
            if (!isLoading && getContext() != null &&
                    (viewModel.getSalesByPeriodData().getValue() == null || viewModel.getSalesByPeriodData().getValue().isEmpty())) {
                Log.e("Reportes", "La carga terminó, pero los datos están vacíos. Posiblemente error de API.");
            }
        });
    }

    // --- Métodos de configuración de gráficos (se mantienen sin cambios) ---
    private void loadBarChart(List<SalesEntry> salesData) {
        BarChart barChart = binding.barChartVentas;
        List<BarEntry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        for (int i = 0; i < salesData.size(); i++) {
            entries.add(new BarEntry(i, salesData.get(i).totalSales));
            xAxisLabels.add(salesData.get(i).periodLabel);
        }
        BarDataSet dataSet = new BarDataSet(entries, "Ventas Totales");
        dataSet.setColors(CHART_COLORS[0]);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        barChart.invalidate();
    }

    private void loadPieChart(Map<String, Float> categoryData) {
        PieChart pieChart = binding.pieChartCategorias;
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryData.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Productos");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.createColors(CHART_COLORS));
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterText("Distribución por Producto");
        pieChart.setCenterTextSize(14f);
        pieChart.animateY(1400);
        pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // =========================================================================
    // ESTRUCTURA MVVM & API DTOs
    // =========================================================================

    // Modelo interno para el Gráfico de Barras (datos agregados)
    public static class SalesEntry {
        // periodLabel usará 'dia' de tu modelo ReporteVentas
        public final String periodLabel;
        // totalSales usará 'totalVentas' de tu modelo ReporteVentas
        public final float totalSales;

        public SalesEntry(String periodLabel, float totalSales) {
            this.periodLabel = periodLabel;
            this.totalSales = totalSales;
        }
    }

}
