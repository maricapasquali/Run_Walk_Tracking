<?php

use app\exceptions\PasswordException;
use app\exceptions\SessionAndCredentialsException;
use app\exceptions\SessionTokenException;
use app\exceptions\UrlException;
use app\exceptions\UserException;
use app\request\Request;
use app\database\SessionDao;
use app\database\UserDao;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../../vendor/autoload.php';

try {
    $changed_password = Request::getBody(CHANGE_PASSWORD);

    $id_user_check_session = SessionDao::instance()->checkForToken($changed_password[TOKEN])[ID_USER];
    $id_user_check_signin = UserDao::instance()->checkSignIn($changed_password[USERNAME], $changed_password[OLD_PASSWORD]);

    if ($id_user_check_session != $id_user_check_signin)
        throw new SessionAndCredentialsException();

    print json_response([UPDATE => UserDao::instance()->changePassword($changed_password[NEW_PASSWORD], $id_user_check_signin)]);

} catch (Exception $e) {
    if (
        $e instanceof SessionTokenException ||
        $e instanceof SessionAndCredentialsException ||
        $e instanceof UserException ||
        $e instanceof PasswordException
    )
        http_response_code(403);
    else if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
