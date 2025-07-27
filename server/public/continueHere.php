<?php

use app\exceptions\UserException;
use app\exceptions\SessionTokenException;
use app\exceptions\UrlException;
use app\request\Request;
use app\database\SessionDao;
use function utility\getDecodedSession;
use function utility\getEncodedSession;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../vendor/autoload.php';

try {

    $body = Request::getBody(CONTINUE_SESSION);
    $session = getDecodedSession($body[SESSION]);
    $id_user = SessionDao::instance()->checkForIdUser($session[ID_USER])[ID_USER];
    if ($id_user != $session[ID_USER])
        throw new UserException();
    $new_session = SessionDao::instance()->updateAll($session);
    $new_session[LAST_UPDATE] = $session[LAST_UPDATE];

    print json_response([SESSION => getEncodedSession($new_session)]);

} catch (Exception $e) {
    if (
        $e instanceof SessionTokenException ||
        $e instanceof UserException
    )
        http_response_code(403);
    else if ($e instanceof UrlException)
        http_response_code(400);
    else
        http_response_code(500);
    print json_response_errors($e);
}
