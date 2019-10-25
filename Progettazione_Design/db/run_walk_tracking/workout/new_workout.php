<?php

require_once("../utility.php");
require_once("../dao/WorkoutDao.php");

try{
  $new_workout = bodyRequest();
  if(!isset($new_workout[ID_USER])||
  !isset($new_workout[DATE])||
  !isset($new_workout[ID_SPORT]) ||
  !isset($new_workout[DURATION])
  )
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(WORKOUT =>WorkoutDao::create($new_workout)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

/*
OUTPUT
"workout": {
  "id_workout": 27,
  "id_user": "38",
  "date": "2019-10-23 17:21:20",
  "id_sport": "2",
  "duration": "7200"
}

*/
?>
