<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
  $changed_password = bodyRequest();
  if(!isset($changed_password[ID_USER]) || !isset($changed_password[PASSWORD]))
  throw new Exception(URL_NOT_VALID);

  print json_encode(array(UPDATE =>UserDao::changePassword($changed_password[ID_USER], $changed_password[PASSWORD])));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

 ?>
