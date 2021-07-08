package com.cherso.cripto.beans;

import java.math.BigDecimal;

public class HistorialMoneda {

    private String nombre;
    private String precioAnterior;
    private String precioActual;
    private Double movimiento;

    public HistorialMoneda() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecioAnterior() {
        return precioAnterior;
    }

    public void setPrecioAnterior(String precioAnterior) {
        this.precioAnterior = precioAnterior;
    }

    public String getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(String precioActual) {
        this.precioActual = precioActual;
    }

    public Double getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(Double movimiento) {
        this.movimiento = movimiento;
    }
}
