package com.mcDuck.Notifier.services;

import com.mcDuck.Notifier.beans.BeanContexto;
import com.mcDuck.Notifier.beans.BeanMoneda;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
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
        logger.info("buscarDatos");
        try {
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

}
