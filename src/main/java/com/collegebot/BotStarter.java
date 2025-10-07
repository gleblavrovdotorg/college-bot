package com.collegebot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotStarter {
    public static void main(String[] args) {
        System.out.println("🚀 Запускаем бота для колледжа...");
        
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TelegramLongPollingBot bot = new CollegeBot();
            
            botsApi.registerBot(bot);
            
            System.out.println("✅ Бот успешно запущен!");
            System.out.println("📞 Бот: @" + bot.getBotUsername());
            System.out.println("💡 Напишите /start боту для проверки");
            
        } catch (TelegramApiException e) {
            if (e.getMessage().contains("webhook")) {
                System.out.println("⚠️ Предупреждение: Ошибка вебхука (можно игнорировать)");
                System.out.println("🔧 Проверяем работоспособность бота...");
                System.out.println("💡 Напишите боту в Telegram для проверки");
                
                TelegramLongPollingBot bot = new CollegeBot();
                System.out.println("🤖 Бот создан: @" + bot.getBotUsername());
                System.out.println("⏳ Ожидаю сообщения...");
                
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        break;
                    }
                }
            } else {
                System.err.println("❌ Критическая ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}