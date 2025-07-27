<?php

use app\exceptions\PasswordException;
use app\exceptions\SessionTokenException;
use app\exceptions\UrlException;
use app\exceptions\UserException;
use app\request\Request;
use app\database\UserDao;
use app\database\SessionDao;
use function utility\getEncodedSession;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../../vendor/autoload.php';

try {

    $userCredentials = Request::getBody(SIGN_IN);
    $id_user = UserDao::instance()->checkSignIn($userCredentials[USERNAME], $userCredentials[PASSWORD]);
    $session = SessionDao::instance()->checkForIdUser($id_user);
    $session[DEVICE] = $userCredentials[DEVICE];
    $session = SessionDao::instance()->updateAll($session);
    print json_response([SESSION => getEncodedSession($session)]);

} catch (SessionTokenException $se) {
    //FIRST_LOGIN
    http_response_code(403);
    print json_response([FIRST_LOGIN => $session == NULL]);
} catch (Exception $e) {
    if ($e instanceof UserException || $e instanceof PasswordException)
        http_response_code(401);
    else if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
