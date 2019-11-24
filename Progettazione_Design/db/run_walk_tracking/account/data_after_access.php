<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");
require_once("../dao/StatisticsDao.php");
require_once("../dao/WorkoutDao.php");
require_once("../dao/SettingsDao.php");
try{

  $user = bodyRequest();

  if(!isset($user[ID_USER]))
    throw new Exception(URL_NOT_VALID);

  print json_encode(UserDao::dataAfterAccess($user));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

?>
