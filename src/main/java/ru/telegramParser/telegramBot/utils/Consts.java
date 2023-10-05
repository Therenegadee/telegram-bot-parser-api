package ru.telegramParser.telegramBot.utils;

public class Consts {

    public static final String PROCESSING_REQUEST_NOW = "Подождите! Я обрабатываю Вашу предыдущую команду!";

    // CHECKING AUTHENTICATION COMMANDS
    public static final String NON_REGISTERED_REQUEST = "Для начала работы с парсером необходимо зарегистрироваться или войти в аккаунт!\n" +
            "Введите /register или /login";
    public static final String NON_LOGGED_IN_REQUEST = "Для начала работы с парсером необходимо зарегистрироваться или войти в аккаунт!\n" +
            "Введите /register или /login";
    public static final String ALREADY_LOGGED_IN_REQUEST = "Вы уже авторизованы!";
    public static final String ALREADY_REGISTERED_REQUEST = "Вы уже зарегистрированы! " +
            "Войдите в свой аккаунт с помощью /login";

    // ERROR COMMANDS
    public static final String ERROR = "Внутренняя ошибка";
    public static final String UNKNOWN_COMMAND = "Извините, я не знаю такой команды\n" +
            "Чтобы узнать список доступных комманд введите /help";
    public static final String CANT_UNDERSTAND = "Извините, но я понимаю только команды :(\n" +
            "Чтобы узнать список доступных комманд введите /help";

    // AUTH MESSAGES
    public static final String INPUT_USERNAME = "Введите имя пользователя";
    public static final String INPUT_PASSWORD = "Введите пароль";
    public static final String INPUT_EMAIL= "Введите Email";
    // REGISTER MESSAGES
    public static final String CONTAINS_CYRILLIC_SYMBOLS = "Логин не может содержать символы кириллицы!\n" +
            "Пожалуйста, повторите ввод логина.";
    public static final String USERNAME_IS_ALREADY_USED = "Имя пользователя уже используется!\n" +
            "Если это Ваш аккаунт, то, пожалуйста, войдите в него, используя команду /login";
    public static final String USERNAME_IS_TOO_LONG = "Введенное имя пользователя слишком длинное!\n" +
            "Введите новое, не превышающее 20 символов!";
    public static final String USERNAME_SUCCESSFULLY_SAVED = "Имя пользователя успешно сохранено!";
    public static final String EMAIL_ISNT_VALID = "Данный Email не валиден!\n" +
            "Введите корректный Email!";
    public static final String EMAIL_IS_ALREADY_USED = "Данный email уже используется!\n" +
            "Введите другой или войдите в аккаунт, используя имя пользователя и пароль, с помощью команды /login";
    public static final String EMAIL_IS_TOO_LONG = "Длина Email не должна превышать 35 символов!\n" +
            "Введите корректный Email!";
    public static final String EMAIL_SUCCESSFULLY_SAVED = "Email успешно сохранен!";
    public static final String PASSWORD_IS_EMPTY = "Пароль не может быть пустым или состоять из пробелов!\n" +
            "Введите корректный пароль!";
    public static final String PASSWORD_IS_NOT_VALID = """
            Вы ввели не корректный пароль!
            Пароль должен состоять из:
            - как минимум 8 и как максимум – 20 символов;
            - как минимум одной цифры;
            - как минимум одной строчной и одной прописной буквы;
            - как минимум одного специального символа (!@#$%&*()-+=^);
            ПАРОЛЬ НЕ ДОЛЖЕН СОДЕРЖАТЬ ПРОБЕЛОВ!
            """;
    public static final String PASSWORD_SUCCESSFULLY_SAVED = "Пароль успешно сохранен!";
    public static final String PROCESSING_REGISTER_REQUEST = "Ваш запрос на регистрацию обрабатывается!\n" +
            "Мы пришлем уведомление, когда запрос будет обработан. Ожидайте, пожалуйста";
    public static final String SUCCESSFUL_REGISTRATION = "Регистрация прошла успешно!";
    public static final String BAD_REGISTER_REQUEST = "Произошла ошибка при попытке регистрации!\n" +
            "Пожалуйста, попробуйте снова: /register";

    // LOGIN MESSAGES
    public static final String USER_DONT_EXISTS = "Пользователя с таким именем пользователя не существует!";
    public static final String INCORRECT_PASSWORD = "Введенный Вами пароль не корректен!";
    public static final String PROCESSING_LOGIN_REQUEST = "Ваш запрос на авторизацию обрабатывается!\n" +
            "Мы пришлем уведомление, когда запрос будет обработан. Ожидайте, пожалуйста";
    public static final String SUCCESSFUL_LOGIN = "Авторизация прошла успешно!";
    public static final String BAD_LOGIN_REQUEST = "Произошла ошибка при попытке авторизации!\n" +
            "Пожалуйста, попробуйте снова: /login";
}
