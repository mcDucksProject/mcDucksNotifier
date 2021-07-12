package com.cherso.cripto.controllers;

import com.cherso.cripto.beans.BeanContexto;
import com.cherso.cripto.beans.BeanMoneda;
import com.cherso.cripto.beans.HistorialMoneda;
import com.cherso.cripto.beans.Respuesta;
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

import static com.cherso.cripto.CriptoApplication.restart;

@Controller
public class NewHomeController extends TelegramLongPollingBot {

    Logger logger = LoggerFactory.getLogger(NewHomeController.class);

    @Autowired
    private NewHomeService hs;

    @Autowired
    private TelegramBotService TBS;

    List<HistorialMoneda> historial = null;

    @EventListener(ApplicationReadyEvent.class)
    public void loop() throws IOException, TelegramApiException {
        logger.info(("*** INICIO DEL LOOP ***"));

        int minutos = traerDatos().getTiempo();


//        botonesBot();
        Timer timer = new Timer((60000 * minutos), ae -> {
//        Timer timer = new Timer((1000), ae -> {

            BeanContexto contexto = null;

            try {
                contexto = traerDatos();
            } catch (IOException e) {
                logger.info("Error al traer los datos");
            }

            // Busco Datos en Binance
            List<BeanMoneda> listaMonedas = null;


            try {
                logger.info("arranco con " + contexto.getMoneda());
                listaMonedas = hs.buscarDatos(contexto);


                historial = hs.procesarDatos(listaMonedas, historial);

                if (historial.get(0).getPrecioAnterior() != null) {

                    Double mov = traerDatos().getMovimiento();

                    for (HistorialMoneda x : historial) {
                        if (x.getMovimiento() > mov) {
                            try {
                                TBS.enviarMensaje("> *" + x.getNombre() + "* - Movimiento: " + x.getMovimiento() + "%");
                                TBS.enviarMensaje("- - - - - - - - - - - ");
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }

//
//                    historial.forEach((x) -> {
//
//                    });
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
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
//    private void enviarDatos(BeanContexto contexto) throws IOException {
//
//        PrintWriter out = null;
//        String datos = contexto.getMoneda() + ";" + contexto.getTiempo() + ";" + contexto.getMovimiento() + ";;";
//        try {
//            out = new PrintWriter(new FileWriter("datos.txt"));
//            out.write(datos);
//        } catch (Exception e) {
//            logger.error("NO SE PUDO ESCRIBIR EL ARCHIVO");
//        } finally {
//            if (out != null)
//                out.close();
//        }
//
//    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void runAfterStartup() throws TelegramApiException, IOException {
//        System.out.println("Inicio el BOT");
//        enviarMensaje("BOT PREPARADO - INGRESE /start PARA INICIAR");
//        traerDatos();
//
////        HttpSession session = request.getSession();
////        session.setAttribute("CONTEXTO", new BeanContexto("USDT", 1, 000.1));
////
////        session.getAttribute("CONTEXTO");
////        BeanContexto contexto = (BeanContexto) session;
//
//        logger.info("aca");
//
//    }

    //    @RequestMapping(value = "/iniciarLoop", method = RequestMethod.GET)
//    public @ResponseBody
//    void loop(boolean b) throws InterruptedException {
//        logger.info("*** INICIAR LOOP ***");
////        if (session.getAttribute("CONTEXTO") == null) {
////            session.setAttribute("CONTEXTO", new BeanContexto("USDT", 1, 000.1));
////        }
//
////        BeanContexto contexto = (BeanContexto) session.getAttribute("CONTEXTO");
//
//        while (b) {
//            logger.info("*** SE INICIA LOOP ***");
//
//
//            Thread.sleep(2000);
//        }
//    }


//    public BeanContexto traerDatos() throws IOException {
//        BufferedReader in = null;
//        String line;
//        BeanContexto contexto = null;
//        try {
//            in = new BufferedReader(new FileReader("datos.txt"));
//
//            while ((line = in.readLine()) != null) {
//                logger.info(line);
//                contexto = new BeanContexto(line.split(";")[0], Integer.parseInt(line.split(";")[1]), Double.parseDouble(line.split(";")[2]));
//            }
//
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        } finally {
//            // cerrando los streams
//            if (in != null)
//                in.close();
//
//        }
//        return contexto;
//    }


//    public void  loopConHilo(boolean x) {
//        Runnable runnable = new Runnable() {
//
//
//
//            @Override
//            public void run() {
//                // Esto se ejecuta en segundo plano una única vez
//                while (true) {
//                    // Pero usamos un truco y hacemos un ciclo infinito
//                    try {
//                        // En él, hacemos que el hilo duerma
//                        Thread.sleep(1000);
//                        // Y después realizamos las operaciones
//                        System.out.println("Me imprimo cada segundo");
//                        // Así, se da la impresión de que se ejecuta cada cierto tiempo
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//
//        // Creamos un hilo y le pasamos el runnable
//        Thread hilo = new Thread(runnable);
//        if(x == true) {
//            logger.info("" +  hilo.getId());
//            hilo.start();
//        } else {
//            hilo.stop();
//        }
//
//
//        // Y aquí podemos hacer cualquier cosa, en el hilo principal del programa
//        System.out.println("Yo imprimo en el hilo principal");
//    }

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
//        try {
        try {
            try {
                procesarInformacionRecibida(update.getMessage().getText());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void enviarMensaje(String texto) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setText(texto);
        message.setChatId("865967445");
        execute(message);

    }

//    public ReplyKeyboardMarkup botonesBot() throws TelegramApiException {
//        logger.info("BOTONES BOT");
//
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
//        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
//        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
//        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
//        inlineKeyboardButton1.setText("uno");
//        inlineKeyboardButton2.setText("dos");
//        inlineKeyboardButton1.setCallbackData("sasdasd");
//        inlineKeyboardButton2.setCallbackData("sasdasdss");
//        inlineKeyboardButtonList.add(inlineKeyboardButton1);
//        inlineKeyboardButtonList.add(inlineKeyboardButton2);
//        inlineButtons.add(inlineKeyboardButtonList);
//        inlineKeyboardMarkup.setKeyboard(inlineButtons);
//
//
//        SendMessage message = new SendMessage();
//        message.setReplyMarkup(inlineKeyboardMarkup);
//    }

    private void procesarInformacionRecibida(String comando) throws InterruptedException, TelegramApiException {

        logger.info("Se ingreso el comando " + comando);

        String clave = comando.split(" ")[0];

        switch (clave) {
            case "/start":
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
                break;
            case "/m":
                logger.info("*** Se ingreso modificador de moneda ***");
                try {
                    Respuesta res = setearMoneda(comando.split(" ")[1]);
                    if(res.getCodigo() == "10") {
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
                    if(res.getCodigo() == "10") {
                        enviarMensaje(res.getTexto());
                        loop();
                    }
                } catch (IOException e) {
                    logger.error("ERROR AL MODIFICAR TIEMPO");
                    enviarMensaje("ERROR AL MODIFICAR TIEMPO");
                }
                break;
            case "/%":
                logger.info("*** Se ingreso modificador de porcentaje ***");
                try {
                    Respuesta res = setearMovimiento(Double.parseDouble(comando.split(" ")[1]));
                    if(res.getCodigo() == "10") {
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
        BufferedReader in = null;
        String line;
        BeanContexto contexto = null;
        try {
            in = new BufferedReader(new FileReader("datos.txt"));

            while ((line = in.readLine()) != null) {
                logger.info(line);
                contexto = new BeanContexto(line.split(";")[0], Integer.parseInt(line.split(";")[1]), Double.parseDouble(line.split(";")[2]));
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            // cerrando los streams
            if (in != null)
                in.close();

        }
        return contexto;
    }



}
