<?php

use app\exceptions\DataException;
use app\exceptions\SessionTokenException;
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
  $body = Request::getBody(UPDATE_ALL);

  $session = SessionDao::instance()->checkForToken($body[TOKEN]);

  $id_user = $session[ID_USER];


  $data = $body[DATA];

  $user = $data[USER];
  $settings = $data[SETTINGS];
  $workouts = $data[WORKOUTS];
  $weights = $data[WEIGHTS];

  if (array_key_exists(ID_USER, $user) && $user[ID_USER] != $id_user)
    throw new SessionTokenException();

  $isValidUser = isset($user[NAME]) &&
    isset($user[LAST_NAME]) &&
    isset($user[GENDER]) &&
    isset($user[BIRTH_DATE]) &&
    isset($user[EMAIL]) &&
    isset($user[CITY]) &&
    isset($user[PHONE]) &&
    isset($user[HEIGHT]) &&
    array_key_exists(IMG, $user);
  if (!$isValidUser)
    throw new DataException(USER);

  $isValidSettings = isset($settings[SPORT]) &&
    isset($settings[TARGET]) &&
    isset($settings[UNIT_MEASURE]) &&
    isset($settings[UNIT_MEASURE][HEIGHT]) &&
    isset($settings[UNIT_MEASURE][WEIGHT]) &&
    isset($settings[UNIT_MEASURE][DISTANCE]);

  if (!$isValidSettings)
    throw new DataException(SETTINGS);

  foreach ($workouts as $workout) {
    $isValidWorkout = isset($workout[ID_WORKOUT]) &&
      isset($workout[DATE]) &&
      isset($workout[DURATION]) &&
      isset($workout[SPORT]);
    if (!$isValidWorkout)
      throw new DataException(WORKOUT);
  }

  foreach ($weights as $weight) {
    $isValidWeight = isset($weight[ID_WEIGHT]) &&
      isset($weight[DATE]) &&
      isset($weight[VALUE]);
    if (!$isValidWeight)
      throw new DataException(WEIGHT);
  }

  /// UPDATER ALL DATA

  // UPDATE USER
  $updateUser = UserDao::instance()->update($user, $id_user);

  // UPDATE SETTINGS
  $updateSport = SettingsDao::instance()->updateSportFor($settings[SPORT], $id_user);
  $updateTarget = SettingsDao::instance()->updateTargetFor($settings[TARGET], $id_user);
  $updateUnits = SettingsDao::instance()->updateUnits($settings[UNIT_MEASURE], $id_user);

  // UPDATE WORKOUTS
  $updateAllWorkout = WorkoutDao::instance()->updateAll($data[WORKOUTS], $id_user);

  // UPDATE WEIGHTS
  $updateAllWeight = WeightDao::instance()->updateAll($data[WEIGHTS], $id_user);

  $updateAll = $updateUser ||
    $updateSport ||
    $updateTarget ||
    $updateUnits ||
    $updateAllWorkout ||
    $updateAllWeight;

  print json_response([UPDATE => $updateAll && SessionDao::instance()->setLastUpdate($body[LAST_UPDATE], $id_user)]);

} catch (Exception $e) {
  if ($e instanceof SessionTokenException)
    http_response_code(403);
  else if (
    $e instanceof UrlException ||
    $e instanceof DataException
  )
    http_response_code(400);
  else
    http_response_code(500);
  print json_response_errors($e);
}
