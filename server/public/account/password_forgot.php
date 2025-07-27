<?php

use app\exceptions\UrlException;
use app\database\UserDao;
use app\exceptions\UserException;
use app\request\Request;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../../vendor/autoload.php';

try {
    $body = Request::getBody(PASSWORD_FORGOT);
    $user = UserDao::instance()->getUserForUsername($body[USERNAME]);
    $success = UserDao::instance()->changePassword($body[PASSWORD], $user[ID_USER]);
    print json_response([SUCCESS => $success]);
} catch (Exception $e) {
    if ($e instanceof UserException)
        http_response_code(401);
    else if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
