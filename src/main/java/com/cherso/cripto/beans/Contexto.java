package com.cherso.cripto.beans;

import org.springframework.stereotype.Component;

@Component
public class Contexto {

    private String moneda = "BTC";
    private int tiempo = 10;
    private Double movimiento = 0.001;

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public Double getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(Double movimiento) {
        this.movimiento = movimiento;
    }
}
