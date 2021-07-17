package com.cherso.cripto.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class BeanHistorial {

    Logger logger = LoggerFactory.getLogger(BeanHistorial.class);

    List<HistorialMoneda> historial;

    public void ingresarDatos(List<BeanMoneda> listaMonedas) {
        if(historial == null) {
            ingresarPrimerosDatos(listaMonedas);
        } else {
            actualizarDatos(listaMonedas);
        }
    }

    private void ingresarPrimerosDatos(List<BeanMoneda> listaMonedas) {

        logger.info(" *** SE INGRESAN LOS PRIMEROS DATOS ***");

        historial = new ArrayList<>();

        for (BeanMoneda moneda : listaMonedas) {

            HistorialMoneda hm = new HistorialMoneda();
            hm.setNombre(moneda.getSymbol());
            hm.setPrecioActual(moneda.getPrice());
            historial.add(hm);
        }

    }

    private void actualizarDatos(List<BeanMoneda> listaMonedas) {

        logger.info(" *** SE ACTUALIZAN LOS DATOS ***");

        for (HistorialMoneda coinHistorial : historial) {
            BeanMoneda bm = buscarMoneda(coinHistorial.getNombre(), listaMonedas);
            String aux = coinHistorial.getPrecioActual();
            coinHistorial.setPrecioActual(bm.getPrice());
            coinHistorial.setPrecioAnterior(aux);
            Double answer = ((Double.parseDouble(coinHistorial.getPrecioActual()) / Double.parseDouble(coinHistorial.getPrecioAnterior())) - 1);
            BigDecimal formatNumber = new BigDecimal(answer);
            formatNumber = formatNumber.setScale(5, RoundingMode.DOWN);
            coinHistorial.setMovimiento(formatNumber.doubleValue());
        }
    }

    private BeanMoneda buscarMoneda(String nombre, List<BeanMoneda> listaMonedas) {

        for (BeanMoneda bm : listaMonedas) {
            if (bm.getSymbol().equals(nombre)) {
                return bm;
            }
        }

        return null;
    }

    public boolean estaVacio()
    {
        if(historial == null) {
            return true;
        }
        return false;
    }

    public List<HistorialMoneda> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialMoneda> historial) {
        this.historial = historial;
    }
}
