package com.cherso.cripto.controllers;

import com.cherso.cripto.beans.*;
import com.cherso.cripto.services.NewHomeService;
import com.cherso.cripto.services.TelegramBotService;
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
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Controller
public class NewHomeController extends TelegramLongPollingBot {

    Logger logger = LoggerFactory.getLogger(NewHomeController.class);

    @Autowired
    private NewHomeService hs;

    @Autowired
    private TelegramBotService TBS;

    @Autowired
    private BeanHistorial beanHistorial;

    @Autowired
    Contexto contexto;

    @EventListener(ApplicationReadyEvent.class)
    public void loop() throws IOException, TelegramApiException, InterruptedException {

        logger.info(("*** INICIO DEL LOOP ***"));

        Timer timer = new Timer((60000 * traerDatos().getTiempo()), ae -> {

            try {
                BeanContexto contexto = null;
                contexto = traerDatos();
                List<BeanMoneda> listaMonedas = null;

                //Buscar cotizacion en binance
                listaMonedas = hs.buscarDatos(contexto);
                // Iniciar o actualizar el historial
                beanHistorial.ingresarDatos(listaMonedas);

                if (!beanHistorial.estaVacio()) {
                    for (HistorialMoneda x : beanHistorial.getHistorial()) {
                        if (x.getMovimiento() > traerDatos().getMovimiento() && x.getMovimiento() > 0) {
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

        return setearMovimiento(movimiento);

    }

    private Respuesta setearMovimiento(Double movimiento) throws IOException {
        BeanContexto contexto = traerDatos();

        Respuesta respuesta = new Respuesta();
        try {
            contexto.setMovimiento(movimiento);
            respuesta.setCodigo("10");
            respuesta.setTexto("Se seteo movimiento " + contexto.getMovimiento());
            enviarDatos(contexto);
        } catch (Exception e) {
            logger.info(e.getMessage());
            respuesta.setCodigo("99");
            respuesta.setTexto("Error");
        }
        return respuesta;
    }

    @RequestMapping(value = "/setMoneda/{moneda}", method = RequestMethod.GET)
    public @ResponseBody
    Respuesta setMoneda(HttpServletRequest request, @PathVariable("moneda") String moneda) throws Exception {

        logger.info("INGRESO CONTROLADOR SET MONEDA");

        return setearMoneda(moneda);
    }

    private Respuesta setearMoneda(String moneda) throws IOException {
        BeanContexto contexto = traerDatos();

        Respuesta respuesta = new Respuesta();
        try {
            contexto.setMoneda(moneda);
            respuesta.setCodigo("10");
            respuesta.setTexto("Se seteo moneda " + contexto.getMoneda());
            enviarDatos(contexto);
        } catch (Exception e) {
            logger.info(e.getMessage());
            respuesta.setCodigo("99");
            respuesta.setTexto("Error");
        }
        return respuesta;
    }

    @RequestMapping(value = "/setTiempo/{tiempo}", method = RequestMethod.GET)
    public @ResponseBody
    Respuesta setMoneda(HttpServletRequest request, @PathVariable("tiempo") int tiempo) throws Exception {

        logger.info("INGRESO CONTROLADOR SET TIEMPO");

        return setearTiempo(tiempo);
    }

    private Respuesta setearTiempo(int tiempo) throws IOException {
        BeanContexto contexto = traerDatos();

        Respuesta respuesta = new Respuesta();
        try {
            contexto.setTiempo(tiempo);
            respuesta.setCodigo("10");
            respuesta.setTexto("Se seteo tiempo " + contexto.getTiempo());
            enviarDatos(contexto);
        } catch (Exception e) {
            logger.info(e.getMessage());
            respuesta.setCodigo("99");
            respuesta.setTexto("Error");
        }
        return respuesta;
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
        try {
            try {
                procesarInformacionRecibida(update.getMessage().getText());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String texto) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setText(texto);
        message.setChatId("865967445");
        execute(message);

    }

    private void procesarInformacionRecibida(String comando) throws InterruptedException, TelegramApiException {

        logger.info("Se ingreso el comando " + comando);

        String clave = comando.split(" ")[0];

        switch (clave) {
            case "/start":
                iniciarBotonera();
                break;
            case "/m":
                logger.info("*** Se ingreso modificador de moneda ***");
                try {
                    Respuesta res = setearMoneda(comando.split(" ")[1]);
                    if (res.getCodigo() == "10") {
                        enviarMensaje(res.getTexto());
                    }
                } catch (IOException e) {
                    logger.error("ERROR AL MODIFICAR MONEDA");
                    enviarMensaje("ERROR AL MODIFICAR MONEDA");
                }
                break;
            case "/t":
                logger.info("*** Se ingreso modificador de tiempo de actualizacion ***");
                try {
                    Respuesta res = setearTiempo(Integer.parseInt(comando.split(" ")[1]));
                    if (res.getCodigo() == "10") {
                        enviarMensaje(res.getTexto());
                        loop();
                    }
                } catch (IOException e) {
                    logger.error("ERROR AL MODIFICAR TIEMPO");
                    enviarMensaje("ERROR AL MODIFICAR TIEMPO");
                }
                break;
            case "/%":
                logger.info("*** SE INGRESA AL MODIFICADOR DE % ***");
                try {
                    Respuesta res = setearMovimiento(Double.parseDouble(comando.split(" ")[1]));
                    if (res.getCodigo() == "10") {
                        enviarMensaje(res.getTexto());
                    }
                } catch (IOException e) {
                    logger.error("ERROR AL MODIFICAR PORCENTAJE");
                }
                break;
            case "/aux":
                logger.info("*** Se ingreso a consulta de Aux ***");
                try {
                    enviarMensaje(traerDatos().toString());
                } catch (IOException e) {
                    logger.error("ERROR AL MODIFICAR PORCENTAJE");
                }
                break;
            case "/help":
                logger.info("*** Se ingreso a consulta de Help ***");
                enviarMensaje("HEEEEEELP");
                break;
            default:
                logger.info("default");
                break;
        }
    }

    private void iniciarBotonera() throws TelegramApiException {
        logger.info("Ejecutando Service de inicio");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setText("Hola, aca estoy!");
        sendMessage.setChatId("865967445");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton keyboardButton1 = new KeyboardButton();
        KeyboardButton keyboardButton2 = new KeyboardButton();
        keyboardButton1.setText("/m BTC");
        keyboardButton2.setText("/m USDT");
        keyboardRow1.add(keyboardButton1);
        keyboardRow1.add(keyboardButton2);
        keyboardRowList.add(keyboardRow1);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardButton keyboardButton1_2 = new KeyboardButton();
        KeyboardButton keyboardButton2_2 = new KeyboardButton();
        KeyboardButton keyboardButton3_2 = new KeyboardButton();
        KeyboardButton keyboardButton4_2 = new KeyboardButton();
        KeyboardButton keyboardButton5_2 = new KeyboardButton();
        keyboardButton1_2.setText("/t 1");
        keyboardButton2_2.setText("/t 3");
        keyboardButton3_2.setText("/t 5");
        keyboardButton4_2.setText("/t 10");
        keyboardButton5_2.setText("/t 15");
        keyboardRow2.add(keyboardButton1_2);
        keyboardRow2.add(keyboardButton2_2);
        keyboardRow2.add(keyboardButton3_2);
        keyboardRow2.add(keyboardButton4_2);
        keyboardRow2.add(keyboardButton5_2);

        keyboardRowList.add(keyboardRow2);

        KeyboardRow keyboardRow3 = new KeyboardRow();
        KeyboardButton keyboardButton1_3 = new KeyboardButton();
        KeyboardButton keyboardButton2_3 = new KeyboardButton();
        KeyboardButton keyboardButton3_3 = new KeyboardButton();
        KeyboardButton keyboardButton4_3 = new KeyboardButton();
        KeyboardButton keyboardButton5_3 = new KeyboardButton();

        keyboardButton1_3.setText("/% 0.001");
        keyboardButton2_3.setText("/% 0.005");
        keyboardButton3_3.setText("/% 0.010");
        keyboardButton4_3.setText("/% 0.015");
        keyboardButton5_3.setText("/% 0.020");

        keyboardRow3.add(keyboardButton1_3);
        keyboardRow3.add(keyboardButton2_3);
        keyboardRow3.add(keyboardButton3_3);
        keyboardRow3.add(keyboardButton4_3);
        keyboardRow3.add(keyboardButton5_3);

        keyboardRowList.add(keyboardRow3);

        KeyboardRow keyboardRow4 = new KeyboardRow();
        KeyboardButton keyboardButton1_4 = new KeyboardButton();
        KeyboardButton keyboardButton2_4 = new KeyboardButton();

        keyboardButton1_4.setText("/help");
        keyboardButton2_4.setText("/aux");

        keyboardRow4.add(keyboardButton1_4);
        keyboardRow4.add(keyboardButton2_4);

        keyboardRowList.add(keyboardRow4);


        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        execute(sendMessage);
    }


    public void enviarDatos(BeanContexto contexto) throws IOException {

        PrintWriter out = null;
        String datos = contexto.getMoneda() + ";" + contexto.getTiempo() + ";" + contexto.getMovimiento() + ";;";
        try {
            out = new PrintWriter(new FileWriter("datos.txt"));
            out.write(datos);
        } catch (Exception e) {
            logger.error("NO SE PUDO ESCRIBIR EL ARCHIVO");
        } finally {
            if (out != null)
                out.close();
        }

    }

    public BeanContexto traerDatos() throws IOException {
        logger.info(" *** TRAER INFO *** ");
        BufferedReader in = null;
        String line;
        BeanContexto contexto = null;
        try {
            in = new BufferedReader(new FileReader("datos.txt"));

            while ((line = in.readLine()) != null) {
                logger.info(line);
//                contexto = new BeanContexto(line.split(";")[0], Integer.parseInt(line.split(";")[1]), Double.parseDouble(line.split(";")[2]), Long.parseLong(line.split(";")[3]));
                contexto = new BeanContexto(line.split(";")[0], Integer.parseInt(line.split(";")[1]), Double.parseDouble(line.split(";")[2]));
            }
            logger.info("INFO: " + contexto.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (in != null)
                in.close();
        }
        return contexto;
    }

}
