package com.cherso.cripto.controllers;

import com.cherso.cripto.beans.BeanContexto;
import com.cherso.cripto.beans.BeanMoneda;
import com.cherso.cripto.beans.HistorialMoneda;
import com.cherso.cripto.services.HomeService;
import com.cherso.cripto.services.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeControllerCron extends TelegramLongPollingBot {

    Logger logger = LoggerFactory.getLogger(HomeControllerCron.class);

    @Autowired
    HomeService homeService;

    @Autowired
    TelegramBotService TBS;


    BeanContexto contexto = new BeanContexto("BTC", 1, 1.0);

    Double porcentajeMovimiento = 0.01;
    int minutos = 1;
    String moneda = "USDT";

    List<HistorialMoneda> historial;

    //        @Scheduled(cron = "*/60 * * * * *")
//    @Scheduled(cron = "*/60 * * * * *")
//    public void calApiBinance() throws Exception {
//        // Busco Datos en Binance
//        List<BeanMoneda> listaMonedas = homeService.buscarDatos(contexto);
//
//        historial = homeService.procesarDatos(listaMonedas, historial);
//
//        if (historial.get(0).getPrecioAnterior() != null) {
//            historial.forEach((x) -> {
//                if (x.getMovimiento() > porcentajeMovimiento ) {
//                    try {
//                        TBS.enviarMensaje("NUEVO MOVIMIENTO: \nMoneda: " + x.getNombre() + " - Movimiento: " + x.getMovimiento() + "%");
//                    } catch (TelegramApiException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//    }

    @EventListener(ApplicationReadyEvent.class)
    public void reload() {
        logger.info(("arranca el reload"));
        Timer timer = new Timer(minutos * 60000, ae -> {
            // Busco Datos en Binance
            List<BeanMoneda> listaMonedas = null;
            try {
                listaMonedas = homeService.buscarDatos(contexto);

                historial = homeService.procesarDatos(listaMonedas, historial);

                if (historial.get(0).getPrecioAnterior() != null) {
                    historial.forEach((x) -> {
                        if (x.getMovimiento() > porcentajeMovimiento) {
                            try {
                                TBS.enviarMensaje("NUEVO REPORTE:");
                                TBS.enviarMensaje("Moneda: " + x.getNombre() + " - Movimiento: " + x.getMovimiento() + "%");
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

        });

        timer.start();
    }


    public Double getPorcentajeMovimiento() {
        return porcentajeMovimiento;
    }

    public void setPorcentajeMovimiento(Double porcentajeMovimiento) {
        this.porcentajeMovimiento = porcentajeMovimiento;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    @Override
    public String getBotUsername() {
        return "Bot Movimientos";
    }

    @Override
    public String getBotToken() {
        return "1781766783:AAH-xsHVOrN3YUiWIW2bklHem7S-XGQ_SCk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info(update.getMessage().getText());

        procesarInformacionRecibida(update.getMessage().getText());
    }

    public void enviarMensaje(String texto) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setText(texto);
        message.setChatId("865967445");
        execute(message);
    }

    private void procesarInformacionRecibida(String comando) {

        logger.info("Se ingreso el comando " + comando);

        String clave = comando.split(" ")[0];

        switch (clave) {
            case "/start":
                logger.info("Ejecutando Service de inicio");
                break;
            case "/moneda":
                logger.info(clave);

//                if(comando.split(" ")[1] == "USDT" || comando.split(" ")[1] == "BTC") {
//                    TBS.cambiarMoneda(comando.split(" ")[1]);
//                }
                break;
            case "/movimiento":
                logger.info(clave);
                contexto.setMovimiento(Double.parseDouble(comando.split(" ")[1]));
                this.porcentajeMovimiento = Double.parseDouble(comando.split(" ")[1]);
                this.setPorcentajeMovimiento(Double.parseDouble(comando.split(" ")[1]));
                try {
                    enviarMensaje("Se cambio el % de movimiento");
                } catch (TelegramApiException e) {
                    logger.error(e.getMessage());
                }
                break;
            case "/profit":
                logger.info(clave);
                break;
            default:
                logger.info("default");
                break;
        }
    }
}
