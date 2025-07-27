<?php

use app\exceptions\DataException;
use app\exceptions\FilterException;
use app\exceptions\UrlException;
use app\request\Request;
use app\database\SessionDao;
use app\database\SettingsDao;
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
      "token" : "hfenogimewgowr23gèwe",
      "last_update": 21312312,
      "filter": "user",
      "data":{
        "gender": "FEMALE",
        "email" : "carlo@gmail.com"
      }
    }
  */

  $body = Request::getBody(UPDATE);
  $id_user = SessionDao::instance()->checkForToken($body[TOKEN])[ID_USER];

  $filter = $body[FILTER];
  $data = $body[DATA];
  if (count($data) == 0)
    throw new DataException($filter);


  switch ($filter) {
    case USER:
      $isValidUser = isset($data[NAME]) ||
        isset($data[LAST_NAME]) ||
        isset($data[GENDER]) ||
        isset($data[BIRTH_DATE]) ||
        isset($data[EMAIL]) ||
        isset($data[CITY]) ||
        isset($data[PHONE]) ||
        isset($data[HEIGHT]) ||
        isset($data[IMG]);
      if (!$isValidUser)
        throw new DataException(USER);
      $update = UserDao::instance()->update($data, $id_user); // ok
      break;
    case SPORT:
      if (!isset($data[VALUE]))
        throw new DataException(SPORT);
      $update = SettingsDao::instance()->updateSportFor($data[VALUE], $id_user); //ok
      break;
    case TARGET:
      if (!isset($data[VALUE]))
        throw new DataException(TARGET);
      $update = SettingsDao::instance()->updateTargetFor($data[VALUE], $id_user); //ok
      break;
    case UNIT_DISTANCE:
      if (!isset($data[VALUE]))
        throw new DataException(UNIT_DISTANCE);
      $update = SettingsDao::instance()->updateUnitDistanceFor($data[VALUE], $id_user); //ok
      break;
    case UNIT_WEIGHT:
      if (!isset($data[VALUE]))
        throw new DataException(UNIT_WEIGHT);
      $update = SettingsDao::instance()->updateUnitWeightFor($data[VALUE], $id_user); //ok
      break;
    case UNIT_HEIGHT:
      if (!isset($data[VALUE]))
        throw new DataException(UNIT_HEIGHT);
      $update = SettingsDao::instance()->updateUnitHeightFor($data[VALUE], $id_user); //ok
      break;
    case WORKOUT:
      $isValidWorkout = isset($data[ID_WORKOUT]) && (
        isset($data[DATE]) ||
        isset($data[SPORT]) ||
        isset($data[DURATION]) ||
        isset($data[DISTANCE]) ||
        isset($data[CALORIES])
      );
      if (!$isValidWorkout)
        throw new DataException(WEIGHT);
      $update = WorkoutDao::instance()->update($data, $id_user);  //ok
      break;
    case WEIGHT:
      $isValidWeight = isset($data[ID_WEIGHT]) && (
        isset($data[DATE]) ||
        isset($data[VALUE])
      );
      if (!$isValidWeight)
        throw new DataException(WEIGHT);
      $update = WeightDao::instance()->update($data, $id_user);  //ok
      break;
    default:
      throw new FilterException();
  }

  print json_response([UPDATE => ($update && SessionDao::instance()->setLastUpdate($body[LAST_UPDATE], $id_user))]);

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
