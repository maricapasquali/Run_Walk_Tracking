<?php

use app\request\Request;
use app\database\SessionDao;
use app\database\UserDao;
use function utility\getEncodedSession;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../vendor/autoload.php';

function sendData($session, $device)
{
  print json_response([
    STATE => [
      CODE => 1,
      DESCRIPTION => NO_CONSISTENT_RECEIVE_DATA,
      DATA => [SESSION => getEncodedSession($session)] +
        UserDao::instance()->allData($session[TOKEN], $device)
    ]
  ]);
}

try {
  $code = Request::getBody(SYNC);
  $session = SessionDao::instance()->checkForToken($code[TOKEN]);

  if ($code[DB_EXIST]) {
    if ($code[LAST_UPDATE] == $session[LAST_UPDATE])
      print json_response([STATE => [CODE => 0, DESCRIPTION => CONSISTENT]]);
    else if ($code[LAST_UPDATE] < $session[LAST_UPDATE])
      // client must receive data from server
      sendData($session, $code[DEVICE]);
    else
      // client must send data to server
      print json_response([STATE => [CODE => 2, DESCRIPTION => NO_CONSISTENT_SEND_DATA]]);
  } else {
    // client must receive data from server (maybe change device!!)
    sendData($session, $code[DEVICE]);
  }

} catch (Exception $e) {
  if ($e instanceof SessionTokenException)
    http_response_code(403);
  else if ($e instanceof UrlException)
    http_response_code(400);
  else
    http_response_code(500);
  print json_response_errors($e);
}
