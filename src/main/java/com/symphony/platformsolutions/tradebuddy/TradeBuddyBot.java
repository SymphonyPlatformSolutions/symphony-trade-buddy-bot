package com.symphony.platformsolutions.tradebuddy;

import clients.SymBotClient;
import com.symphony.platformsolutions.tradebuddy.bot.IMListenerImpl;
import com.symphony.platformsolutions.tradebuddy.bot.RoomListenerImpl;
import configuration.SymConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@SpringBootApplication
public class TradeBuddyBot {
    private static SymBotClient botClient;

    public static void main(String [] args) {
        SpringApplication.run(TradeBuddyBot.class, args);
    }

    public TradeBuddyBot(IMListenerImpl imListener, RoomListenerImpl roomListener) throws Exception {
        botClient = SymBotClient.initBotRsa("config.json");
        botClient.getDatafeedEventsService().addListeners(imListener, roomListener);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/bundle.json").allowedOrigins("*");
            }
        };
    }

    public static SymBotClient getBotClient() {
        return botClient;
    }

    public static SymConfig getConfig() {
        return botClient.getConfig();
    }
}
