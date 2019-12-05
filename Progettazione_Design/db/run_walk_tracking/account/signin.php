<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");
require_once("../dao/StatisticsDao.php");
require_once("../dao/WorkoutDao.php");
require_once("../dao/SettingsDao.php");
try{
  $userCredentials = bodyRequest();
  if(!isset($userCredentials[USERNAME]) || !isset($userCredentials[PASSWORD]))
      throw new Exception(URL_NOT_VALID);

   $user = UserDao::checkCredential($userCredentials[USERNAME], $userCredentials[PASSWORD]);
   if($user[ID_USER]==NULL) throw new Exception("USER NON TROVATO");

   // NEL CASO SIA LA PRIMA VOLTA USA TOKEN INVIATO
   $result = UserDao::checkFirstLogin($user);
   if($result)
      print json_encode(array(FIRST_LOGIN => $result==1) + array(ID_USER =>$user[ID_USER]));
   else
      print json_encode(UserDao::dataAfterAccess($user));


}catch(Exception $e){
  print json_errors($e->getMessage());
}

/*
INPUT
{
  "username": "mariabianchi$1"
  "password": "pass"
}

OUTPUT
{
  "first_login": true
  "id_user" : ....
}

o

{
  "user": {
    "id_user": "38",
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
