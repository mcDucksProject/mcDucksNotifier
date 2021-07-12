package com.cherso.cripto.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
//
//@Component
//@Scope("session")
public class BeanContexto {

    private String moneda;
    private int tiempo;
    private Double movimiento;

    public BeanContexto(String moneda, int tiempo, Double movimiento) {
        System.out.println("CONTROLADOR BEAN CONTEXTO");
        this.moneda = moneda;
        this.tiempo = tiempo;
        this.movimiento = movimiento;
    }

    public BeanContexto() {

    }

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

    @Override
    public String toString() {
        return "" + moneda + ";" + tiempo + ";" + movimiento + ";;";
    }
}
