<?php

use app\exceptions\PasswordException;
use app\exceptions\SignUpTokenException;
use app\exceptions\UrlException;
use app\exceptions\UserConfirmException;
use app\exceptions\UserException;
use app\request\Request;
use app\database\SessionDao;
use app\database\UserDao;
use function utility\getEncodedSession;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../../vendor/autoload.php';

try {
    $userCredentials = Request::getBody(FIRST_LOGIN);

    $id_user = UserDao::instance()->checkSignUp($userCredentials);

    $session = SessionDao::instance()->create($id_user);
    $session[DEVICE] = $userCredentials[DEVICE];
    print json_response([
        SESSION => getEncodedSession($session),
        DATA => UserDao::instance()->allData($session[TOKEN], $session[DEVICE])
    ]);
} catch (Exception $e) {
    if ($e instanceof UserConfirmException)
        http_response_code(409);
    else if ($e instanceof UserException || $e instanceof PasswordException || $e instanceof SignUpTokenException)
        http_response_code(401);
    else if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
