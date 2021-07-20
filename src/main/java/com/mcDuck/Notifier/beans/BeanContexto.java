package com.mcDuck.Notifier.beans;

public class BeanContexto {

    private String moneda;
    private int tiempo;
    private Double movimiento;

    public BeanContexto(String moneda, int tiempo, Double movimiento) {
        this.moneda = moneda;
        this.tiempo = tiempo;
        this.movimiento = movimiento;
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
        return "" + moneda + ";" + tiempo + ";" + movimiento;
    }
}
