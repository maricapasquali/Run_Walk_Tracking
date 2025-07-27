<?php

namespace app\exceptions;

use Exception;

define("CODE_PASSWORD", 5);
define("PASSWORD_NOT_CORRECT", "Password non corretta");

define("CODE_USER", 6);
define("USER_NOT_FOUND", "User non registato");

define("CODE_FILTER", 7);
define("FILTER_NOT_VALID", "Filtro non valido");

define("CODE_DATA", 8);
define("DATA_NOT_SUFFICIENT", "Dati non sufficienti");

define("CODE_URL", 9);
define("URL_NOT_VALID", "Url non valido");

define("CODE_SESSION_TOKEN", 10);
define("SESSION_TOKEN_NOT_VALID", "Token sessione non valido");

define("CODE_SIGNUP_TOKEN", 11);
define("SIGN_UP_TOKEN_NOT_VALID", "Codice di attivazione non valido");

define("CODE_USER_JUST_SIGN_UP_CONFIRM", 12);
define("USER_JUST_SIGN_UP_CONFIRM", "Conferma della registrazione già avvenuta");

define("CODE_USER_JUST_SIGN_UP", 13);
define("USER_JUST_SIGN_UP", "User già registato");
define("USERNAME_JUST_USED", "Username già utilizzato");

define("CODE_LINK_EXPIRED", 14);
define("LINK_EXPIRED", "Link for the reset you clicked is EXPIRED !! Please request a new one.");

define("SESSION_AND_CREDENTIALS", "Sessione e credenziali non coincidono");
define("CODE_SESSION_AND_CREDENTIALS", 15);

define("IMAGE_NOT_VALID", "Immagine non esiste");
define("CODE_IMAGE_NOT_VALID", 16);

class UserException extends Exception
{
    public function __construct()
    {
        parent::__construct(USER_NOT_FOUND, CODE_USER);
    }
}

class PasswordException extends Exception
{
    public function __construct()
    {
        parent::__construct(PASSWORD_NOT_CORRECT, CODE_PASSWORD);
    }
}

class UrlException extends Exception
{
    public function __construct()
    {
        parent::__construct(URL_NOT_VALID, CODE_URL);
    }
}

class SessionTokenException extends Exception
{
    public function __construct()
    {
        parent::__construct(SESSION_TOKEN_NOT_VALID, CODE_SESSION_TOKEN);
    }
}

class SignUpTokenException extends Exception
{
    public function __construct($message = null, $code = 0, Exception $previous = null)
    {
        parent::__construct(SIGN_UP_TOKEN_NOT_VALID, CODE_SIGNUP_TOKEN);
    }
}

class DataException extends Exception
{
    public function __construct($message)
    {
        parent::__construct($message . " : " . DATA_NOT_SUFFICIENT, CODE_DATA);
    }
}

class FilterException extends Exception
{
    public function __construct()
    {
        parent::__construct(FILTER_NOT_VALID, CODE_FILTER);
    }
}

class UserConfirmException extends Exception
{
    public function __construct()
    {
        parent::__construct(USER_JUST_SIGN_UP_CONFIRM, CODE_USER_JUST_SIGN_UP_CONFIRM);
    }
}

class UserJustSignUpException extends Exception
{
    public function __construct()
    {
        parent::__construct(USER_JUST_SIGN_UP, CODE_USER_JUST_SIGN_UP);
    }
}

class UserNameJustUsedException extends Exception
{
    public function __construct()
    {
        parent::__construct(USERNAME_JUST_USED, CODE_USER_JUST_SIGN_UP);
    }
}

class LinkExpiredException extends Exception
{
    public function __construct()
    {
        parent::__construct(LINK_EXPIRED, CODE_LINK_EXPIRED);
    }
}

class SessionAndCredentialsException extends Exception
{

    public function __construct()
    {
        parent::__construct(SESSION_AND_CREDENTIALS, CODE_SESSION_AND_CREDENTIALS);
    }
}

class ImageException extends Exception
{
    public function __construct()
    {
        parent::__construct(IMAGE_NOT_VALID, CODE_IMAGE_NOT_VALID);
    }
}


