<?php

use app\exceptions\DataException;
use app\exceptions\FilterException;
use app\exceptions\SessionTokenException;
use app\exceptions\UrlException;
use app\request\Request;
use app\database\SessionDao;
use app\database\WeightDao;
use app\database\WorkoutDao;
use function utility\json_response;
use function utility\json_response_errors;

require __DIR__ . '/../vendor/autoload.php';

try {
  /*
    Body examples:
    {
      "token": "3AnDPoEE0ncGOKqlq5saP9ptjHhEVJZ1qSqEk4RXaQTIDqbyMq6B0Dnp6WMTIKhbjGbg5Ug2SuYtIync515cZRutGCrxSDAPlqaW",
      "last_update": 1212121,
      "filter": "workout",
      "data":{
        "id_workout": 1,
        "date":"{% now 'millis', '12 25 2019' %}",
        "duration": 1500,
        "sport": "RUN"
      }
    }
    or  
    {
      "token": "3AnDPoEE0ncGOKqlq5saP9ptjHhEVJZ1qSqEk4RXaQTIDqbyMq6B0Dnp6WMTIKhbjGbg5Ug2SuYtIync515cZRutGCrxSDAPlqaW",
      "last_update": 1212121,
      "filter": "weight",
      "data":{
        "id_weight": 23,
        "date":"2019-12-29",
        "value": 50.0
      }
    }
  */

  $body = Request::getBody(INSERT);

  $id_user = SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];

  $data = $body[DATA];
  $data[ID_USER] = $id_user;

  switch ($body[FILTER]) {
    case WORKOUT:
      $required = [ID_USER, ID_WORKOUT, DATE, SPORT, DURATION];
      foreach ($required as $field) {
        if (!isset($data[$field])) {
          throw new DataException(WORKOUT);
        }
      }
      $insert = WorkoutDao::instance()->create($data);  //ok

      break;
    case WEIGHT:
      $required = [ID_WEIGHT, DATE, VALUE];
      foreach ($required as $field) {
        if (!isset($data[$field])) {
          throw new DataException(WEIGHT);
        }
      }
      $insert = WeightDao::instance()->create($data);  //ok

      break;
    default:
      throw new FilterException();
  }

  print json_response([INSERT => ($insert && SessionDao::instance()->setLastUpdate($body[LAST_UPDATE], $id_user))]);

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
