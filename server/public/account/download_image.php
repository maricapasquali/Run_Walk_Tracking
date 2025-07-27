<?php

use app\exceptions\ImageException;
use app\exceptions\SessionTokenException;
use app\exceptions\UrlException;
use app\request\Request;
use app\database\SessionDao;
use app\database\UserDao;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../../vendor/autoload.php';

try {
    $body = Request::getBody(DOWNLOAD_IMAGE);

    $id_user = SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];

    print json_response(UserDao::instance()->getImageProfileForIdUserAndName($id_user, $body[IMG]));
} catch (Exception $e) {
    if ($e instanceof ImageException)
        http_response_code(404);
    else if ($e instanceof SessionTokenException)
        http_response_code(403);
    else if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
