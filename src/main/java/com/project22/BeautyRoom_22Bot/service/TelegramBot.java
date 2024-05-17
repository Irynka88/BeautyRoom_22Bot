package com.project22.BeautyRoom_22Bot.service;


import com.project22.BeautyRoom_22Bot.config.BotConfig;
import com.project22.BeautyRoom_22Bot.model.User;
import com.project22.BeautyRoom_22Bot.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;


    final BotConfig config;

    static final String HELP_TEXT = "Цей бот створений для запису на послуги нашого салону,\n"+
            "а також для перегляду додаткової інфрмації про салон.\n\n"+
            "Можна користуватися командами з меню\n\n"+
            "/start - розпочати\n\n"+
//            "/myData - отримати особисту інформацію\n\n"+
//            "/deleteData - видалити особисту інформацію \n\n"+
            "/help - побачити це повідомлення знову\n\n"+
            "/register";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String BROW_BUTTON = "BROW_BUTTON";
    static final String LASH_BUTTON = "LASH_BUTTON";
    static final String MU_BUTTON = "MU_BUTTON";




    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Розпочати чат"));
//        listOfCommands.add(new BotCommand("/myData","Отримати інформацію профілю"));
//        listOfCommands.add(new BotCommand("/deleteData","Видалити інформацію профілю"));
        listOfCommands.add(new BotCommand("/help","Інформація з використання боту"));
        listOfCommands.add(new BotCommand("/register","Зареєструватися"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));

        }

        catch (TelegramApiException e){

//            log.error("Error setting bot`s command list: " + e.getMessage());

        }

    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();


            switch (messageText){
                case "/start":

                    registerUser(update.getMessage());

                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/help":

                    sendMessage(chatId, HELP_TEXT);
                    break;

                case "/register":

                    register(chatId);
                    break;

                default:sendMessage(chatId, "Вибачте , команди не існує");

            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)){

                String text = EmojiParser.parseToUnicode ("Вітаю , ви зареєструвалися! \n" +
                "Оберіть потрібну процедуру :cherry_blossom: ");
                executeEditMassageText(text , chatId , messageId);

//                if (update.hasCallbackQuery()){
//                    String callBackData = update.getCallbackQuery().getData();
//                    long msgId = update.getCallbackQuery().getMessage().getMessageId();
//                    long chtId = update.getCallbackQuery().getMessage().getChatId();
//
//                    if (callBackData.equals(BROW_BUTTON)){
//
//                        String text1 = ("Ви обрали процедуру фарбування/ламі брів");
//                        executeEditMassageText(text1, chtId, msgId);
//                    }
//
//                }







            } else if (callbackData.equals(NO_BUTTON)) {

                String text = EmojiParser.parseToUnicode ("Реєстрація скасована :x:");
                executeEditMassageText(text , chatId , messageId);


            }
        }

    }

    private void register(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(EmojiParser.parseToUnicode("Бажаєте зареєструватися? :innocent: "));

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Так");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("Ні");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            execute(message);

        }
        catch (TelegramApiException e){
//            log.error("Error occurred: " + e.getMessage());

        }


    }

    private void chooseFavor(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(EmojiParser.parseToUnicode(":arrow_down: "));
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var browButton = new InlineKeyboardButton();
        browButton.setText("Фарбування/ламі брів");
        browButton.setCallbackData(BROW_BUTTON);

        var lashButton = new InlineKeyboardButton();
        lashButton.setText("Ламі/нарощення вій");
        lashButton.setCallbackData(LASH_BUTTON);

        var muButton = new InlineKeyboardButton();
        muButton.setText("Макіяж");
        muButton.setCallbackData(MU_BUTTON);

        rowInLine.add(browButton);
        rowInLine.add(lashButton);
        rowInLine.add(muButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            execute(message);

        }
        catch (TelegramApiException e){
//            log.error("Error occurred: " + e.getMessage());

        }





    }

    private void registerUser(Message msg) {

        if (userRepository.findById(msg.getChatId()).isEmpty()){

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
//            log.info("User saved: " + user);

        }

    }

    private void startCommandReceived(long chatId , String name){

        String answer = EmojiParser.parseToUnicode("Привіт , " + name + " :blush:\n" + "Вітаю вас у салоні краси BeautyRoom :heart:");

//        String answer = "Hi , " + name + ", nice to meet you!";

//        log.info("Replied to user " + name);

        sendMessage(chatId, answer);

    }

    private void sendMessage(long chatId , String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);


//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//
//        row.add("Weather");
//        row.add("Get joke");
//
//        keyboardRows.add(row);
//
//        row = new KeyboardRow();
//
//        row.add("register");
//        row.add("setting");
//        row.add("data");
//
//        keyboardRows.add(row);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//
//        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);

        }
        catch (TelegramApiException e){
//            log.error("Error occurred: " + e.getMessage());

        }

    }


    private void executeEditMassageText(String text , long chatId , long messageId ){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);

        }
        catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());

        }
    }
}
