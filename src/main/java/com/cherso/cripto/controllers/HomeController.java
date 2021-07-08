//package com.cherso.cripto.controllers;
//
//import com.cherso.cripto.beans.BeanContexto;
//import com.cherso.cripto.beans.BeanMoneda;
//import com.cherso.cripto.services.HomeService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.web.bind.annotation.*;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.net.ProtocolException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/")
//public class HomeController extends TelegramLongPollingBot {
//
//    Logger logger = LoggerFactory.getLogger(HomeController.class);
//
//    public BeanContexto contexto = new BeanContexto("USDT", 60000, 1);
//
//    @Autowired
//    HomeService homeService;
//
//    @RequestMapping(value = "/buscarInf/{moneda}/{tiempo}/{movimiento}", method = RequestMethod.GET)
//    public @ResponseBody
//    List<BeanMoneda> hello(HttpServletRequest request,
//                           @PathVariable("moneda") String moneda,
//                           @PathVariable("tiempo") int tiempo,
//                           @PathVariable("movimiento") int movimiento) throws Exception {
//
//        logger.info("Ingreso HomeController con datos - moneda: " + moneda + " tiempo: " + tiempo + " movimiento: " + movimiento);
//
//        BeanContexto contexto = new BeanContexto(moneda, tiempo, movimiento);
//        List<BeanMoneda> listaMonedas = null;
//        try {
//            listaMonedas = homeService.buscarDatos(contexto);
//        } catch (ProtocolException e) {
//            logger.info(e.getMessage());
//        }
//
////        Timer timer = new Timer(1000, new ActionListener() {
////            public void actionPerformed(ActionEvent e) {
////                try {
////                    List<BeanMoneda> listaMonedas = homeService.buscarDatos(contexto);
////                } catch (Exception exception) {
////                    exception.printStackTrace();
////                }
////            }
////        });
////
////        timer.start();
//
//        return listaMonedas;
//    }
//
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void doSomethingAfterStartup() {
//
//        try {
//            List<BeanMoneda> lista = homeService.buscarDatos(contexto);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Timer timer = new Timer(60000, new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    List<BeanMoneda> lista = homeService.buscarDatos(contexto);
//                } catch (Exception ex) {
//                    logger.error(ex.getMessage());
//                }
//            }
//        });
//
//        timer.start();
//    }
//
//    @Override
//    public String getBotUsername() {
//        return "Bot Movimientos";
//    }
//
//    @Override
//    public String getBotToken() {
//        return "1781766783:AAH-xsHVOrN3YUiWIW2bklHem7S-XGQ_SCk";
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        logger.info(update.getMessage().getText());
//
////        procesarInformacionRecibida(update.getMessage().getText());
//    }
//
//
////    public void loop(int periodo) {
////        Timer timer = new Timer(periodo, new ActionListener() {
////            public void actionPerformed(ActionEvent e) {
////                try {
//////                  List<BeanMoneda> listaMonedas = homeService.buscarDatos(contexto);
////                    homeService.buscarDatos();
////                } catch (Exception exception) {
////                    exception.printStackTrace();
////                }
////            }
////        });
////
////        timer.start();
////    }
//
//}
