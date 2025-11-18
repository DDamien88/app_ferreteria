package com.example.appferreteria.modelo;

import java.util.List;

public class Inicio {


    private final int totalProducts;
    private final int criticalStockCount;
    private final List<ProductoSimple> lowStockProducts;

    public Inicio(int totalProducts, int criticalStockCount, List<ProductoSimple> lowStockProducts) {
        this.totalProducts = totalProducts;
        this.criticalStockCount = criticalStockCount;
        this.lowStockProducts = lowStockProducts;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getCriticalStockCount() {
        return criticalStockCount;
    }

    public List<ProductoSimple> getLowStockProducts() {
        return lowStockProducts;
    }

    // Clase simple para la lista de stock bajo
    public static class ProductoSimple {
        public final int id;
        public final String name;
        public final int stock;

        public ProductoSimple(int id, String name, int stock) {
            this.id = id;
            this.name = name;
            this.stock = stock;
        }
    }
}
