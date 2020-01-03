<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");
require_once("../exception/Exceptions.php");

try{
  $changed_password = bodyRequest();
  if(!isset($changed_password[TOKEN]) ||
     !isset($changed_password[USERNAME]) ||
     !isset($changed_password[OLD_PASSWORD]) ||
     !isset($changed_password[NEW_PASSWORD]))
  throw new UrlExeception();

  $id_user_check_session = SessionDao::checkForToken($changed_password[TOKEN])[ID_USER];
  $id_user_check_signin = UserDao::checkSignIn($changed_password[USERNAME], $changed_password[OLD_PASSWORD]);

  if($id_user_check_session!=$id_user_check_signin) throw new SessionAndCredentialsException();

  print json_encode(array(UPDATE =>UserDao::changePassword($changed_password[NEW_PASSWORD], $id_user_check_signin)));

}catch(Exception $e){
  print json_errors($e);
}

 ?>
