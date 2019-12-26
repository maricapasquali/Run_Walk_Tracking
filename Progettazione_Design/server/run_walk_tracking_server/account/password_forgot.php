<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
  $forgot_password = $_POST;
  if(!isset($forgot_password[USERNAME]) || !isset($forgot_password[PASSWORD]))
    throw new Exception(URL_NOT_VALID);
  $user = UserDao::getUserForUsername($forgot_password[USERNAME]);
  $success = UserDao::changePassword($forgot_password[PASSWORD], $user[ID_USER]);
  print json_encode(array("success"=>$success));
}catch(Exception $e){
  print json_errors($e->getMessage());
}

 ?>
