<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");

try{
  $changed_password = Request::getBody(CHANGE_PASSWORD);

  $id_user_check_session = SessionDao::instance()->checkForToken($changed_password[TOKEN])[ID_USER];
  $id_user_check_signin = UserDao::instance()->checkSignIn($changed_password[USERNAME], $changed_password[OLD_PASSWORD]);

  if($id_user_check_session!=$id_user_check_signin) throw new SessionAndCredentialsException();

  print json_encode(array(UPDATE =>UserDao::instance()->changePassword($changed_password[NEW_PASSWORD], $id_user_check_signin)));

}catch(Exception $e){
  print json_errors($e);
}

 ?>
