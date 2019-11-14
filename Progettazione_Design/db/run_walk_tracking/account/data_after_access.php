<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");
require_once("../dao/SettingsDao.php");
try{
  $user = bodyRequest();
  if(!isset($user[ID_USER]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(USER => $user+UserDao::getImageProfileForIdUser($user[ID_USER]),
                               APP => array(SETTINGS => SettingsDao::getSettingsFor($user[ID_USER]))));

}catch(Exception $e){
  print json_errors($e->getMessage());
}


 ?>
