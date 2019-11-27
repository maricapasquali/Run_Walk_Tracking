<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
  $change_password = bodyRequest();

  if(!isset($change_password[ID_USER]) || !isset($change_password[PASSWORD]))
    throw new Exception(URL_NOT_VALID);
  $success = UserDao::changePassword($change_password[ID_USER], $change_password[PASSWORD]);
  print json_encode(array(UPDATE=>$success));
}catch(Exception $e){
  print json_errors($e->getMessage());
}

 ?>
