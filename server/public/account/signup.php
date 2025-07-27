<?php

use app\exceptions\UrlException;
use app\exceptions\UserJustSignUpException;
use app\exceptions\UserNameJustUsedException;
use app\request\Request;
use app\database\UserDao;
use app\email\Mailer;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../../vendor/autoload.php';

try {
    $new_user = Request::getBody(SIGN_UP);
    //print $new_user->toJson();
    $token = UserDao::instance()->create($new_user);
    $isSignup = $token != NULL;
    if($isSignup)
        Mailer::instance()->sendSignup($new_user[EMAIL], $new_user, $token);
    print json_response([SIGN_UP => $isSignup]);
} catch (Exception $e) {
    if ($e instanceof UserJustSignUpException || $e instanceof UserNameJustUsedException)
        http_response_code(409);
    else if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
