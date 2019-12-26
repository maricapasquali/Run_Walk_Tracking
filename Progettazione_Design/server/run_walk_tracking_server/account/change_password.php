<?php
require_once("../utility.php");
require_once("../dao/SessionDao.php");
require_once("../dao/UserDao.php");

try{
  $changed_password = bodyRequest();
  if(!isset($changed_password[TOKEN]) || !isset($changed_password[PASSWORD]))
  throw new Exception(URL_NOT_VALID);

  $id_user = SessionDao::getForToken($changed_password[TOKEN])[ID_USER];
  if($id_user==NULL) throw new Exception(SESSION_TOKEN_NOT_VALID);

  print json_encode(array(UPDATE =>UserDao::changePassword( $changed_password[PASSWORD], $id_user)));

}catch(Exception $e){
  print json_errors($e->getMessage());
}

 ?>
