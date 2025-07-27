<?php

use app\exceptions\UrlException;
use app\request\Request;
use app\database\UserDao;
use app\email\Mailer;
use function utility\date_end_validity_link;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../vendor/autoload.php';

try {
    $request = Request::getBody(REQUEST_CHANGE_PASSWORD);

    $key = rand();
    $expiry_date = date_end_validity_link();
    $email = $request[EMAIL];
    $request_password_forgot = UserDao::instance()->requestForgotPassword($email, $key, $expiry_date);

    if($request_password_forgot)
        Mailer::instance()->sendRequestResetPassword($email, $key);

    print json_response(
        [
            EMAIL => $email,
            KEY => $key,
            EXPIRY_DATE => $expiry_date,
            REQUEST_PASSWORD_FORGOT_SEND => $request_password_forgot
        ]
    );
} catch (Exception $e) {
    if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
