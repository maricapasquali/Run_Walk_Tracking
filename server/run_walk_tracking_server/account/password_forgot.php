<?php
require_once("../utility.php");
require_once("../dao/UserDao.php");

try{
  $forgot_password = $_POST;
  if(!isset($forgot_password[USERNAME]) || !isset($forgot_password[PASSWORD]))
    throw new UrlExeception();
  $user = UserDao::instance()->getUserForUsername($forgot_password[USERNAME]);
  $success = UserDao::instance()->changePassword($forgot_password[PASSWORD], $user[ID_USER]);
  print json_encode(array(SUCCESS=>$success));
}catch(Exception $e){
  print json_errors($e);
}

 ?>
