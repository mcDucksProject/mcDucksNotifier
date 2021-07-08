//package com.cherso.cripto.controllers;
//
//import com.cherso.cripto.beans.BeanContexto;
//import com.cherso.cripto.services.TelegramBotService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//public class TelegramBotController extends TelegramLongPollingBot {
//
//    Logger logger = LoggerFactory.getLogger(TelegramBotController.class);
//
//    @Autowired
//    TelegramBotService TBS;
//
//    BeanContexto contexto = new BeanContexto();
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
//        procesarInformacionRecibida(update.getMessage().getText());
//    }
//
//    public void enviarMensaje(String texto) throws TelegramApiException {
//        SendMessage message = new SendMessage();
//        message.setText(texto);
//        message.setChatId("865967445");
//        execute(message);
//    }
//
//    private void procesarInformacionRecibida(String comando) {
//
//        logger.info("Se ingreso el comando " + comando);
//
//        String clave = comando.split(" ")[0];
//
//        switch (clave) {
//            case "/start":
//                logger.info("Ejecutando Service de inicio");
//                break;
//            case "/moneda":
//                logger.info(clave);
//
//                if (comando.split(" ")[1] == "USDT" || comando.split(" ")[1] == "BTC") {
//                    TBS.cambiarMoneda(comando.split(" ")[1]);
//                }
//                break;
//            case "/movimiento":
//                logger.info(clave);
//                break;
//            case "/profit":
//                logger.info(clave);
//                break;
//            default:
//                logger.info("default");
//                break;
//        }
//    }
//
//
//}
