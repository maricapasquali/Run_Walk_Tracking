<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
require_once("../dao/StatisticsDao.php");
require_once("../dao/WorkoutDao.php");
try{
  $userCredentials = bodyRequest();
  if(!isset($userCredentials[USERNAME]) || !isset($userCredentials[PASSWORD]) || !isset($userCredentials[TOKEN]))
  throw new Exception(URL_NOT_VALID);

   $session=UserDao::checkSignUp($userCredentials);
   if($session==NULL)  throw new Exception(SESSION_TOKEN_NOT_VALID);

   print json_encode(array(SESSION => $session, DATA =>UserDao::allData($session[TOKEN])));

 }catch(Exception $e){
  print json_errors($e->getMessage());
}

/*
INPUT
{
  "id_user": 27
  "token": "QZghiChpnA"
}

OUTPUT
{
  "Error": "TOKEN NON CORRETTO"
}

o

{
  "user": {
    "id_user": "27",
    "settings": {
      "sport": {
        "id_sport": 2,
        "name": "WALK"
      },
      "target": {
        "id_target": 2,
        "name": "LOSE_WEIGHT"
      },
      "unit_measure": {
        "energy": {
          "id_unit": 1,
          "unit": "Kcal"
        },
        "distance": {
          "id_unit": 1,
          "unit": "Km"
        },
        "weight": {
          "id_unit": 1,
          "unit": "Kg"
        },
        "height": {
          "id_unit": 1,
          "unit": "m"
        }
      }
    }
  }
}

*/


?>
