<?php

use app\exceptions\DataException;
use app\exceptions\FilterException;
use app\exceptions\UrlException;
use app\request\Request;
use app\database\SessionDao;
use app\database\UserDao;
use app\database\WeightDao;
use app\database\WorkoutDao;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../vendor/autoload.php';

try {
  /*
    Body example:
    {
      "token": "3AnDPoEE0ncGOKqlq5saP9ptjHhEVJZ1qSqEk4RXaQTIDqbyMq6B0Dnp6WMTIKhbjGbg5Ug2SuYtIync515cZRutGCrxSDAPlqaW",
      "last_update": 1212121,
      "filter": "workout",
      "data":{
        "id_workout": 1
      }
    }
  */

  $body = Request::getBody(DELETE);
  $id_user = SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];

  $delete = false;
  switch ($body[FILTER]) {
    case USER:
      // DELETE USER ACCOUNT
      $delete = UserDao::instance()->delete($id_user); //ok
      break;
    case WORKOUT:
      if (!isset($body[DATA]) || !isset($body[DATA][ID_WORKOUT]))
        throw new DataException(WORKOUT);

      $delete = WorkoutDao::instance()->delete($body[DATA][ID_WORKOUT]); //ok
      break;
    case WEIGHT:
      if (!isset($body[DATA]) || !isset($body[DATA][ID_WEIGHT]))
        throw new DataException(WEIGHT);

      $delete = WeightDao::instance()->delete($body[DATA][ID_WEIGHT]); //ok
      break;
    default:
      throw new FilterException();
  }

  print json_response([DELETE => ($delete && SessionDao::instance()->setLastUpdate($body[LAST_UPDATE], $id_user))]);

} catch (Exception $e) {
  if ($e instanceof SessionTokenException)
    http_response_code(403);
  else if (
    $e instanceof UrlException ||
    $e instanceof DataException ||
    $e instanceof FilterException
  )
    http_response_code(400);
  else
    http_response_code(500);
  print json_response_errors($e);
}
