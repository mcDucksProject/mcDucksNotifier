package com.mcDuck.Notifier.controllers;

import com.mcDuck.Notifier.services.NewHomeService;
import com.mcDuck.Notifier.services.TelegramBotService;
import com.mcDuck.Notifier.beans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.io.*;
import java.util.List;


@Controller
public class TelegramBotController {

    Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    @Autowired
    private NewHomeService homeService;

    @Autowired
    private TelegramBotService TBS;

    @Autowired
    private BeanHistorial beanHistorial;

    @EventListener(ApplicationReadyEvent.class)
    public void loop() throws IOException, TelegramApiException, InterruptedException {

        logger.info((" *** INICIO DEL LOOP *** "));

        Timer timer = new Timer((60000 * TBS.traerDatos().getTiempo()), ae -> {

            try {

                BeanContexto contexto = TBS.traerDatos();
                List<BeanMoneda> listaMonedas = homeService.buscarDatos(contexto);
                beanHistorial.ingresarDatos(listaMonedas);

                if (!beanHistorial.estaVacio()) {
                    for (HistorialMoneda x : beanHistorial.getHistorial()) {
                        if (x.getMovimiento() > TBS.traerDatos().getMovimiento() && x.getMovimiento() > 0) {
                            TBS.enviarMensaje("> *" + x.getNombre() + "* - Movimiento: " + x.getMovimiento() + "%");
                            TBS.enviarMensaje("- - - - - - - - - - - ");
                        }
                    }
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        });

        timer.start();

    }

    @RequestMapping(value = "/setMovimiento/{movimiento}", method = RequestMethod.GET)
    public @ResponseBody
    Respuesta setMovimiento(HttpServletRequest request, @PathVariable("movimiento") Double movimiento) throws Exception {

        logger.info("INGRESO CONTROLADOR SET MOVIMIENTO");

        return TBS.setearMovimiento(movimiento);

    }


    @RequestMapping(value = "/setMoneda/{moneda}", method = RequestMethod.GET)
    public @ResponseBody
    Respuesta setMoneda(HttpServletRequest request, @PathVariable("moneda") String moneda) throws Exception {

        logger.info("INGRESO CONTROLADOR SET MONEDA");

        return TBS.setearMoneda(moneda);
    }



    @RequestMapping(value = "/setTiempo/{tiempo}", method = RequestMethod.GET)
    public @ResponseBody
    Respuesta setMoneda(HttpServletRequest request, @PathVariable("tiempo") int tiempo) throws Exception {

        logger.info("INGRESO CONTROLADOR SET TIEMPO");

        return TBS.setearTiempo(tiempo);
    }


}
