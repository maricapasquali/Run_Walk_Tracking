<?php
require_once("utility.php");
require_once("dao/SessionDao.php");
require_once("dao/UserDao.php");
require_once("dao/SettingsDao.php");
require_once("dao/WorkoutDao.php");
require_once("dao/WeightDao.php");
require_once("exception/Exceptions.php");
try {
  $body = bodyRequest();

  if(!isset($body[TOKEN]) || !isset($body[LAST_UPDATE]) )
      throw new UrlException();

  $session = SessionDao::checkForToken($body[TOKEN]);

  $data = $body[DATA];
  if(!isset($data))  throw new DataExeception(DATA);
// UPDATE USER
  $user = $data[USER];
  if(!isset($user[NAME]) ||
     !isset($user[LAST_NAME]) ||
     !isset($user[GENDER]) ||
     !isset($user[BIRTH_DATE]) ||
     !isset($user[EMAIL]) ||
     !isset($user[PHONE]) ||
     !isset($user[CITY]) ||
     !isset($user[HEIGHT]))
      throw new DataExeception(USER);
  $updateUser = UserDao::update($user, $session[ID_USER]);

  // UPDATE SETTINGS
  $settings = $data[SETTINGS];
  if(!isset($settings[SPORT]) ||
     !isset($settings[TARGET]) ||
     !isset($settings[UNIT_MEASURE]) ||
     !isset($settings[UNIT_MEASURE][HEIGHT]) ||
     !isset($settings[UNIT_MEASURE][WEIGHT]) ||
     !isset($settings[UNIT_MEASURE][DISTANCE]) )
      throw new DataExeception(SETTINGS);

  $updateSport = SettingsDao::updateSportFor($settings[SPORT], $session[ID_USER]);
  $updateTarget = SettingsDao::updateTargetFor($settings[TARGET], $session[ID_USER]);
  $updateUnits = SettingsDao::updateUnits( $settings[UNIT_MEASURE], $session[ID_USER]);

  foreach ($data[WORKOUTS] as $workout) {
    if(!isset($workout[ID_WORKOUT]) ||
       !isset($workout[DATE]) ||
       !isset($workout[DURATION]) ||
       !isset($workout[SPORT])
       )
        throw new DataExeception(WORKOUT);
  }

  // UPDATE WORKOUTS
  $updateAllWorkout = WorkoutDao::updateAll($data[WORKOUTS], $session[ID_USER]);

  foreach ($data[WEIGHTS] as $weight) {
    if(!isset($weight[ID_WEIGHT]) ||
       !isset($weight[DATE]) ||
       !isset($weight[VALUE]))
        throw new DataExeception(WEIGHT);
  }

  // UPDATE WEIGHTS
  $updateAllWeight = WeightDao::updateAll($data[WEIGHTS], $session[ID_USER]);

  $updateAll = $updateUser ||
               $updateSport || $updateTarget || $updateUnits ||
               $updateAllWorkout ||
               $updateAllWeight;

  print json_encode(array(UPDATE => $updateAll ? SessionDao::setLastUpdate($body[LAST_UPDATE], $session[ID_USER]): false));

} catch (Exception $e) {
  print json_errors($e);
}


?>
