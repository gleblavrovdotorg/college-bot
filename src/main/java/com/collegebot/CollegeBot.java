package com.collegebot;
import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CollegeBot extends TelegramLongPollingBot {
    
    private final Map<Integer, String> textMessagesStorage = new HashMap<>();
    private final Map<Integer, MediaMessage> mediaMessagesStorage = new HashMap<>();
    private final AtomicInteger messageCounter = new AtomicInteger(1);
    
    private static final String CHANNEL_ID = "@KRD_TEC_LUV";
    private static final List<Long> ADMIN_ID = Arrays.asList(
    7709348924L, // твой ID
    8097728191L // второй админ
    );
    
    String channelID = "@KRD\\_TEC\\_LUV";

    private static class MediaMessage {
        String fileId;
        String caption;
        String type; // "photo", "video", "video_note", "sticker", "animation"
        
        MediaMessage(String fileId, String caption, String type) {
            this.fileId = fileId;
            this.caption = caption;
            this.type = type;
        }
    }
    
    @Override
    public String getBotUsername() {
        return "KRDTEC_BOT";
    }
    
    @Override
    public String getBotToken() {
        return "8495675368:AAHfXb9ShfAuKk8EZL1YGSV7G4tJVS1rexw";
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            Long userId = message.getFrom().getId();
            
            if (message.hasText()) {
                String text = message.getText();
                
                if (text.equals("/start")) {
                    sendWelcomeMessage(chatId.toString());
                    return;
                }
                
                if (text.equals("/help")) {
                    sendHelpMessage(chatId.toString());
                    return;
                }
                
                if (text.startsWith("/post") && userId.equals(ADMIN_ID)) {
                    handlePostCommand(chatId.toString(), text);
                    return;
                }
                
                if (text.equals("/list") && userId.equals(ADMIN_ID)) {
                    handleListCommand(chatId.toString());
                    return;
                }
                
                handleTextMessage(chatId.toString(), text, userId);
            }
            else if (message.hasPhoto()) {
                handlePhotoMessage(chatId.toString(), message, userId);
            }
            else if (message.hasVideo()) {
                handleVideoMessage(chatId.toString(), message, userId);
            }
            else if (message.hasVideoNote()) {
                handleVideoNoteMessage(chatId.toString(), message, userId);
            }
            else if (message.hasSticker()) {
                handleStickerMessage(chatId.toString(), message, userId);
            }
            else if (message.hasAnimation()) {
                handleAnimationMessage(chatId.toString(), message, userId);
            }
        }
    }
    
    private void handleTextMessage(String chatId, String text, Long userId) {
        int messageId = messageCounter.getAndIncrement();
        textMessagesStorage.put(messageId, text);
        
        String userResponse = "✅ Ваше текстовое сообщение #" + messageId + " принято!\n\n" +
                            "📝 Текст: " + (text.length() > 100 ? text.substring(0, 100) + "..." : text) + "\n\n" +
                            "⏳ Ожидайте модерации!";
        sendTextMessage(chatId, userResponse);
        
        String adminMessage = "📨 **Новое ТЕКСТОВОЕ сообщение**\n\n" +
                             "🆔 ID: #" + messageId + " (ТЕКСТ)\n" +
                             "👤 От: " + userId + "\n\n" +
                             "📝 Текст:\n" + text + "\n\n" +
                             "📤 Для публикации: /post " + messageId;
        sendTextMessage(ADMIN_ID.toString(), adminMessage);
    }
    
    private void handlePhotoMessage(String chatId, Message message, Long userId) {
        List<PhotoSize> photos = message.getPhoto();
        PhotoSize largestPhoto = photos.get(photos.size() - 1);
        String fileId = largestPhoto.getFileId();
        String caption = message.getCaption();
        
        int messageId = messageCounter.getAndIncrement();
        mediaMessagesStorage.put(messageId, new MediaMessage(fileId, caption, "photo"));
        
        String userResponse = "✅ Ваше фото #" + messageId + " принято!\n\n" +
                            (caption != null ? "📝 Подпись: " + caption + "\n\n" : "") +
                            "⏳ Ожидайте модерации!";
        sendTextMessage(chatId, userResponse);
        
        String adminMessage = "🖼 **Новое ФОТО сообщение**\n\n" +
                             "🆔 ID: #" + messageId + " (ФОТО)\n" +
                             "👤 От: " + userId + "\n\n" +
                             (caption != null ? "📝 Подпись: " + caption + "\n\n" : "📝 Без подписи\n\n") +
                             "📤 Для публикации: /post " + messageId;
        sendTextMessage(ADMIN_ID.toString(), adminMessage);
    }
    
    private void handleVideoMessage(String chatId, Message message, Long userId) {
        Video video = message.getVideo();
        String fileId = video.getFileId();
        String caption = message.getCaption();
        
        int messageId = messageCounter.getAndIncrement();
        mediaMessagesStorage.put(messageId, new MediaMessage(fileId, caption, "video"));
        
        String userResponse = "✅ Ваше видео #" + messageId + " принято!\n\n" +
                            (caption != null ? "📝 Подпись: " + caption + "\n\n" : "") +
                            "⏳ Ожидайте модерации!";
        sendTextMessage(chatId, userResponse);
        
        String adminMessage = "🎥 **Новое ВИДЕО сообщение**\n\n" +
                             "🆔 ID: #" + messageId + " (ВИДЕО)\n" +
                             "👤 От: " + userId + "\n\n" +
                             (caption != null ? "📝 Подпись: " + caption + "\n\n" : "📝 Без подписи\n\n") +
                             "📤 Для публикации: /post " + messageId;
        sendTextMessage(ADMIN_ID.toString(), adminMessage);
    }
    
    private void handleVideoNoteMessage(String chatId, Message message, Long userId) {
        String fileId = message.getVideoNote().getFileId();
        
        int messageId = messageCounter.getAndIncrement();
        mediaMessagesStorage.put(messageId, new MediaMessage(fileId, null, "video_note"));
        
        String userResponse = "✅ Ваш видео-кружок #" + messageId + " принят!\n\n" +
                            "⏳ Ожидайте модерации!";
        sendTextMessage(chatId, userResponse);
        
        String adminMessage = "🎬 **Новый ВИДЕО-КРУЖОК**\n\n" +
                             "🆔 ID: #" + messageId + " (ВИДЕО-КРУЖОК)\n" +
                             "👤 От: " + userId + "\n\n" +
                             "📤 Для публикации: /post " + messageId;
        sendTextMessage(ADMIN_ID.toString(), adminMessage);
    }
    
    private void handleStickerMessage(String chatId, Message message, Long userId) {
        Sticker sticker = message.getSticker();
        String fileId = sticker.getFileId();
        String emoji = sticker.getEmoji() != null ? sticker.getEmoji() : "❓";
        
        int messageId = messageCounter.getAndIncrement();
        mediaMessagesStorage.put(messageId, new MediaMessage(fileId, emoji, "sticker"));
        
        String userResponse = "✅ Ваш стикер #" + messageId + " принят!\n\n" +
                            "😊 Эмодзи: " + emoji + "\n\n" +
                            "⏳ Ожидайте модерации!";
        sendTextMessage(chatId, userResponse);
        
        String adminMessage = "🩷 **Новый СТИКЕР**\n\n" +
                             "🆔 ID: #" + messageId + " (СТИКЕР)\n" +
                             "👤 От: " + userId + "\n\n" +
                             "😊 Эмодзи: " + emoji + "\n\n" +
                             "📤 Для публикации: /post " + messageId;
        sendTextMessage(ADMIN_ID.toString(), adminMessage);
    }
    
    private void handleAnimationMessage(String chatId, Message message, Long userId) {
        String fileId = message.getAnimation().getFileId();
        String caption = message.getCaption();
        
        int messageId = messageCounter.getAndIncrement();
        mediaMessagesStorage.put(messageId, new MediaMessage(fileId, caption, "animation"));
        
        String userResponse = "✅ Ваш GIF #" + messageId + " принят!\n\n" +
                            (caption != null ? "📝 Подпись: " + caption + "\n\n" : "") +
                            "⏳ Ожидайте модерации!";
        sendTextMessage(chatId, userResponse);
        
        String adminMessage = "🎭 **Новый GIF**\n\n" +
                             "🆔 ID: #" + messageId + " (GIF)\n" +
                             "👤 От: " + userId + "\n\n" +
                             (caption != null ? "📝 Подпись: " + caption + "\n\n" : "📝 Без подписи\n\n") +
                             "📤 Для публикации: /post " + messageId;
        sendTextMessage(ADMIN_ID.toString(), adminMessage);
    }
    
    private void handlePostCommand(String chatId, String text) {
        try {
            String[] parts = text.split(" ");
            if (parts.length < 2) {
                sendTextMessage(chatId, "❌ Использование: /post <id_сообщения>\nПример: /post 1");
                return;
            }
            
            int messageId = Integer.parseInt(parts[1]);
            
            if (textMessagesStorage.containsKey(messageId)) {
                String messageText = textMessagesStorage.get(messageId);
                publishTextToChannel(messageText);
                textMessagesStorage.remove(messageId);
                sendTextMessage(chatId, "✅ Текстовое сообщение #" + messageId + " опубликовано!");
            }
            else if (mediaMessagesStorage.containsKey(messageId)) {
                MediaMessage mediaMessage = mediaMessagesStorage.get(messageId);
                
                switch (mediaMessage.type) {
                    case "photo":
                        publishPhotoToChannel(mediaMessage.fileId, mediaMessage.caption);
                        sendTextMessage(chatId, "✅ Фото #" + messageId + " опубликовано!");
                        break;
                    case "video":
                        publishVideoToChannel(mediaMessage.fileId, mediaMessage.caption);
                        sendTextMessage(chatId, "✅ Видео #" + messageId + " опубликовано!");
                        break;
                    case "video_note":
                        publishVideoNoteToChannel(mediaMessage.fileId);
                        sendTextMessage(chatId, "✅ Видео-кружок #" + messageId + " опубликован!");
                        break;
                    case "sticker":
                        publishStickerToChannel(mediaMessage.fileId);
                        sendTextMessage(chatId, "✅ Стикер #" + messageId + " опубликован!");
                        break;
                    case "animation":
                        publishAnimationToChannel(mediaMessage.fileId, mediaMessage.caption);
                        sendTextMessage(chatId, "✅ GIF #" + messageId + " опубликован!");
                        break;
                }
                mediaMessagesStorage.remove(messageId);
            }
            else {
                sendTextMessage(chatId, "❌ Сообщение с ID " + messageId + " не найдено.");
            }
            
        } catch (NumberFormatException e) {
            sendTextMessage(chatId, "❌ Неверный формат ID. Используйте число.");
        } catch (TelegramApiException e) {
            sendTextMessage(chatId, "❌ Ошибка при публикации: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void publishTextToChannel(String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(CHANNEL_ID);
        message.setText(text);
        execute(message);
    }
    
    private void publishPhotoToChannel(String fileId, String caption) throws TelegramApiException {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(CHANNEL_ID);
        photo.setPhoto(new InputFile(fileId));
        
        String finalCaption =
                            (caption != null ?  caption : "") 
                            ;
        photo.setCaption(finalCaption);
        
        execute(photo);
    }
    
    private void publishVideoToChannel(String fileId, String caption) throws TelegramApiException {
        SendVideo video = new SendVideo();
        video.setChatId(CHANNEL_ID);
        video.setVideo(new InputFile(fileId));
        
        String finalCaption = 
                            (caption != null ? ":\n\n" + caption : "");
        video.setCaption(finalCaption);
        
        execute(video);
    }
    
    private void publishStickerToChannel(String fileId) throws TelegramApiException {
        SendSticker sticker = new SendSticker();
        sticker.setChatId(CHANNEL_ID);
        sticker.setSticker(new InputFile(fileId));
        
        execute(sticker);
    }
    
    private void publishAnimationToChannel(String fileId, String caption) throws TelegramApiException {
        SendAnimation animation = new SendAnimation();
        animation.setChatId(CHANNEL_ID);
        animation.setAnimation(new InputFile(fileId));
        
        String finalCaption = 
                            (caption != null ? ":\n\n" + caption : "")
                            ;
        animation.setCaption(finalCaption);
        
        execute(animation);
    }
    
    private void publishVideoNoteToChannel(String fileId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(CHANNEL_ID);
        message.setText("🎬 Анонимный видео-кружок\n\n" +
                       "К сожалению, видео-кружки нельзя отправить в канал.\n" +
                       "Но вы можете создать обычное видео и отправить его!\n\n" +
                       "#видеокружок #неподдерживается");
        execute(message);
    }
    
    private void handleListCommand(String chatId) {
        StringBuilder list = new StringBuilder("📋 Сообщения для модерации:\n\n");
        
        for (Map.Entry<Integer, String> entry : textMessagesStorage.entrySet()) {
            String preview = entry.getValue().length() > 50 ? 
                entry.getValue().substring(0, 50) + "..." : entry.getValue();
            list.append("📝 #").append(entry.getKey()).append(" (ТЕКСТ): ").append(preview).append("\n\n");
        }
        
        for (Map.Entry<Integer, MediaMessage> entry : mediaMessagesStorage.entrySet()) {
            MediaMessage media = entry.getValue();
            String typeEmoji = "";
            String typeName = "";
            
            switch (media.type) {
                case "photo":
                    typeEmoji = "🖼";
                    typeName = "ФОТО";
                    break;
                case "video":
                    typeEmoji = "🎥";
                    typeName = "ВИДЕО";
                    break;
                case "video_note":
                    typeEmoji = "🎬";
                    typeName = "ВИДЕО-КРУЖОК";
                    break;
                case "sticker":
                    typeEmoji = "🩷";
                    typeName = "СТИКЕР";
                    break;
                case "animation":
                    typeEmoji = "🎭";
                    typeName = "GIF";
                    break;
            }
            
            String captionPreview = media.caption != null && media.caption.length() > 30 ? 
                media.caption.substring(0, 30) + "..." : 
                (media.caption != null ? media.caption : "без подписи");
            
            list.append(typeEmoji).append(" #").append(entry.getKey())
                .append(" (").append(typeName).append("): ").append(captionPreview).append("\n\n");
        }
        
        if (textMessagesStorage.isEmpty() && mediaMessagesStorage.isEmpty()) {
            sendTextMessage(chatId, "📭 Нет сообщений для модерации.");
        } else {
            sendTextMessage(chatId, list.toString());
        }
    }
    
    private void sendWelcomeMessage(String chatId) {
        String welcomeText = "👋 Привет! Я бот для анонимных сообщений в канал "+ channelID + "!\n\n" +
                "📨 Присылай сюда:\n" +
                "• 📝 Текстовые сообщения\n" +
                "• 🖼 Фото (с подписями или без)\n" +
                "• 🎥 Видео (с подписями или без)\n" +
                "• 🩷 Стикеры\n" +
                "• 🎭 GIF-анимации\n" +
                "• 💌 Признания, шутки, мемы\n\n" +
                "⚠️ Все сообщения публикуются после модерации.\n\n" +
                "⚠️ *! АДМИНЫ КАНАЛА НЕ НЕСУТ ОТВЕТСВЕННОСТИ ЗА ОТПРАВЛЕННЫЙ БОТОМ КОНТЕНТ !*\n\n" +
                "Используй /help для справки";
        
        sendTextMessage(chatId, welcomeText);
    }
    
    private void sendHelpMessage(String chatId) {
        String helpText = "ℹ️ *Как пользоваться ботом:*\n\n" +
                "✍️ Просто пришли сюда:\n" +
                "• Любой текст\n" +
                "• Фото или картинку\n" +
                "• Видео файл\n" +
                "• Стикер\n" +
                "• GIF\n" +
                "• Медиа с подписью\n\n" +
                "🔒 Все сообщения *анонимны*!\n\n" +
                "⏱ Если твое сообщение не появляется в течение 5 часов, считай, что оно не прошло модерацию\n\n" +
                "ℹ️ *Примечание:* Видео-кружки нельзя отправить в канал.";
        
        sendTextMessage(chatId, helpText);
    }
    
    private void sendTextMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown");
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения в чат " + chatId + ": " + e.getMessage());
        }
    }
}
