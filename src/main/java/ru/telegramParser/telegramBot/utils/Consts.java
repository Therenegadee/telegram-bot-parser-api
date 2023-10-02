package ru.telegramParser.telegramBot.utils;

public class Consts {
    // CHECKING AUTHENTICATION COMMANDS
    public static final String NON_REGISTERED_REQUEST = "Для начала работы с парсером необходимо зарегистрироваться или войти в аккаунт!" +
            "Введите /register или /login";
    public static final String NON_LOGGED_IN_REQUEST = "Для начала работы с парсером необходимо зарегистрироваться или войти в аккаунт!" +
            "Введите /register или /login";
    public static final String ALREADY_LOGGED_IN_REQUEST = "Вы уже авторизованы!";
    public static final String ALREADY_REGISTERED_REQUEST = "Вы уже зарегистрированы! " +
            "Войдите в свой аккаунт с помощью /login";

    // ERROR COMMANDS
    public static final String ERROR = "Внутренняя ошибка";
    public static final String UNKNOWN_COMMAND = "Извините, я не знаю такой команды";
    public static final String CANT_UNDERSTAND = "Извините, но я понимаю только команды :(" +
            "Чтобы узнать список доступных комманд введите /help";

}
