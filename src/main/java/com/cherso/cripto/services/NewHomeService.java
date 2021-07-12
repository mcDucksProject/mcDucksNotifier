package com.cherso.cripto.services;

import com.cherso.cripto.beans.BeanContexto;
import com.cherso.cripto.beans.BeanMoneda;
import com.cherso.cripto.beans.HistorialMoneda;
import com.cherso.cripto.beans.Respuesta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewHomeService {

    @Value("${urlPriceBinance}")
    private String urlPriceBinance;

    Logger logger = LoggerFactory.getLogger(NewHomeService.class);


    public List<BeanMoneda> buscarDatos(BeanContexto contexto) throws Exception {
        try {
            logger.info("Ingreso al Home Service");

            String res = peticionHttpGet(urlPriceBinance);
            List<BeanMoneda> listaCoins = parseBinancePriceList(res);

            List<BeanMoneda> listaMonedaSeleccionada = filtrarMonedas(listaCoins, contexto.getMoneda());

            return listaMonedaSeleccionada;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    private List<BeanMoneda> filtrarMonedas(List<BeanMoneda> listaCoins, String moneda) {

        List<BeanMoneda> filtrados = new ArrayList<>();

        for (BeanMoneda bean : listaCoins) {
            if (bean.getSymbol().contains(moneda)) {
                filtrados.add(bean);
            }
        }

        return filtrados;

    }

    private List<BeanMoneda> parseBinancePriceList(String res) throws JsonProcessingException {
        try {
            logger.info("Se intenta parsear el String");

            ObjectMapper mapper = new ObjectMapper();
            List<BeanMoneda> listCoins = mapper.readValue(res, new TypeReference<List<BeanMoneda>>() {
            });
            return listCoins;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public static String peticionHttpGet(String urlParaVisitar) throws Exception {
        StringBuilder resultado = new StringBuilder();
        URL url = new URL(urlParaVisitar);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        String linea;
        while ((linea = rd.readLine()) != null) {
            resultado.append(linea);
        }
        rd.close();
        return resultado.toString();
    }


    public List<HistorialMoneda> procesarDatos(List<BeanMoneda> listaMonedas, List<HistorialMoneda> historial) {

        List<HistorialMoneda> historialMonedas = new ArrayList<>();
        if (historial == null) {
            logger.info("PRIMERO ENTRA ACA");

            for (BeanMoneda moneda : listaMonedas) {
                HistorialMoneda hm = new HistorialMoneda();
                hm.setNombre(moneda.getSymbol());
                hm.setPrecioActual(moneda.getPrice());
                historialMonedas.add(hm);
            }
            return historialMonedas;

        } else {
            for (HistorialMoneda coinHistorial : historial) {
                BeanMoneda bm = buscarMoneda(coinHistorial.getNombre(), listaMonedas);
                String aux = coinHistorial.getPrecioActual();
                coinHistorial.setPrecioActual(bm.getPrice());
                coinHistorial.setPrecioAnterior(aux);

                Double answer = ((Double.parseDouble(coinHistorial.getPrecioActual()) / Double.parseDouble(coinHistorial.getPrecioAnterior())) - 1);
                BigDecimal formatNumber = new BigDecimal(answer);
                formatNumber = formatNumber.setScale(5, RoundingMode.DOWN);
                logger.info(coinHistorial.getNombre() + " - " + formatNumber);
//                coinHistorial.setMovimiento(formatNumber);

                double d = formatNumber.doubleValue();
                coinHistorial.setMovimiento(d);
            }
            logger.info("DESPUES ENTRA ACA");
            return historial;
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



}
