package com.cherso.cripto;

import com.cherso.cripto.controllers.NewHomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
public class CriptoApplication {

    Logger logger = LoggerFactory.getLogger(CriptoApplication.class);

    public static void main(String[] args) throws TelegramApiException {

        SpringApplication.run(CriptoApplication.class, args);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(new NewHomeController());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
