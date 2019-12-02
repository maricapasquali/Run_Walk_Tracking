<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
require_once("../dao/StatisticsDao.php");
require_once("../dao/WorkoutDao.php");
try{
  $code = bodyRequest();
  if(!isset($code[ID_USER]) || !isset($code[TOKEN])) throw new Exception(URL_NOT_VALID);

   UserDao::checkToken($code[ID_USER], $code[TOKEN]);

   print json_encode(UserDao::dataAfterAccess(array(ID_USER =>$code[ID_USER])));

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
      "language": "Inglese",
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
